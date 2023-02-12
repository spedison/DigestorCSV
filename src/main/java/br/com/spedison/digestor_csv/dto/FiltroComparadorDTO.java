package br.com.spedison.digestor_csv.dto;

import br.com.spedison.digestor_csv.model.FiltroComparadorVO;
import br.com.spedison.digestor_csv.model.TipoComparacaoEnum;
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
public class FiltroComparadorDTO {
    Long idFiltro;
    @EqualsAndHashCode.Include
    Long idComparador;
    private TipoComparacaoEnum tipoComparacao;
    String strValor;
    BigDecimal mumValor1;
    BigDecimal mumValor2;
    String nomeColuna; // Nome da coluna no Header do 1o Arquivo

    public void preencheVO(FiltroComparadorVO compadorVO) {
        compadorVO.setId(getIdComparador());
        compadorVO.setTipoComparacao(getTipoComparacao());
        compadorVO.setStrValor(getStrValor());
        compadorVO.setMumValor1(getMumValor1());
        compadorVO.setMumValor2(getMumValor2());
        String [] valor = getNomeColuna().split("[;]");
        compadorVO.setNumeroColuna(Integer.parseInt(valor[0]));
        compadorVO.setNomeColuna(valor[1]);
    }
}