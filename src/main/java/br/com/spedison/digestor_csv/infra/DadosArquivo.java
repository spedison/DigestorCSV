package br.com.spedison.digestor_csv.infra;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.*;

@Data
@Log4j2
public class DadosArquivo {

    private File arquivo;
    private String[] primeirasLinhasParaExibir;
    private String[] primeirasLinhasAvaliaSeparadores;
    private String encoding;
    private List<Character> possiveisSeparadores;

    public DadosArquivo(File arquivo) {
        this(arquivo, 20);
    }

    public DadosArquivo(File arquivo, int numeroLinhas) {
        this.arquivo = arquivo;
        this.encoding = FileUtils.getEncodingArquivoTexto(arquivo.toString());
        carregaLinhas(numeroLinhas);
        carregaPossiveisSeparadores();
    }

    public DadosArquivo(File arquivo, String encoding, int numeroLinhas) {
        this.arquivo = arquivo;
        this.encoding = encoding;
        carregaLinhas(numeroLinhas);
        carregaPossiveisSeparadores();
    }

    private void carregaPossiveisSeparadores() {
        DefineSeparador ds = new DefineSeparador();
        possiveisSeparadores = ds.separadoresUsados(primeirasLinhasAvaliaSeparadores);
    }

    private void carregaLinhas(int numeroLinhas) {
        Charset cs = StringUtils.getCharset(encoding);
        try {
            BufferedReader br = FileUtils.abreArquivoLeitura(arquivo.toString(), cs);
            List<String> linhas = new LinkedList<>();
            String linha;
            for (int conta = 0; conta < numeroLinhas; conta++) {
                linha = br.readLine();
                if (Objects.isNull(linha) || linha.isBlank())
                    continue;
                linhas.add(linha);
            }
            primeirasLinhasParaExibir = linhas.stream().limit(5).toArray(String[]::new);
            primeirasLinhasAvaliaSeparadores = linhas.stream().toArray(String[]::new);
        } catch (IOException e) {
            log.error(e);
            primeirasLinhasParaExibir = new String[]{""};
        }
    }

    public Instant getDataCriacao() {
        BasicFileAttributes attr =
                null;
        try {
            attr = Files.readAttributes(arquivo.toPath(), BasicFileAttributes.class);
            return attr.creationTime().toInstant();
        } catch (IOException e) {
            log.error(e);
            return Instant.now();
        }
    }

    public String getNome() {
        return arquivo.getName();
    }

    public String getDiretorio() {
        return arquivo.getParent();
    }

    public String getExtensao() {
        int pos = arquivo.getName().lastIndexOf(".");
        if (pos == -1)
            return "";
        return arquivo.getName().substring(pos + 1);
    }

    public String getTamanho() {
        return org.apache.commons.io.FileUtils.byteCountToDisplaySize(arquivo.length());
    }

    public String getPossiveisSeparadores() {
        StringJoiner sj = new StringJoiner(", ");

        if (Objects.isNull(possiveisSeparadores))
            return "";

        possiveisSeparadores.forEach(
                c -> {
                    sj.add("[ %s ]".formatted(StringUtils.chParaUsuario(c)));
                }
        );
        return sj.toString();
    }

    public String getEncoding() {
        return encoding;
    }

    public String getPrimeirasLinhasJuntas() {
        StringJoiner sj = new StringJoiner("<br><br>");
        Arrays
                .stream(primeirasLinhasParaExibir)
                .limit(5).map("%s"::formatted)
                .forEach(sj::add);
        return sj.toString();
    }

}
