package br.com.spedison.digestor_csv.processadores;

import br.com.spedison.digestor_csv.infra.*;
import br.com.spedison.digestor_csv.model.AgrupaCampoVO;
import br.com.spedison.digestor_csv.model.AgrupaVO;
import br.com.spedison.digestor_csv.model.EstadoProcessamentoEnum;
import br.com.spedison.digestor_csv.service.AgrupaService;
import lombok.extern.log4j.Log4j2;
import org.jobrunr.jobs.context.JobContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
@Log4j2
public class ProcessadorAgrupa extends ProcessadorBase {

    //Lista de Mapas contendo os Handles dos arquivos abertos.
    private final List<Map<String, BufferedWriter>> listaMapaArquivos = new LinkedList<>(); // Tem que ter um mapa desse para cada arquivo processado.
    @Autowired
    private AgrupaService agrupaService;
    private List<Integer> colunasParaProcessar;
    private AgrupaVO agrupaVO;

    @Autowired
    ExecutadorComControleTempo executaAtualizacao;

    private void adicionaMap() {
        listaMapaArquivos.add(new TreeMap<>());
    }

    @Override
    protected void preInicia(Long idTarefa, JobContext jobContext) {
        agrupaVO = agrupaService.getAgrupaComCampos(idTarefa);
    }

    BufferedWriter pegaAquivo(FileProcessamento fileProcessamento, String prefixo) {

        Map<String, BufferedWriter> mapaArquivos = listaMapaArquivos.get(fileProcessamento.getNumeroArquivoProcessamento());

        String[] arquivoSeparado = FileUtils.separaNomeExtensaoArquivo(fileProcessamento.getName());
        String nomeArquivo = "%s___%s.%s".formatted(arquivoSeparado[0], prefixo, arquivoSeparado[1]);

        // Se o Arquivo já está aberto, retorna.
        if (mapaArquivos.containsKey(nomeArquivo)) {
            return mapaArquivos.get(nomeArquivo);
        }

        try {
            // Vamos abrir um arquivo novo.
            BufferedWriter bw = FileUtils.abreArquivoEscrita(getDiretorioSaida(), nomeArquivo, getCharset());
            // Adiciona o Header e ...
            bw.write(fileProcessamento.getHeader());
            bw.newLine();
            // .. adiciona no map para utilizar posteriormente.
            mapaArquivos.put(nomeArquivo, bw);
            return bw;
        } catch (IOException ioe) {
            return null;
        }
    }

    void processaUmArquivo(FileProcessamento arquivoEntrada) {

        try {

            BufferedReader br = FileUtils.abreArquivoLeitura(arquivoEntrada.toString(), getEncoding());

            arquivoEntrada.setHeader(br.readLine());
            arquivoEntrada.incLinhasProcessadas();

            String line;

            while ((line = br.readLine()) != null) {

                String[] colunas = line.split(super.getSeparadoresColunas());
                final String nomeSufixoArquivo =
                        colunasParaProcessar
                                .stream()
                                .map(i -> colunas[i])
                                .map(FileUtils::ajustaNome)
                                .collect(Collectors.joining("___"));

                // Pega o arquivo de acordo com a nome definido.
                BufferedWriter bw = pegaAquivo(arquivoEntrada, nomeSufixoArquivo);

                if (bw != null) {
                    bw.write(line);
                    bw.newLine();
                    arquivoEntrada.incLinhasProcessadas();
                } else {
                    log.error("Problemas ao gravar a linha : " + line);
                }

                executaAtualizacao
                        .executaSeTimeout(() -> {
                            agrupaService.atualizaLinhasProcessadas(agrupaVO.getId(), getLinhasProcessadas());
                        });
            }
            br.close();
            fechaTodosArquivos(arquivoEntrada.getNumeroArquivoProcessamento());
            agrupaService.atualizaLinhasProcessadas(getIdTarefa(), getLinhasProcessadas());
            log.debug("Processado arquivo %s".formatted((arquivoEntrada.getName())));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /***
     * Fecha todos os arquivos que estão na variável mapaArquivos
     */
    private void fechaTodosArquivos(Integer posicao) {
        Map<String, BufferedWriter> mapaArquivos = listaMapaArquivos.get(posicao);
        mapaArquivos.forEach((str, bw) -> {
            log.debug("Fechando arquivo " + str);
            try {
                bw.close();
            } catch (IOException ioe) {
                log.error("Problemas ao fechar arquivo " + str);
            }
        });
    }

    @Override
    void terminar() {
        log.debug("Fechando Arquivos.");
        IntStream
                .range(
                        0,
                        listaMapaArquivos.size())
                .forEach(
                        this::fechaTodosArquivos);

        agrupaService.registrarFimProcessamento(agrupaVO.getId(), getLinhasProcessadas());
    }

    @Override
    boolean iniciar() {

        if (agrupaVO.getEstado() != EstadoProcessamentoEnum.NAO_INICIADO)
            return false;

        // Pega o número das colunas que seráo usadas.
        colunasParaProcessar = agrupaVO
                .getCamposParaAgrupar()
                .stream().map(AgrupaCampoVO::getNumeroColuna)
                .toList();

        // Numera todos os arquivos que serão processados.
        IntStream
                .range(0, getArquivosEmProcessamento().length)
                .peek(i -> this.adicionaMap())// Adiciona item no map.
                .forEach(
                        i -> getArquivosEmProcessamento()[i]
                                .setNumeroArquivoProcessamento(i)); // Defini o map na estrutura para processar.

        agrupaService.registrarInicioProcessamento(getIdTarefa(), getJobId().toString());

        return true;
    }

    @Override
    void processar() {

        Stream<FileProcessamento> stream = Arrays.stream(getArquivosEmProcessamento());

        agrupaService.registrarProcessando(getIdTarefa());

        (isParalelo() ?
                stream.parallel() :
                stream)
                .forEach(this::processaUmArquivo);
    }

    @Override
    String getDiretorioSaida() {
        return agrupaVO.getDiretorioSaida();
    }

    @Override
    EstadoProcessamentoEnum getEstadoProcessamento() {
        return agrupaVO.getEstado();
    }

    @Override
    String getDiretorioEntrada() {
        return agrupaVO.getDiretorioEntrada();
    }
}
