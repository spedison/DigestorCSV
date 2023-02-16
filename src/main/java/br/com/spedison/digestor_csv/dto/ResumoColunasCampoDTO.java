package br.com.spedison.digestor_csv.dto;

import br.com.spedison.digestor_csv.model.RemoveColunasCampoVO;
import br.com.spedison.digestor_csv.model.RemoveColunasVO;
import br.com.spedison.digestor_csv.model.ResumoColunasCampoVO;
import br.com.spedison.digestor_csv.model.ResumoColunasVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Log4j2
@AllArgsConstructor
@NoArgsConstructor
public class ResumoColunasCampoDTO {
    private Long idResumoColunas;
    @EqualsAndHashCode.Include
    private Long idCampo;
    private String nomeCampo;
    public void preencheVO(ResumoColunasCampoVO campoVO) {
        campoVO.setId(getIdCampo());
        ResumoColunasVO removeColunas = new ResumoColunasVO();
        removeColunas.setId(getIdResumoColunas());
        String [] valor = nomeCampo.split("[;]");
        campoVO.setNumeroColuna(Integer.parseInt(valor[0]));
        campoVO.setNomeColuna(valor[1]);
        campoVO.setResumoColunasVO(removeColunas);
    }
}