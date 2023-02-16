package br.com.spedison.digestor_csv.repository;

import br.com.spedison.digestor_csv.model.EstadoProcessamentoEnum;
import br.com.spedison.digestor_csv.model.RemoveColunasVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RemoveColunasRepository extends JpaRepository<RemoveColunasVO, Long> {

    @Query("SELECT f FROM RemoveColunasVO f join fetch f.camposParaRemover c")
    List<RemoveColunasVO> listaTodosComCampos();

    @Query("SELECT a FROM RemoveColunasVO a ")
    List<RemoveColunasVO> listaTodosSemCampos();

    @Query("SELECT a FROM RemoveColunasVO a join fetch a.camposParaRemover where a.id = :id")
    RemoveColunasVO buscaPorIdComCampos(Long id);

    @Query("SELECT a FROM RemoveColunasVO a where a.id = :id")
    RemoveColunasVO buscaPorIdSemCampos(Long id);

    @Query("delete from RemoveColunasCampoVO c where c.id = :idCampo and c.removeColunasVO.id = :id")
    @Modifying
    Integer deleteCampoById(Long id, Long idCampo);

    @Modifying
    @Query("delete from RemoveColunasCampoVO c where c.removeColunasVO.id = :idAgrupa")
    Integer deleteTodosCampos(Long idAgrupa);

    @Modifying
    @Query("update from RemoveColunasVO a set a.jobId = :jobId, a.estado = :estadoProcessamento where a.id = :id")
    Integer updateJob(Long id, String jobId, EstadoProcessamentoEnum estadoProcessamento);

    @Modifying
    @Query("update from RemoveColunasVO a set  a.numeroLinhasProcessadas = :linhasProcessadas where a.id = :id")
    Integer atualizaLinhasProcessadas(Long id, Long linhasProcessadas);

    @Modifying
    @Query("update from RemoveColunasVO a set  a.diretorioEntrada = :nomeDiretorio where a.id = :id")
    Integer atualizaDiretoriosEntrada(Long id, String nomeDiretorio);
}
