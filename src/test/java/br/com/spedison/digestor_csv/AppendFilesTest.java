package br.com.spedison.digestor_csv;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

@SpringBootTest
public class AppendFilesTest {

    @Test
    public void testaAppendSimples() {
        File f = new File("testa_append.dat");

        try {
            if (!f.exists())
                Files.writeString(f.toPath(), "Esse é um téste de append com acentós açento é mais quê açentõs #1"+System.lineSeparator(), StandardCharsets.UTF_8, StandardOpenOption.APPEND, StandardOpenOption.CREATE_NEW);
            else
                Files.writeString(f.toPath(), "Esse é um téste de append com acentós açento é mais quê açentõs #1"+System.lineSeparator(), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            Files.writeString(f.toPath(), "Esse é um teste de append @2"+System.lineSeparator(), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            Files.writeString(f.toPath(), "Esse é um teste de append @3"+System.lineSeparator(), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
