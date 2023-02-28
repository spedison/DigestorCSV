package br.com.spedison.digestor_csv.infra;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

@Log4j2
@NoArgsConstructor
@Component
public class FileWriterResiliente implements Closeable {

    final static Integer TAM_BUFFER = 1024 * 10;
    private String nomeArquivo;
    private File arquivoParaGravar;
    private final StringBuffer buffer = new StringBuffer(TAM_BUFFER);

    @Setter
    private Charset encoding = StandardCharsets.UTF_8;

    public boolean bufferIsEmpty(){
        return buffer.isEmpty();
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
        arquivoParaGravar = new File(this.nomeArquivo);
        buffer.setLength(0);
    }

    public void setNomeArquivo(File nomeArquivo) {
        setNomeArquivo(nomeArquivo.toString());
    }

    public void flush() throws IOException {
        try {
            if (!arquivoParaGravar.exists())
                Files.writeString(arquivoParaGravar.toPath(), buffer.toString(), encoding, StandardOpenOption.APPEND, StandardOpenOption.CREATE_NEW);
            else
                Files.writeString(arquivoParaGravar.toPath(), buffer.toString(), encoding, StandardOpenOption.APPEND);
            buffer.setLength(0);
        } catch (IOException ioe) {
            log.error("Problemas ao gravar no arquivo : " + nomeArquivo);
            log.error(ioe);
            throw ioe;
        }
    }

    public boolean writeLine(String newLine) {

        String lineToWrite = newLine + System.lineSeparator();

        if (buffer.capacity() > (buffer.length() + lineToWrite.length())) {
            buffer.append(lineToWrite);
            return true;
        } else {
            try {
                flush();
            } catch (IOException ioe) {
                log.error("Problemas ao gravar o buffer " + buffer.toString());
                return false;
            }
            buffer.append(lineToWrite);
            return true;
        }
    }

    @Override
    public void close() throws IOException {
        flush();
    }
}