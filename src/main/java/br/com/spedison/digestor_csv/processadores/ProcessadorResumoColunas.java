package br.com.spedison.digestor_csv.processadores;

import br.com.spedison.digestor_csv.infra.ExecutadorComControleTempo;
import br.com.spedison.digestor_csv.infra.FileProcessamento;
import br.com.spedison.digestor_csv.infra.FileUtils;
import br.com.spedison.digestor_csv.model.EstadoProcessamentoEnum;
import br.com.spedison.digestor_csv.model.ResumoColunasCampoVO;
import br.com.spedison.digestor_csv.model.ResumoColunasVO;
import br.com.spedison.digestor_csv.service.ResumoColunasService;
import lombok.extern.log4j.Log4j2;
import org.jobrunr.jobs.context.JobContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
@Log4j2
public class ProcessadorResumoColunas extends ProcessadorBase {

    //Lista de Mapas contendo os Handles dos arquivos abertos.
    @Autowired
    private ResumoColunasService resumeColunasService;
    private ResumoColunasVO resumeColunasVO;
    private List<Integer> colunasParaResumir;
    @Autowired
    ExecutadorComControleTempo executaAtualizacao;
    List<Map<String, List<Double>>> dadosColunaSumarizada = new LinkedList<>();

    private void addItemDadosColuna() {
        dadosColunaSumarizada.add(new HashMap<>());
    }

    static private final List<String> listaColunasResumo = List.of("Contagem", "Soma", "Média", "Mínimo", "Máximo");

    @Override
    protected void preInicia(Long idTarefa, JobContext jobContext) {
        resumeColunasVO = resumeColunasService.getResumoComCampos(idTarefa);
    }

    void processaUmArquivo(FileProcessamento arquivoEntrada) {

        Map<String, List<Double>> mapaProcessamento = dadosColunaSumarizada.get(arquivoEntrada.getNumeroArquivoProcessamento());

        try {

            BufferedReader br = FileUtils.abreArquivoLeitura(arquivoEntrada.toString(), getEncoding());

            // Pega o arquivo de acordo com a nome definido.
            BufferedWriter bw = FileUtils.abreArquivoEscrita(
                    resumeColunasVO.getDiretorioSaida(),
                    arquivoEntrada.getName(), getEncoding());

            arquivoEntrada.setHeader(br.readLine());
            arquivoEntrada.incLinhasProcessadas();

            String linha = null;
            // Lê o carquivo todo e carrega o resumo em memória.
            while ((linha = br.readLine()) != null) {
                arquivoEntrada.incLinhasProcessadas();

                var colunas = linha.split(getSeparadoresColunas());
                StringBuilder linhaParaEscrever = new StringBuilder();

                this.colunasParaResumir
                        .stream()
                        .map(i -> colunas[i] + getSeparadoresColunas())
                        .forEach(linhaParaEscrever::append);

                List<Double> listaUsada = dadosColunaSumarizada
                        .get(arquivoEntrada.getNumeroArquivoProcessamento())
                        .get(linhaParaEscrever.toString());

                if (listaUsada == null) {
                    listaUsada = new LinkedList<>();
                    mapaProcessamento.put(linhaParaEscrever.toString(), listaUsada);
                }

                listaUsada
                        .add(
                                Double.parseDouble(
                                        colunas[resumeColunasVO.getNumeroColunaSumarizada()].
                                                replaceAll("[^0-9-E.,]",
                                                        "")));
            }
            br.close();
            // Termina a leitura do mapa de processamento e fecha o arquivo

            // Trata o Header e o escreve no arquivo de saida.
            var header = new StringJoiner(getSeparadoresColunas());
            var colunas = arquivoEntrada.getHeader().split(getSeparadoresColunas());
            colunasParaResumir.stream().map(i -> colunas[i]).forEach(header::add);
            listaColunasResumo.forEach(header::add);
            bw.write(header.toString());
            bw.newLine();

            // Roda todas as chaves, calcula o resumo e grava o resumo
            for (var keys : mapaProcessamento.keySet()) {
                List<Double> dados = mapaProcessamento.get(keys);
                var ret = dados.stream().mapToDouble(Double::valueOf).summaryStatistics();
                bw.write(keys); // Grava no arquivo as chaves usadas.
                bw.write("%d%s%f%s%f%s%f%s%f".formatted(
                        ret.getCount(), getSeparadoresColunas(),
                        ret.getSum(), getSeparadoresColunas(),
                        ret.getAverage(), getSeparadoresColunas(),
                        ret.getMin(), getSeparadoresColunas(),
                        ret.getMax())); // Grava o resumo estatístico
                bw.newLine();
            }
            bw.close(); // Fecha arquivo de saida.

            resumeColunasService.atualizaLinhasProcessadas(getIdTarefa(), getLinhasProcessadas());
            log.debug("Processado arquivo %s".formatted((arquivoEntrada.getName())));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    void terminar() {
        resumeColunasService.registrarFimProcessamento(resumeColunasVO.getId(), getLinhasProcessadas());
    }

    @Override
    boolean iniciar() {

        if (resumeColunasVO.getEstado() != EstadoProcessamentoEnum.NAO_INICIADO)
            return false;

        colunasParaResumir =
                resumeColunasVO
                        .getCamposParaResumir()
                        .stream()
                        .map(ResumoColunasCampoVO::getNumeroColuna)
                        .toList();

        resumeColunasService.registrarInicioProcessamento(getIdTarefa(), getJobId().toString());

        return true;
    }

    @Override
    void processar() {

        dadosColunaSumarizada.clear();
        IntStream.range(0, getArquivosEmProcessamento().size()).forEach(x -> addItemDadosColuna());

        Stream<FileProcessamento> stream = super.getArquivosEmProcessamento().stream();

        resumeColunasService.registrarProcessando(getIdTarefa());

        (isParalelo() ?
                stream.parallel() :
                stream)
                .forEach(this::processaUmArquivo);
    }

    @Override
    String getDiretorioSaida() {
        return resumeColunasVO.getDiretorioSaida();
    }

    @Override
    EstadoProcessamentoEnum getEstadoProcessamento() {
        return resumeColunasVO.getEstado();
    }

    @Override
    String getDiretorioEntrada() {
        return resumeColunasVO.getDiretorioEntrada();
    }
}