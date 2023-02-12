package br.com.spedison.digestor_csv.dto;

import br.com.spedison.digestor_csv.model.AgrupaCampoVO;
import br.com.spedison.digestor_csv.model.AgrupaVO;
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
public class AgrupaCampoDTO {
    Long idAgrupa;
    @EqualsAndHashCode.Include
    Long idCampo;
    String nomeCampo;
    public void preencheVO(AgrupaCampoVO campoVO) {
        campoVO.setId(getIdCampo());
        AgrupaVO agrupa = new AgrupaVO();
        agrupa.setId(getIdAgrupa());
        String [] valor = nomeCampo.split("[;]");
        campoVO.setNumeroColuna(Integer.parseInt(valor[0]));
        campoVO.setNomeColuna(valor[1]);
        campoVO.setAgrupaVO(agrupa);
    }
}