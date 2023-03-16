package br.com.spedison.digestor_csv.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tb_filtro_comparador")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Log4j2
@AllArgsConstructor
@NoArgsConstructor
public class FiltroCriterioVO {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @ManyToOne
    @JoinColumn(name = "filtro_id") // Coluna que será usada para relaciona com o filtro.id
    private FiltroVO filtroVO;
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private TipoComparacaoEnum tipoComparacao;
    String strValor;
    BigDecimal mumValor1;
    BigDecimal mumValor2;
    @Column(nullable = false)
    Integer numeroColuna; // Número da Coluna iniciando em 0, se -1 compara com todas.
    @Column(length = 255, nullable = true)
    String nomeColuna; // Número da Coluna iniciando em 0, se -1 compara com todas.

    public boolean compara(String valorColuna) {
        valorColuna = valorColuna.replaceAll("[\"']", "").trim();
        try {
            return switch (tipoComparacao) {
                case TXT_IGUAL -> valorColuna.equals(strValor);
                case TXT_CONTEM -> valorColuna.contains(strValor);
                case TXT_NAO_CONTEM -> !valorColuna.contains(strValor);
                case TXT_INICIA -> valorColuna.startsWith(strValor);
                case TXT_TERMINA -> valorColuna.endsWith(strValor);
                case NUM_ENTRE -> (new BigDecimal(strValor)).compareTo(mumValor1) > 0
                        && (new BigDecimal(strValor)).compareTo(mumValor2) < 0;
                case NUM_MAIOR -> (new BigDecimal(strValor)).compareTo(mumValor1) > 0;
                case NUM_MENOR -> (new BigDecimal(strValor)).compareTo(mumValor1) < 0;
                case NUM_IGUAL -> (new BigDecimal(strValor)).compareTo(mumValor1) == 0;
                case VAZIO -> false;
            };
        } catch (NumberFormatException nfe) {
            log.error(nfe);
            log.error("Erro de comparação %s com valor %s"
                    .formatted(tipoComparacao, valorColuna));
            return false;
        }
    }

    /***
     * Pega um texto explicando a comparação.
     * @return Texto
     */
    public String getComparacao() {
        return switch (tipoComparacao) {
            case TXT_INICIA, TXT_IGUAL,
                    TXT_NAO_CONTEM, TXT_TERMINA,
                    TXT_CONTEM -> "Coluna " + getNomeColuna() + " " +
                    tipoComparacao.getTexto() + " \"" + getStrValor() + "\"";
            case NUM_ENTRE -> "Coluna " + getNomeColuna() + " " +
                    tipoComparacao.getTexto() + " " + getMumValor1() + " e " +
                    getMumValor2();
            case NUM_IGUAL, NUM_MAIOR, NUM_MENOR -> "Coluna " + getNomeColuna() + " " +
                    tipoComparacao.getTexto() + " " + getMumValor1();
            case VAZIO -> "Sem nenhuma comparação. Favor remover a dicionar outra";
        };
    }
}