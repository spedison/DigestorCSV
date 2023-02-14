package br.com.spedison.digestor_csv.repository;

import br.com.spedison.digestor_csv.model.RemoveColunasCampoVO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RemoveColunasCampoRepository extends JpaRepository<RemoveColunasCampoVO, Long> {
    List<RemoveColunasCampoVO> findAllByRemoveColunasVO_Id(long id);
    void removeByIdAndRemoveColunasVO_Id(long id, long filtroId);
}
