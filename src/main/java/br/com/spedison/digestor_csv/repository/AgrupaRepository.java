package br.com.spedison.digestor_csv.repository;

import br.com.spedison.digestor_csv.model.AgrupaVO;
import br.com.spedison.digestor_csv.model.EstadoProcessamentoEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AgrupaRepository extends JpaRepository<AgrupaVO, Long> {

    //@Query("SELECT f FROM AgrupaVO f join fetch f.camposParaAgrupar c")
    //List<AgrupaVO> listaTodosComCampos();

    //@Query("SELECT a FROM AgrupaVO a ")
    //List<AgrupaVO> listaTodosSemCampos();

    @Query("SELECT a FROM AgrupaVO a join fetch a.camposParaAgrupar where a.id = :id")
    AgrupaVO buscaPorIdComCampos(Long id);

    @Query("SELECT a FROM AgrupaVO a where a.id = :id")
    Optional<AgrupaVO> buscaPorIdSemCampos(Long id);

    @Query("select max(a.ordem)+1 from AgrupaCampoVO a where a.agrupaVO.id = :idAgrupa")
    Integer getProximaOrdem(Long idAgrupa);


    @Query("delete from AgrupaVO a where a.id = :id")
    @Modifying
    Integer deleteAgrupoById(Long id);

    @Query("delete from AgrupaCampoVO c where c.id = :idCampo and c.agrupaVO.id = :idAgrupa")
    @Modifying
    Integer deleteCampoDoAgrupaById(Long idAgrupa, Long idCampo);

    @Modifying
    @Query("delete from AgrupaCampoVO c where c.agrupaVO.id = :idAgrupa")
    Integer deleteTodosComparadorDoFiltro(Long idAgrupa);

    @Modifying
    @Query("update from AgrupaVO a set a.jobId = :jobId, a.estado = :estadoProcessamento where a.id = :idAgrupa")
    Integer updateJobDoFiltro(Long idAgrupa, String jobId, EstadoProcessamentoEnum estadoProcessamento);

    @Modifying
    @Query("update from AgrupaVO a set  a.numeroLinhasProcessadas= :linhasProcessadas where a.id = :idAgrupa")
    Integer atualizaLinhasProcessadas(Long idAgrupa, Long linhasProcessadas);

    @Modifying
    @Query("update from AgrupaVO a set  a.diretorioEntrada= :nomeDiretorioEntrada, a.diretorioSaida= :nomeDiretorioSaida, a.nomeTarefa = :nomeTarefa where a.id = :idAgrupa")
    Integer atualizaDiretoriosETarefa(Long idAgrupa, String nomeDiretorioEntrada, String nomeDiretorioSaida, String nomeTarefa);



}
