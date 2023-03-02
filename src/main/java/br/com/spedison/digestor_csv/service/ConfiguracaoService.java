package br.com.spedison.digestor_csv.service;

import br.com.spedison.digestor_csv.infra.StringUtils;
import br.com.spedison.digestor_csv.model.ConfiguracaoVO;
import br.com.spedison.digestor_csv.repository.ConfiguracaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;

@Service
public class ConfiguracaoService {

    @Autowired
    ConfiguracaoRepository crepo;

    @Transactional
    public void iniciarConfiguracao() {
        Long conta = crepo.contaHabilitados();

        if (conta == 0) {
            ConfiguracaoVO vo;

            vo = new ConfiguracaoVO(ConfiguracaoVO.nomes[0], "Separadores de Colunas", 0, ";,:|", true,"Separadores que podem ser encontrados no arquivo, aqui vc pode adicionar mais algum o tab é representado por \\t new line não é considerado um separador de coluna");
            crepo.save(vo);

            vo = new ConfiguracaoVO(ConfiguracaoVO.nomes[1], "Diretório de Entrada", 1, "<Coloque o diretório de entrada aqui>", true, "Diretório que ele lerá os arquivos de entrada. Aqui ele lê alguns níveis para baixo, assim você pode ter um diretório de projetos com diversos processamentos");
            crepo.save(vo);

            vo = new ConfiguracaoVO(ConfiguracaoVO.nomes[2], "Diretório de Saída", 2, "<Coloque o diretório de saida aqui>", true, "Esse diretório é utilizado para gravar os resultados de seu processamento. Lembrando que é um diretório base, assim o seu processamento fica bem identificado. Esse diretório também poderá ser usado como entrada assim você não precisa mover os arquivos entre os diretórios");
            crepo.save(vo);

            vo = new ConfiguracaoVO(ConfiguracaoVO.nomes[3], "Encoding dos Arquivos", 3, "8859", true,"O enconding é a codificação utilizada para expressar os caracteres do texto. Assim ele fica sem caracteres estranos. Normalmente o documento explica qual a codificação utilizada. Aqui pode ser: latim1, utf8, utf16, ascii, 8859");
            crepo.save(vo);

            vo = new ConfiguracaoVO(ConfiguracaoVO.nomes[4], "Processa arquivos em paralelo", 4, "0", true,"Se a sua máquina tiver vários núcleos e bastante memória, pode habilitar com 1. Se não tiver deixe com 0. Ele só será utilizado se tiver vários arquivos para processar.");
            crepo.save(vo);

            vo = new ConfiguracaoVO(ConfiguracaoVO.nomes[5], "Extensão dos arquivos", 5, "csv", true, "Essa é a extensão do arquivo utilizado por exemplo: os arquivos são laranja.csv e banana.csv, assim a extensão é csv. Fique tranquilo pois ele não diferencia maiuscula e minúsculas.");
            crepo.save(vo);

            vo = new ConfiguracaoVO(ConfiguracaoVO.nomes[6], "Separador de novas colunas", 6, ";", true, "Caso ele precise adicionar novas colunas ao arquivo de saída ele colocará esse separador.");
            crepo.save(vo);
        }
    }

    public List<ConfiguracaoVO> getListaParaTela() {
        return crepo.findAllByHabilitadoOrderByOrdem(true);
    }

    @Transactional
    public void salvaListaDaTela(List<ConfiguracaoVO> itensParaSalvar) {
        itensParaSalvar
                .forEach(item -> crepo.updateValor(item.getNome(), item.getValor()));
    }

    public Properties getConfiguracao() {
        Properties ret = new Properties();
        crepo
                .findAllByHabilitadoOrderByOrdem(true)
                .forEach(item -> ret.setProperty(item.getNome(), item.getValor()));
        return ret;
    }

    public String getConfiguracao(String nome) {
        return crepo.getValores(nome).get(0);
    }

    public List<String> getDiretorios() {
        return crepo.getValores(ConfiguracaoVO.nomes[1], ConfiguracaoVO.nomes[2]);
    }

    public String getDirSaida() {
        return crepo.getValores(ConfiguracaoVO.nomes[2]).get(0);
    }

    public String getSeparador() {
        return crepo.getValores(ConfiguracaoVO.nomes[0]).get(0);
    }

    public String getExtensao() {
        return crepo.getValores(ConfiguracaoVO.nomes[5]).get(0);
    }

    public Charset getEnconding() {
        String enc = crepo.getValores(ConfiguracaoVO.nomes[3]).get(0);
        return StringUtils.getCharset(enc);
    }
}
