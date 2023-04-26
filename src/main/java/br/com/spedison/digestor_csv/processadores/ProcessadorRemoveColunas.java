package br.com.spedison.digestor_csv.processadores;

import br.com.spedison.digestor_csv.infra.ExecutadorComControleTempo;
import br.com.spedison.digestor_csv.infra.FileProcessamento;
import br.com.spedison.digestor_csv.infra.FileUtils;
import br.com.spedison.digestor_csv.model.EstadoProcessamentoEnum;
import br.com.spedison.digestor_csv.model.RemoveColunasCampoVO;
import br.com.spedison.digestor_csv.model.RemoveColunasVO;
import br.com.spedison.digestor_csv.service.RemoveColunasService;
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
public class ProcessadorRemoveColunas extends ProcessadorBase {

    //Lista de Mapas contendo os Handles dos arquivos abertos.
    @Autowired
    private RemoveColunasService removeColunasService;
    private RemoveColunasVO removeColunasVO;

    private List<Integer> colunasParaRemover;

    @Autowired
    ExecutadorComControleTempo executaAtualizacao;

    @Override
    protected void preInicia(Long idTarefa, JobContext jobContext) {
        removeColunasVO = removeColunasService.getRemoveColunasComCampos(idTarefa);
    }


    void processaUmArquivo(FileProcessamento arquivoEntrada) {

        try {

            BufferedReader br = FileUtils.abreArquivoLeitura(arquivoEntrada.toString(), getEncoding());

            // Pega o arquivo de acordo com a nome definido.
            BufferedWriter bw = null;
            bw = FileUtils.abreArquivoEscrita(
                    removeColunasVO.getDiretorioSaida(),
                    arquivoEntrada.getName(), getEncoding());

            String line = null;
            while ((line = br.readLine()) != null) {
                arquivoEntrada.incLinhasProcessadas();
                String[] colunas = line.split(super.getSeparadoresColunas());
                StringJoiner linhaProcessada = new StringJoiner(getSeparadoresColunas());
                final int[] posColuna = {0};
                IntStream.range(0, colunas.length)
                        .filter(i -> !colunasParaRemover.contains(i))
                        .mapToObj(i -> colunas[i])
                        .forEach(linhaProcessada::add);
                bw.write(linhaProcessada.toString());
                bw.newLine();
                executaAtualizacao.executaSeTimeout(() -> {
                    removeColunasService.atualizaLinhasProcessadas(removeColunasVO.getId(), getLinhasProcessadas());
                });
            }
            br.close();
            bw.close();
            removeColunasService.atualizaLinhasProcessadas(getIdTarefa(), getLinhasProcessadas());
            log.debug("Processado arquivo %s".formatted((arquivoEntrada.getName())));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    void terminar() {
        removeColunasService.registrarFimProcessamento(removeColunasVO.getId(), getLinhasProcessadas());
    }

    @Override
    boolean iniciar() {

        if (removeColunasVO.getEstado() != EstadoProcessamentoEnum.NAO_INICIADO)
            return false;

        colunasParaRemover =
                removeColunasVO
                        .getCamposParaRemover()
                        .stream()
                        .map(RemoveColunasCampoVO::getNumeroColuna)
                        .toList();

        removeColunasService.registrarInicioProcessamento(getIdTarefa(), getJobId().toString());

        return true;
    }

    @Override
    void processar() {

        Stream<FileProcessamento> stream = getArquivosEmProcessamento().stream();

        removeColunasService.registrarProcessando(getIdTarefa());

        (isParalelo() ?
                stream.parallel() :
                stream)
                .forEach(this::processaUmArquivo);
    }

    @Override
    String getDiretorioSaida() {
        return removeColunasVO.getDiretorioSaida();
    }

    @Override
    EstadoProcessamentoEnum getEstadoProcessamento() {
        return removeColunasVO.getEstado();
    }

    @Override
    String getDiretorioEntrada() {
        return removeColunasVO.getDiretorioEntrada();
    }
}