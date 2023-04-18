package br.com.spedison.digestor_csv.service;


import br.com.spedison.digestor_csv.infra.DadosArquivo;
import br.com.spedison.digestor_csv.infra.FileUtils;
import br.com.spedison.digestor_csv.infra.ListadorDiretoriosEArquivos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Service
public class VerificaArquivosService {

    @Autowired
    ListadorDiretoriosEArquivos listadorDiretoriosEArquivos;

    @Autowired
    ConfiguracaoService configuracaoService;

    /***
     * Gera uma lista de arquivos não vazios para exibir detalhes
     * @param diretorio Diretório de arquivos
     * @param quantidade Quantidade máxima de arquivos
     * @return Dados detalhados de arquivos
     */
    public List<DadosArquivo> getDadosArquivos(String diretorio, Integer quantidade) {

        String extensao = configuracaoService.getExtensao();

        // Segurança : Somente pode listar os diretórios dentro da lista definida pela entrada do sistema.
        if (listadorDiretoriosEArquivos.lerDiretoriosParaEntradaFormatoString().indexOf(diretorio) == -1) {
            return new LinkedList<>();
        }

        // Lista os arquivos limitando a quantidade.
        final int[] conta = {0};
        FilenameFilter filenameFilterLimitaQuantidade = (f, n) ->
                FileUtils.getFiltroArquivoNaoVazio(extensao).accept(f, n) && (conta[0]++ < quantidade);

        // Processa a listagem
        File f = new File(diretorio);
        File[] ret = f.listFiles(filenameFilterLimitaQuantidade);

        // Converte o File em detalhamento do arquivo e depois em uma lista de detalhes.
        return Arrays.stream(ret).map(DadosArquivo::new).toList();
    }

}
