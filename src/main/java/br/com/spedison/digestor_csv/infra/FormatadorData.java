package br.com.spedison.digestor_csv.infra;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Scope(value="application")
public class FormatadorData {
    private static final DateTimeFormatter sdfNomeArquivo = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    public String formataDataParaArquivos(LocalDateTime timeStamp){
        return timeStamp.format(sdfNomeArquivo);
    }

}
