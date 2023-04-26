package br.com.spedison.digestor_csv.processadores;


import br.com.spedison.digestor_csv.infra.FileProcessamento;
import br.com.spedison.digestor_csv.infra.ListFileProcessamento;
import br.com.spedison.digestor_csv.infra.StringUtils;
import br.com.spedison.digestor_csv.infra.Utils;
import br.com.spedison.digestor_csv.model.ConfiguracaoVO;
import br.com.spedison.digestor_csv.model.EstadoProcessamentoEnum;
import br.com.spedison.digestor_csv.service.ConfiguracaoService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.jobrunr.jobs.context.JobContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;


@Log4j2
@Component
public abstract class ProcessadorBase {

    @Setter
    Properties properties;

    @Getter
    @Setter
    String nomeProcessamento;

    @Autowired
    ConfiguracaoService configuracaoService;
    @Getter
    private Long idTarefa;

    @Getter
    private ListFileProcessamento arquivosEmProcessamento = new ListFileProcessamento();

    @Getter
    Charset encoding;

    @Getter
    private UUID jobId;

    @Getter
    private JobContext jobContext;

    protected void listaArquivosParaProcessar(ListFileProcessamento listaParaAdd) {
        File dirParaListar = new File(getDiretorioEntrada());
        Arrays.stream(
                        Objects.requireNonNull(dirParaListar.listFiles(
                                (dir, name) -> {
                                    String ext = getExtensao().toLowerCase();
                                    if (ext.equals("*")) return true;
                                    return name.toLowerCase().endsWith("." + ext);
                                }))
                )
                .map(f -> new FileProcessamento(f.getAbsolutePath()))
                .forEach(listaParaAdd::add);
    }

    // Essa deve ser a última instrução antes de terminar o processamento;
    abstract void terminar();

    abstract boolean iniciar();

    abstract void processar();

    abstract String getDiretorioSaida();

    abstract EstadoProcessamentoEnum getEstadoProcessamento();

    abstract String getDiretorioEntrada();

    @CacheEvict({"primeiro-entrada-entrada", "diretorios-entrada",})
    public void executar(Long idTarefa, JobContext jobContext) {
        preInicia(idTarefa, jobContext);
        this.idTarefa = idTarefa;
        this.jobId = jobContext.getJobId();
        this.jobContext = jobContext;
        properties = configuracaoService.getConfiguracao();
        listaArquivosParaProcessar(arquivosEmProcessamento);
        encoding = configuracaoService.getEnconding();
        if (iniciar()) {
            new File(getDiretorioSaida()).mkdirs(); // Cria diretório de saida.
            processar();
            terminar();
        }
    }

    protected abstract void preInicia(Long idTarefa, JobContext jobContext);

    public Boolean isParalelo() {
        String val = properties.getProperty(ConfiguracaoVO.nomes[4]);
        val = val.trim().toLowerCase();
        return val.equals("true") || val.equals("sim")
                || val.equals("verdadeiro") || val.equals("1")
                || val.equals("ligado");
    }

    public Charset getCharset() {
        String nomeCharset = properties.getProperty(ConfiguracaoVO.nomes[3]);
        return StringUtils.getCharset(nomeCharset);
    }

    public String getSeparadoresColunas() {
        return properties.getProperty(ConfiguracaoVO.nomes[0]).replace("\\t", "\t");
    }

    public String getExtensao() {
        return properties.getProperty(ConfiguracaoVO.nomes[5]);
    }

    /***
     * Define o nome do arquivo de saida a partir do arquivo de entrada e os dados da tarefa.
     * @param entrada Arquivo de entrada
     * @return Arquivo de saida
     */
    protected File defineArqivoSaida(File entrada) {
        // O Diretório de saida e o nome da tarefa é base para criar o diretório de saida.
        return Paths.get(getDiretorioSaida(), entrada.getName()).toFile();
    }

    public void logEstado(String msg) {
        log.debug(msg + " - Processando %s - com o estado %s em %d linhas".
                formatted(nomeProcessamento, getEstadoProcessamento(), getLinhasProcessadas()));
    }

    public void logEstado() {
        log.debug("Processando %s - com o estado %s em %d linhas".
                formatted(nomeProcessamento, getEstadoProcessamento(), getLinhasProcessadas()));
    }

    public Long getLinhasProcessadas() {
        if (getArquivosEmProcessamento() == null || getArquivosEmProcessamento().isEmpty())
            return 0L;

        return
                getArquivosEmProcessamento()
                        .stream()
                        .map(FileProcessamento::getNumeroLinhasProcessadas)
                        .reduce(0L, (a, b) -> a + b);
    }
}