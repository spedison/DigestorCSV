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
public class ResumoColunasVO {

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
    @OneToMany(mappedBy = "resumoColunasVO", fetch = FetchType.LAZY)
    private List<ResumoColunasCampoVO> camposParaResumir;
    private LocalDateTime dtInicio;
    private LocalDateTime dtFim;
    @Column(nullable = false)
    private Long numeroLinhasProcessadas;
    private Integer numercoColunaSumarizada;
    private String nomeColunaSumarizada;

    @Column(nullable = true)
    private String jobId;

    public String getNomeTarefas(){
        if (getJobId() == null || getJobId().isBlank())
            return "Sem tarefa associada.";
        return " Tarefa :: " + getJobId();
    }

}
