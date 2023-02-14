package br.com.spedison.digestor_csv.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.persistence.*;

@Entity
@Table(name = "tb_agrupa_campo")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Log4j2
@AllArgsConstructor
@NoArgsConstructor
public class RemoveColunasCampoVO {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @ManyToOne
    @JoinColumn(name = "remove_colunas_id") // Coluna que será usada para relaciona com o remove_colunas.id
    private RemoveColunasVO removeColunasVO;
    @Column(nullable = false)
    Integer numeroColuna; // Número da Coluna iniciando em 0, se -1 compara com todas.
    @Column(length = 255, nullable = true)
    String nomeColuna; // Número da Coluna iniciando em 0, se -1 compara com todas.
}