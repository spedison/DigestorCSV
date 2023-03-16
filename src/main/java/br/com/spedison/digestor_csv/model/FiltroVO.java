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
@Table(name = "tb_filtro")
public class FiltroVO {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 255, nullable = false)
    private String nomeDaTarefa;

    @Column(nullable = false)
    private String diretorioEntrada;
    @Column(nullable = false)
    private String diretorioSaida;
    @Column(nullable = false)
    private LocalDateTime dataCriacao;
    @Enumerated(EnumType.STRING)
    private EstadoProcessamentoEnum estado;
    @OneToMany(mappedBy = "filtroVO", fetch = FetchType.LAZY)
    private List<FiltroCriterioVO> comparadores;
    private LocalDateTime dtInicio;
    private LocalDateTime dtFim;

    @Column(nullable = false)
    private Long numeroLinhasProcessadas;

    @Column(nullable = false)
    private Boolean todasCondicoesDevemAtender;

    @Column(nullable = true)
    private String jobId;

    public String getNomeTarefas(){
        return "";
        //return getEstado().toString();
        //if (getJobId() == null || getJobId().isBlank())
        //    return "";
        //return " Tarefa :: " + getJobId();
    }

}
