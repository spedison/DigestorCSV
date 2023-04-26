package br.com.spedison.digestor_csv.infra;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class FileProcessamento extends File {
    @Getter
    private Long numeroLinhasProcessadas = 0L;

    @Setter
    @Getter
    private Integer numeroArquivoProcessamento;

    @Getter
    @Setter
    private String header;

    public void incLinhasProcessadas() {
        numeroLinhasProcessadas++;
    }

    public FileProcessamento(String pathname) {
        super(pathname);
    }
}
