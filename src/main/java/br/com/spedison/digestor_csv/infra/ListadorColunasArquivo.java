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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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

    private List<String> getColunasObjeto(String header) {
        if (Objects.isNull(header) || header.isBlank())
            return null;

        String separador = StringUtils.strParaExpressaoRegular(configuracaoService.getSeparador());
        return Arrays.stream(header.split(separador)).toList();
    }


    public List<String> getListaColunasRemoveColunas(Long id) {
        RemoveColunasVO agrupa = removeColunasService.getAgrupaSemCampos(id);
        return getListColunas(agrupa.getDiretorioEntrada());
    }

    public List<String> getListaColunasAgrupa(Long idAgrupa) {
        AgrupaVO agrupa = agrupaService.getAgrupaSemCampos(idAgrupa);
        return getListColunas(agrupa.getDiretorioEntrada());
    }

    public List<String> getListaColunasFiltro(Long idFiltro) {
        FiltroVO filtro = filtroService.getFiltroSemCriterios(idFiltro);
        List<String> ret = getColunasObjeto(filtro.getHeader());
        if (Objects.isNull(ret))
            return getListColunas(filtro.getDiretorioEntrada());
        else
            return ret;
    }

    @Cacheable("colunas-arquivos-dir")
    public List<String> getListColunas(String dirEntrada) {
        String separador = StringUtils.strParaExpressaoRegular(configuracaoService.getSeparador());
        String extensao = configuracaoService.getExtensao();
        Charset encode = configuracaoService.getEnconding();
        String linhaPrimeiroArquivo = lePrimeiraLinhaArquivo(dirEntrada, extensao, encode, false);

        if (Objects.isNull(linhaPrimeiroArquivo))
            return new LinkedList<>();

        return Arrays
                .stream(
                        linhaPrimeiroArquivo
                                .split(separador))
                .map(s -> {
                    s = s.trim();
                    if (s.charAt(0) == '"')
                        s = s.substring(1);
                    if (s.charAt(s.length() - 1) == '"')
                        s = s.substring(0, s.length() - 1);
                    return s;
                })
                .toList();
    }

    /***
     *
     * @param dir Diretório que será analisado
     * @param ext Extensão de arquivo aceita
     * @param encode Encoding usado
     * @param linhas Número de linhas lidas.
     * @param ignoraLinhasVazias Se true, não aceita linhas vazias como retorno, se false todas as linhas deverão se preenchidas
     * @return Linhas lidas do 1o arquivo do diretório indicado por dir.
     */
    private String[] lePrimeirasLinhasDoArquivo(String dir, String ext, Charset encode, int linhas, boolean ignoraLinhasVazias) {
        File dirToList = new File(dir);
        FilenameFilter fnf = FileUtils.getFiltroArquivoNaoVazio(ext);

        final boolean[] achou = {false};
        FilenameFilter fnfSomenteUm = (d, n) -> {
            if (achou[0] == true) {
                return false;
            }
            if (fnf.accept(d, n) == false) {
                return false;
            } else {
                achou[0] = true;
                return true;
            }
        };

        File[] lista = dirToList.listFiles(fnfSomenteUm);

        if (lista == null || lista.length == 0)
            return null;

        var ret = new LinkedList<String>();
        try (BufferedReader br = FileUtils.abreArquivoLeitura(lista[0].toString(), encode)) {
            for (int k = 0; k < linhas; k++) {
                String s = br.readLine();
                if (s == null)
                    break;
                if (!ignoraLinhasVazias) {
                    if (s.isBlank()) {
                        continue;
                    }
                }
                ret.add(s);
            }
        } catch (IOException e) {
            log.error(e);
            return null;
        }
        return ret.toArray(String[]::new);
    }

    private String lePrimeiraLinhaArquivo(String dir, String ext, Charset encode, boolean ignoraLinhasVazias) {
        String[] linhas = lePrimeirasLinhasDoArquivo(dir, ext, encode, 1, ignoraLinhasVazias);
        if (Objects.isNull(linhas))
            return null;
        if (linhas.length == 0)
            return null;
        return linhas[0];
    }
}