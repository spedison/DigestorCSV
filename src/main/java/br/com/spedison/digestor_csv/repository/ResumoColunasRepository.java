package br.com.spedison.digestor_csv.repository;

import br.com.spedison.digestor_csv.model.EstadoProcessamentoEnum;
import br.com.spedison.digestor_csv.model.ResumoColunasVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ResumoColunasRepository extends JpaRepository<ResumoColunasVO, Long> {

    @Query("SELECT a FROM ResumoColunasVO a join fetch a.camposParaResumir where a.id = :id")
    ResumoColunasVO buscaPorIdComCampos(Long id);

    @Query("SELECT a FROM ResumoColunasVO a where a.id = :id")
    ResumoColunasVO buscaPorIdSemCampos(Long id);

    @Query("delete from ResumoColunasCampoVO c where c.id = :idCampo and c.resumoColunasVO.id = :id")
    @Modifying
    Integer deleteCampoById(Long id, Long idCampo);

    void deleteById(Long id);

    @Modifying
    @Query("delete from ResumoColunasCampoVO c where c.resumoColunasVO.id = :id")
    Integer deleteTodosCampos(Long id);

    @Modifying
    @Query("update from ResumoColunasVO a set a.jobId = :jobId, a.estado = :estadoProcessamento where a.id = :id")
    Integer updateJob(Long id, String jobId, EstadoProcessamentoEnum estadoProcessamento);

    @Modifying
    @Query("update from ResumoColunasVO a set  a.numeroLinhasProcessadas= :linhasProcessadas where a.id = :id")
    Integer atualizaLinhasProcessadas(Long id, Long linhasProcessadas);

    @Modifying
    @Query("update from ResumoColunasVO a set  a.diretorioEntrada= :nomeDiretorio, a.numeroLinhasProcessadas = :numeroCampo, a.nomeColunaSumarizada = :nomeCampo where a.id = :id")
    Integer atualizaDiretorioCampoResumo(Long id, String nomeDiretorio, Integer numeroCampo, String nomeCampo);
}