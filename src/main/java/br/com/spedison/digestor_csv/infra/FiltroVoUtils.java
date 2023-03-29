package br.com.spedison.digestor_csv.infra;

import br.com.spedison.digestor_csv.model.FiltroVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;

@Component
public class FiltroVoUtils {
    @Autowired
    FormatadorData formatadorData;

    public void formataDirSaida(FiltroVO vo, String dirSaida) {
        vo.setDiretorioSaida(
                Paths.get(
                                dirSaida, "filtro",
                                FileUtils.ajustaNome(
                                        vo.getNomeDaTarefa()),
                                formatadorData.formataDataParaArquivos(vo.getDataCriacao()))
                        .toString());
    }
}
