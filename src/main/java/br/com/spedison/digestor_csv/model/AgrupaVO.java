package br.com.spedison.digestor_csv.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@Table(name = "tb_agrupa")
public class AgrupaVO {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String diretorioEntrada;
    @Column(nullable = false)
    private String diretorioSaida;
    @Column(nullable = false)
    private LocalDateTime dataCriacao;
    @Enumerated(EnumType.STRING)
    private EstadoProcessamentoEnum estado;
    @OneToMany(mappedBy = "agrupaVO", fetch = FetchType.LAZY)
    private List<AgrupaCampoVO> camposParaAgrupar;
    private LocalDateTime dtInicio;
    private LocalDateTime dtFim;
    @Column(nullable = false)
    private Long numeroLinhasProcessadas;
    @Column(nullable = true)
    private String jobId;
    private String nomeTarefa;

    public String getNomeTarefas() {
        return getEstado().toString();
    }

    public List<AgrupaCampoVO> getCamposOrdenados() {
        return this
                .getCamposParaAgrupar()
                .stream()
                .sorted((o1, o2) -> o1.getOrdem().compareTo(o2.getOrdem()))
                .toList();
    }

}
