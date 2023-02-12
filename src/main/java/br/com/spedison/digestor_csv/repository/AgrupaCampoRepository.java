package br.com.spedison.digestor_csv.repository;

import br.com.spedison.digestor_csv.model.AgrupaCampoVO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgrupaCampoRepository extends JpaRepository<AgrupaCampoVO, Long> {
    List<AgrupaCampoVO> findAllByAgrupaVO_Id(long id);
    void removeByIdAndAgrupaVO_Id(long id, long filtroId);
}
