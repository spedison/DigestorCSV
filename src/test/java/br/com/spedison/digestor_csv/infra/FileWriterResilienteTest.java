package br.com.spedison.digestor_csv.infra;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class FileWriterResilienteTest {

    @Autowired
    FileWriterResiliente fileWriterResiliente;

    static final File f = new File("Teste_Buffer_Writer.dat");

    @BeforeEach
    public void setup() {
        if (f.exists())
            f.delete();
    }

    @Test
    void bufferIsEmpty() {

        fileWriterResiliente.setNomeArquivo(f);
        assertTrue(fileWriterResiliente.bufferIsEmpty());

        fileWriterResiliente.writeLine("Meu Teste de Buffer");
        assertFalse(fileWriterResiliente.bufferIsEmpty());
    }

    @Test
    void flush() {
        String linha = "Meu Teste de Buffer";
        fileWriterResiliente.setNomeArquivo(f);
        fileWriterResiliente.writeLine(linha);
        try {
            fileWriterResiliente.flush();
            assertTrue( f.length() > linha.length());
        } catch (IOException ioe) {
            fail();
        }
    }

    @Test
    void writeLine() {
        // Vou escrever mais de 500KB e ele terá que retornar true sem nenhum exceção até o final do processamwento
        try (FileWriterResiliente fr = fileWriterResiliente) {
            fr.setNomeArquivo(f);
            IntStream
                    .range(0, 2_000_000)
                    .mapToObj(i -> "Linha para gravar # %d".formatted(i))
                    .peek(log::debug)
                    .forEach(fr::writeLine);
        } catch (IOException ioe) {
            fail();
        }
    }

    @Test
    void close() {

        fileWriterResiliente.setNomeArquivo(f);
        IntStream
                .range(0, 100)
                .mapToObj("Linha para gravar # %d"::formatted)
                .peek(log::debug)
                .map(fileWriterResiliente::writeLine)
                .forEach(Assertions::assertTrue);

        try {
            fileWriterResiliente.close();
        } catch (
                IOException ioe) {
            fail();
        }
    }
}