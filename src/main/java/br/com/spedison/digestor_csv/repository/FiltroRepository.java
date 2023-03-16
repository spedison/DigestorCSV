package br.com.spedison.digestor_csv.repository;

import br.com.spedison.digestor_csv.model.EstadoProcessamentoEnum;
import br.com.spedison.digestor_csv.model.FiltroVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FiltroRepository extends JpaRepository<FiltroVO, Long> {

    @Query("SELECT f FROM FiltroVO f join fetch f.comparadores c")
    List<FiltroVO> listaTodosComComparadores();

    @Query("SELECT f FROM FiltroVO f join fetch f.comparadores c where f.id = :id")
    FiltroVO buscaPorIdComComparador(Long id);

    @Query("delete from FiltroVO f where f.id = :id")
    @Modifying
    Integer deleteFiltroById(Long id);

    @Query("delete from FiltroCriterioVO c where c.id = :idComparador and c.filtroVO.id = :idFiltro")
    @Modifying
    Integer deleteComparadorDoFiltroById(Long idFiltro, Long idComparador);

    @Modifying
    @Query("delete from FiltroCriterioVO c where c.filtroVO.id = :idFiltro")
    Integer deleteTodosComparadorDoFiltro(Long idFiltro);

    @Modifying
    @Query("update from FiltroVO f set f.jobId = :jobId, f.estado = :estadoProcessamento where f.id = :idFiltro")
    Integer updateJobDoFiltro(Long idFiltro, String jobId, EstadoProcessamentoEnum estadoProcessamento);

    @Modifying
    @Query("update from FiltroVO f set f.id = :idFiltro, f.numeroLinhasProcessadas= :linhasProcessadas where f.id = :idFiltro")
    void atualizaLinhasProcessadas(Long idFiltro, Long linhasProcessadas);
}