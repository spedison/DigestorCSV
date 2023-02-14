package br.com.spedison.digestor_csv.dto;

import br.com.spedison.digestor_csv.model.RemoveColunasCampoVO;
import br.com.spedison.digestor_csv.model.RemoveColunasVO;
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
public class RemoveColunasCampoDTO {
    private Long idRemoveColunas;
    @EqualsAndHashCode.Include
    private Long idCampo;
    private String nomeCampo;
    public void preencheVO(RemoveColunasCampoVO campoVO) {
        campoVO.setId(getIdCampo());
        RemoveColunasVO removeColunas = new RemoveColunasVO();
        removeColunas.setId(getIdRemoveColunas());
        String [] valor = nomeCampo.split("[;]");
        campoVO.setNumeroColuna(Integer.parseInt(valor[0]));
        campoVO.setNomeColuna(valor[1]);
        campoVO.setRemoveColunasVO(removeColunas);
    }
}