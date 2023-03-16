package br.com.spedison.digestor_csv.repository;

import br.com.spedison.digestor_csv.model.FiltroCriterioVO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FiltroComparadorRepository extends JpaRepository<FiltroCriterioVO,Long> {


    List<FiltroCriterioVO> findAllByFiltroVO_Id(long id);

    void removeByIdAndFiltroVO_Id(long id, long filtroId);
}
