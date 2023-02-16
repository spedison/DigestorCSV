package br.com.spedison.digestor_csv.repository;

import br.com.spedison.digestor_csv.model.RemoveColunasCampoVO;
import br.com.spedison.digestor_csv.model.ResumoColunasCampoVO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumoColunasCampoRepository extends JpaRepository<ResumoColunasCampoVO, Long> {
    List<ResumoColunasCampoVO> findAllByResumoColunasVO_Id(long id);
    void removeByIdAndResumoColunasVO_Id(long id, long resumoId);
}
