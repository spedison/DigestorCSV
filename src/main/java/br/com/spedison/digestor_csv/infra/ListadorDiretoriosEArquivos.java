package br.com.spedison.digestor_csv.infra;

import br.com.spedison.digestor_csv.service.ConfiguracaoService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
@Log4j2
public class ListadorDiretoriosEArquivos {
    @Autowired
    ConfiguracaoService configuracaoService;

    private void addInList(File dir, List<File> directories, Long limite) throws IOException {
        try (Stream<Path> stream = Files.walk(Paths.get(dir.toString()), 10)) {
            stream
                    .limit(limite)
                    .filter(Files::isDirectory)
                    .map(Path::toFile)
                    .forEach(directories::add);
        } catch (IOException e) {
            log.error(e);
            throw e;
        }
    }

    @Cacheable("diretorios-entrada")
    public List<File> lerDiretoriosParaEntrada() {
        List<File> ret = new LinkedList<>();
        List<String> diretorios = configuracaoService.getDiretorios();
        IntStream.range(0, diretorios.size()).forEach(i -> {
            try {
                addInList(new File(diretorios.get(i)), ret, 1_000_000L);
            } catch (IOException ioe) {
            }
        });
        return ret;
    }

    public List<String> lerDiretoriosParaEntradaString() {
        return
                lerDiretoriosParaEntrada()
                        .stream()
                        .map(File::toString)
                        .distinct()
                        .toList();
    }

    @Cacheable("primeiro-diretorio-entrada")
    public File lerPrimeiroDiretorioParaEntrada() {
        List<File> ret = new LinkedList<>();
        String diretorios = configuracaoService.getDiretorios().get(0);
        try {
            addInList(new File(diretorios), ret, 1L);
        } catch (IOException ioe) {
            return null;
        }
        if (ret.isEmpty())
            return null;
        return ret.get(0);
    }
}