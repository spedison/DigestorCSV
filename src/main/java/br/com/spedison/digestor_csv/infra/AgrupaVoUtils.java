package br.com.spedison.digestor_csv.infra;

import br.com.spedison.digestor_csv.model.AgrupaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;

@Component
public class AgrupaVoUtils {
    @Autowired
    FormatadorData formatadorData;

    public void formataDirSaida(AgrupaVO vo, String dirSaida) {
        vo.setDiretorioSaida(
                Paths.get(
                                dirSaida, "agrupa",
                                FileUtils.ajustaNome(
                                        vo.getNomeTarefa()),
                                formatadorData.formataDataParaArquivos(vo.getDataCriacao()))
                        .toString());
    }
}
