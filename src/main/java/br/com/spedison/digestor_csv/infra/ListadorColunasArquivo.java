package br.com.spedison.digestor_csv.infra;

import br.com.spedison.digestor_csv.model.AgrupaVO;
import br.com.spedison.digestor_csv.model.FiltroVO;
import br.com.spedison.digestor_csv.model.RemoveColunasVO;
import br.com.spedison.digestor_csv.service.AgrupaService;
import br.com.spedison.digestor_csv.service.ConfiguracaoService;
import br.com.spedison.digestor_csv.service.FiltroService;
import br.com.spedison.digestor_csv.service.RemoveColunasService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

@Component
@Log4j2
public class ListadorColunasArquivo {

    @Autowired
    ConfiguracaoService configuracaoService;
    @Autowired
    FiltroService filtroService;

    @Autowired
    AgrupaService agrupaService;

    @Autowired
    RemoveColunasService removeColunasService;

    public List<String> getListaColunasRemoveColunas(Long id) {
        RemoveColunasVO agrupa = removeColunasService.getAgrupaSemCampos(id);
        return getListColunas(agrupa.getDiretorioEntrada());
    }
    public List<String> getListaColunasAgrupa(Long idAgrupa) {
        AgrupaVO agrupa = agrupaService.getAgrupaSemCampos(idAgrupa);
        return getListColunas(agrupa.getDiretorioEntrada());
    }

    public List<String> getListaColunasFiltro(Long idFiltro) {
        FiltroVO fitro = filtroService.getFiltroSemComparadores(idFiltro);
        return getListColunas(fitro.getDiretorioEntrada());
    }

    public List<String> getListColunas(String dirEntrada) {
        String separador = configuracaoService.getSeparador();
        String extensao = configuracaoService.getExtensao();
        Charset encode = configuracaoService.getEnconding();
        String linhaPrimeiroArquivo = lePrimeiraLinhaArquivo(dirEntrada, extensao, encode);

        return Arrays
                .stream(
                        linhaPrimeiroArquivo
                                .split(separador))
                .map(s -> s.replace("\"", ""))
                .toList();
    }

    private String lePrimeiraLinhaArquivo(String dir, String ext, Charset encode) {
        File dirToList = new File(dir);
        File[] lista =
                dirToList.listFiles((dirBase, nome) -> {
                    File ff = new File(dirBase.toString(), nome);
                    if (ff.exists() && ff.isFile()) {
                        return (
                                nome.trim().toLowerCase().endsWith(ext.toLowerCase().trim())
                                        || ext.trim().equals("*")
                                        || ext.trim().equals("*.*")
                        );
                    }
                    return false;
                });

        if (lista.length == 0)
            return "";

        try {
            BufferedReader br = FileUtils.abreArquivoLeitura(lista[0].toString(), encode);
            String ret = br.readLine();
            br.close();
            return ret;
        } catch (IOException e) {
            log.error(e);
            return "";
        }
    }
}
