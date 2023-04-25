package br.com.spedison.digestor_csv.infra;

import br.com.spedison.digestor_csv.dto.ResumoColunasDTO;
import br.com.spedison.digestor_csv.model.ResumoColunasVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;
import java.util.Objects;

@Component
public class ResumoColunasVoUtils {
    @Autowired
    FormatadorData formatadorData;

    public void formataDirSaida(ResumoColunasVO vo, String dirSaida) {
        vo.setDiretorioSaida(
                Paths.get(
                                dirSaida, "resumo",
                                FileUtils.ajustaNome(
                                        vo.getNomeDaTarefa()),
                                formatadorData.formataDataParaArquivos(vo.getDataCriacao()))
                        .toString());
    }

    public void setColunaResumida(ResumoColunasVO resumoColunasVO, ResumoColunasDTO dto) {
        String coluna = "";
        if (!Objects.isNull(resumoColunasVO.getNumeroColunaSumarizada()) &&
                !Objects.isNull(resumoColunasVO.getNomeColunaSumarizada()))
            coluna = resumoColunasVO.getNumeroColunaSumarizada() + ";" + resumoColunasVO.getNomeColunaSumarizada();
        dto.setColunaSumarizada(coluna);
    }

}
