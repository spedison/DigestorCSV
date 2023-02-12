package br.com.spedison.digestor_csv.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_configuracao")
public class ConfiguracaoVO {
    @Id
    @Column(length = 100)
    @EqualsAndHashCode.Include
    private String nome;

    @Column(name = "texto_site", length = 255)
    private String textoSite;

    Integer ordem;

    @Column(length = 255)
    private String valor;

    Boolean habilitado;

    transient public final static String[] nomes = {"separadores", "diretorio_entrada",
            "diretorio_saida", "encoding", "processa_paralelo","extensao_arquivo"};
}
