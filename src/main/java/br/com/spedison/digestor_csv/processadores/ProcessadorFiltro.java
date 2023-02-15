package br.com.spedison.digestor_csv.processadores;

import br.com.spedison.digestor_csv.infra.ExecutadorComControleTempo;
import br.com.spedison.digestor_csv.infra.FileProcessamento;
import br.com.spedison.digestor_csv.infra.FileUtils;
import br.com.spedison.digestor_csv.model.EstadoProcessamentoEnum;
import br.com.spedison.digestor_csv.model.FiltroComparadorVO;
import br.com.spedison.digestor_csv.model.FiltroVO;
import br.com.spedison.digestor_csv.service.FiltroService;
import lombok.extern.log4j.Log4j2;
import org.jobrunr.jobs.context.JobContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Log4j2
@Component
public class ProcessadorFiltro extends ProcessadorBase {

    @Autowired
    private FiltroService filtroService;

    private FiltroVO filtroVO;

    @Autowired
    ExecutadorComControleTempo executaAtualizacao;


    private Boolean todasCondicoesDevemAtender = false;

    private String[] getColunasParaComparar(String[] colunas, String linhaToda, List<FiltroComparadorVO> comparadores) {
        return comparadores
                .stream()
                .map(FiltroComparadorVO::getNumeroColuna)
                .map(c -> c == -1 ? linhaToda : colunas[c])
                .toArray(String[]::new);
    }

    @Override
    protected void preInicia(Long idTarefa, JobContext jobContext) {
        filtroVO = filtroService.getFiltroComComparadores(idTarefa);
    }

    private void processaUmArquivo(FileProcessamento arquivoEntrada) {
        File arquivoSaida = defineArqivoSaida(arquivoEntrada);

        logEstado("Processando arquivo:" + arquivoEntrada.getName());

        try {
            // Abre os arquivos de entrada e saída
            BufferedReader aLeitura = FileUtils.abreArquivoLeitura(arquivoEntrada.toString(), getCharset());
            BufferedWriter aEscrita = FileUtils.abreArquivoEscrita(arquivoSaida.toString(), getCharset());

            // Lê o header do Arquivo
            arquivoEntrada.setHeader(aLeitura.readLine()); // Lê o cabeçalho
            arquivoEntrada.incLinhasProcessadas();
            // Escreve o Header na saida
            aEscrita.write(arquivoEntrada.getHeader());
            aEscrita.newLine();
            log.trace("Escrevendo Header (" + arquivoSaida.getName() + ") :: " + arquivoEntrada.getHeader());

            // Lê a primeira linha para processar
            String linha = aLeitura.readLine();
            arquivoEntrada.incLinhasProcessadas();

            while (linha != null) {
                String[] colunas = linha.split(getSeparadoresColunas());
                String[] dadosParaComparar = getColunasParaComparar(colunas, linha, filtroVO.getComparadores());
                Boolean resultado =
                        todasCondicoesDevemAtender ?
                                IntStream
                                        .range(0, dadosParaComparar.length)
                                        .allMatch(c -> filtroVO.getComparadores().get(c).compara(dadosParaComparar[c])) :
                                IntStream
                                        .range(0, dadosParaComparar.length)
                                        .anyMatch(c -> filtroVO.getComparadores().get(c).compara(dadosParaComparar[c]));

                if (resultado) {
                    aEscrita.write(linha);
                    aEscrita.newLine();
                    log.trace("Escrevendo linhas Encontradas(" + arquivoSaida.getName() + ") :: " + linha);
                } else {
                    log.trace("Não localizei os dados na linha :: " + linha);
                }

                linha = aLeitura.readLine();
                arquivoEntrada.incLinhasProcessadas();
                executaAtualizacao.executaSeTimeout(() -> {
                    long linhasProcessadas = getLinhasProcessadas();
                    filtroService.atualizaLinhasProcessadas(getIdTarefa(), linhasProcessadas);
                    log.debug("Processando %d linhas".formatted(linhasProcessadas));
                });
            }
            aEscrita.close();
            aLeitura.close();
            filtroService.atualizaLinhasProcessadas(getIdTarefa(), getLinhasProcessadas());
            log.debug("Processado arquivo %s".formatted((arquivoEntrada.getName())));
        } catch (IOException e) {
            log.error(e);
            log.error("Problemas ao processar arquivos %s e %s.".formatted(arquivoSaida.toString(), arquivoEntrada.toString()));
        }
    }

    @Override
    void terminar() {
        filtroVO = filtroService.registrarFimProcessamento(getIdTarefa(), getLinhasProcessadas());
    }

    @Override
    boolean iniciar() {
        this.filtroVO = filtroService.getFiltroComComparadores(getIdTarefa());

        if (this.filtroVO == null)
            return false;

        if (filtroVO.getEstado() != EstadoProcessamentoEnum.NAO_INICIADO)
            return false;

        todasCondicoesDevemAtender = filtroVO.getTodasCondicoesDevemAtender();
        filtroVO = filtroService.registrarInicioProcessamento(getIdTarefa(), getJobId().toString());

        return true;
    }

    @Override
    void processar() {
        logEstado("Listando arquivos diretorios");
        new File(filtroVO.getDiretorioSaida()).mkdirs();
        // -- arquivosEmProcessamento = listaArquivosParaProcessar();
        logEstado("Iniciando processamento dos arquivos");
        filtroService.registrarProcessando(getIdTarefa());
        Stream<FileProcessamento> sf = Arrays.stream(getArquivosEmProcessamento());
        if (isParalelo()) {
            sf = sf.parallel();
        }
        sf.forEach(this::processaUmArquivo);
        logEstado("Processamento terminado");
    }

    @Override
    String getDiretorioSaida() {
        return filtroVO.getDiretorioSaida();
    }

    @Override
    EstadoProcessamentoEnum getEstadoProcessamento() {
        return filtroVO.getEstado();
    }

    @Override
    String getDiretorioEntrada() {
        return filtroVO.getDiretorioEntrada();
    }
}
