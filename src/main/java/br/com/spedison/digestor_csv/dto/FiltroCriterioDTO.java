package br.com.spedison.digestor_csv.dto;

import br.com.spedison.digestor_csv.model.FiltroCriterioVO;
import br.com.spedison.digestor_csv.model.TipoCriterioEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Log4j2
@AllArgsConstructor
@NoArgsConstructor
public class FiltroCriterioDTO {
    Long idFiltro;
    @EqualsAndHashCode.Include
    Long idCriterio;
    private TipoCriterioEnum tipoComparacao;
    String strValor;
    BigDecimal mumValor1;
    BigDecimal mumValor2;
    String nomeColuna; // Nome da coluna no Header do 1o Arquivo

    public void preencheVO(FiltroCriterioVO compadorVO) {
        compadorVO.setId(getIdCriterio());
        compadorVO.setTipoComparacao(getTipoComparacao());
        compadorVO.setStrValor(getStrValor());
        compadorVO.setMumValor1(getMumValor1());
        compadorVO.setMumValor2(getMumValor2());
        String [] valor = getNomeColuna().split("[;]");
        compadorVO.setNumeroColuna(Integer.parseInt(valor[0]));
        compadorVO.setNomeColuna(valor[1]);
    }
}