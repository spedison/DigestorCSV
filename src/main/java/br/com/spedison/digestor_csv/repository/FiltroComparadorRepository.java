package br.com.spedison.digestor_csv.repository;

import br.com.spedison.digestor_csv.model.FiltroComparadorVO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FiltroComparadorRepository extends JpaRepository<FiltroComparadorVO,Long> {


    List<FiltroComparadorVO> findAllByFiltroVO_Id(long id);

    void removeByIdAndFiltroVO_Id(long id, long filtroId);
}
