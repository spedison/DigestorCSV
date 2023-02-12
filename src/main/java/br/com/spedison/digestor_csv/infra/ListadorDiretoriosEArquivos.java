package br.com.spedison.digestor_csv.infra;

import br.com.spedison.digestor_csv.service.ConfiguracaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

@Component
public class ListadorDiretoriosEArquivos {
    @Autowired
    ConfiguracaoService configuracaoService;

    public List<File> lerDiretoriosParaEntrada() {

        List<String> diretorios = configuracaoService.getDiretorios();
        diretorios.add(Paths.get(diretorios.get(0),"filtro").toString());
        diretorios.add(Paths.get(diretorios.get(1),"filtro").toString());

        List<File> ret = new LinkedList<>();

        diretorios
                .stream()
                .map(File::new)
                .forEach(f -> {
                    if (f.exists() && f.isDirectory())
                        ret.add(f);

                    f.listFiles((dir, name) -> {
                        File ff = new File(dir.toString(), name);
                        if (ff.exists() && ff.isDirectory())
                            ret.add(ff);
                        return true;
                    });
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

}
