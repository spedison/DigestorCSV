package br.com.spedison.digestor_csv.dto;

import br.com.spedison.digestor_csv.model.EstadoProcessamentoEnum;
import br.com.spedison.digestor_csv.model.ResumoColunasCampoVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResumoColunasDTO {
    private Long id;
    private String nomeDaTarefa;
    private String diretorioEntrada;
    private String diretorioSaida;
    private LocalDateTime dataCriacao;
    private EstadoProcessamentoEnum estado;
    private LocalDateTime dtInicio;
    private LocalDateTime dtFim;
    private String colunaSumarizada; // Numero e Nome da Coluna Sumarizada
}
