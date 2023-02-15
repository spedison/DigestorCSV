package br.com.spedison.digestor_csv.service;

import br.com.spedison.digestor_csv.infra.StringUtils;
import br.com.spedison.digestor_csv.infra.Utils;
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

            vo = new ConfiguracaoVO(ConfiguracaoVO.nomes[0], "Separadores de Colunas", 0, ";,:|", true);
            crepo.save(vo);

            vo = new ConfiguracaoVO(ConfiguracaoVO.nomes[1], "Diretório de Entrada", 1, "<Coloque o diretório de entrada aqui>", true);
            crepo.save(vo);

            vo = new ConfiguracaoVO(ConfiguracaoVO.nomes[2], "Diretório de Saída", 2, "<Coloque o diretório de saida aqui>", true);
            crepo.save(vo);

            vo = new ConfiguracaoVO(ConfiguracaoVO.nomes[3], "Encoding dos Arquivos", 3, "8859", true);
            crepo.save(vo);

            vo = new ConfiguracaoVO(ConfiguracaoVO.nomes[4], "Processa arquivos em paralelo", 4, "0", true);
            crepo.save(vo);

            vo = new ConfiguracaoVO(ConfiguracaoVO.nomes[5], "Extensão dos arquivos", 5, "csv", true);
            crepo.save(vo);
        }
    }

    public List<ConfiguracaoVO> getListaParaTela() {
        return crepo.findAllByHabilitadoOrderByOrdem(true);
    }

    @Transactional
    public void salvaListaDaTela(List<ConfiguracaoVO> itensParaSalvar) {
        itensParaSalvar
                .stream()
                .map(c -> new String[]{c.getNome(), c.getValor()})
                .forEach(str -> crepo.updateValor(str[0], str[1]));
    }

    public Properties getConfiguracao() {
        Properties ret = new Properties();

        crepo
                .findAllByHabilitadoOrderByOrdem(true)
                .stream()
                .map(s -> new String[]{s.getNome(), s.getValor()})
                .forEach(c -> ret.setProperty(c[0], c[1]));

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
