package br.com.spedison.digestor_csv.service;

import br.com.spedison.digestor_csv.infra.FormatadorData;
import br.com.spedison.digestor_csv.infra.ResumoColunasVoUtils;
import br.com.spedison.digestor_csv.model.*;
import br.com.spedison.digestor_csv.repository.ResumoColunasCampoRepository;
import br.com.spedison.digestor_csv.repository.ResumoColunasRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Service
public class ResumoColunasService {
    @Autowired
    ResumoColunasRepository resumoRepository;
    @Autowired
    ConfiguracaoService configuracaoService;
    @Autowired
    ResumoColunasCampoRepository resumoCamposRepository;
    @Autowired
    FormatadorData formatadorData;

    @Autowired
    ResumoColunasVoUtils resumoColunasVoUtils;

    public ResumoColunasVO criaNovo(String diretorioEntrada) {
        ResumoColunasVO ret = new ResumoColunasVO();
        String dirSaida = configuracaoService.getConfiguracao(ConfiguracaoVO.nomes[2]);
        List<ResumoColunasCampoVO> campos = new LinkedList<>();
        ret.setId(null);
        ret.setNomeDaTarefa("Nova Tarefa");
        ret.setEstado(EstadoProcessamentoEnum.NAO_INICIADO);
        ret.setDataCriacao(LocalDateTime.now());
        ret.setDiretorioEntrada(diretorioEntrada);
        resumoColunasVoUtils.formataDirSaida(ret, dirSaida);
        ret.setCamposParaResumir(campos);
        ret.setNumeroLinhasProcessadas(0L);
        List<ResumoColunasCampoVO> lst = new LinkedList<>();
        ret.setCamposParaResumir(lst);
        return resumoRepository.save(ret);
    }

    public List<Integer> pegaCampos(Long id) {
        return resumoRepository
                .buscaPorIdComCampos(id)
                .getCamposParaResumir()
                .stream()
                .map(ResumoColunasCampoVO::getNumeroColuna)
                .toList();
    }

    @Transactional
    public boolean remove(Long id) {
        Integer contaC = resumoRepository.deleteTodosCampos(id);
        resumoRepository.deleteById(id);
        return true;
    }

    public List<ResumoColunasVO> listar() {
        return resumoRepository.findAll();
    }

    @Transactional
    public Integer salvaDiretoriosECampoSumarizado(ResumoColunasVO resumoCamposVO) {
        String dirSaida = configuracaoService.getDirSaida();
        resumoColunasVoUtils.formataDirSaida(resumoCamposVO, dirSaida);
        return resumoRepository.atualizaDiretorioECampoResumo(
                resumoCamposVO.getId(),
                resumoCamposVO.getNomeDaTarefa(),
                resumoCamposVO.getDiretorioEntrada(),
                resumoCamposVO.getDiretorioSaida(),
                resumoCamposVO.getNumeroColunaSumarizada(),
                resumoCamposVO.getNomeColunaSumarizada());
    }

    @Transactional
    public ResumoColunasVO registrarInicioProcessamento(Long id, String jobId) {
        ResumoColunasVO f = resumoRepository.buscaPorIdSemCampos(id);
        if (f == null)
            return null;
        f.setEstado(EstadoProcessamentoEnum.INICIANDO);
        f.setDtInicio(LocalDateTime.now());
        f.setJobId(jobId);
        return resumoRepository.save(f);
    }

    @Transactional
    public ResumoColunasVO registrarFimProcessamento(Long id, Long numeroDeLinhasProcessadas) {
        ResumoColunasVO f = resumoRepository.buscaPorIdSemCampos(id);
        if (f == null)
            return null;
        f.setEstado(EstadoProcessamentoEnum.TERMINADO);
        f.setDtFim(LocalDateTime.now());
        f.setNumeroLinhasProcessadas(numeroDeLinhasProcessadas);
        return resumoRepository.save(f);
    }

    @Transactional
    public ResumoColunasVO registrarProcessando(Long id) {
        ResumoColunasVO f = resumoRepository.buscaPorIdSemCampos(id);
        if (f == null)
            return null;
        f.setEstado(EstadoProcessamentoEnum.PROCESSANDO);
        return resumoRepository.save(f);
    }

    public ResumoColunasVO getResumoComCampos(long id) {
        return resumoRepository.buscaPorIdComCampos(id);
    }

    public ResumoColunasVO getResumoSemCampos(long id) {
        return resumoRepository.buscaPorIdSemCampos(id);
    }

    @Transactional
    public ResumoColunasCampoVO adicionaCampos(ResumoColunasCampoVO campo) {
        if (campo.getResumoColunasVO() == null)
            return null;
        return resumoCamposRepository.save(campo);
    }

    public List<ResumoColunasCampoVO> getCampos(long id) {
        List<ResumoColunasCampoVO> ret = resumoCamposRepository.findAllByResumoColunasVO_Id(id);
        if (ret == null)
            ret = new LinkedList<>();
        return ret;
    }

    @Transactional
    public void atualizaLinhasProcessadas(Long id, Long linhasProcessadas) {
        resumoRepository.atualizaLinhasProcessadas(id, linhasProcessadas);
    }

    @Transactional
    public void removeCampo(long id, long idCampo) {
        resumoCamposRepository.removeByIdAndResumoColunasVO_Id(idCampo, id);
    }

    public ResumoColunasVO copiar(long id) {
        ResumoColunasVO v = resumoRepository.buscaPorIdComCampos(id);
        String dirSaidaConfiguracao = configuracaoService.getDirSaida();

        if (v == null)
            return null;

        ResumoColunasVO novo = new ResumoColunasVO();
        BeanUtils.copyProperties(v, novo);

        novo.setNumeroLinhasProcessadas(0L);
        novo.setCamposParaResumir(new LinkedList<>());
        v.getCamposParaResumir().forEach(p -> {
            ResumoColunasCampoVO pp = new ResumoColunasCampoVO();
            BeanUtils.copyProperties(p,pp);
            pp.setId(null);
            pp.setResumoColunasVO(novo);
            novo.getCamposParaResumir().add(pp);
        });
        novo.setEstado(EstadoProcessamentoEnum.NAO_INICIADO);
        novo.setDataCriacao(LocalDateTime.now());
        novo.setId(null);
        resumoColunasVoUtils.formataDirSaida(novo, dirSaidaConfiguracao);
        resumoRepository.save(novo);
        resumoCamposRepository.saveAll(novo.getCamposParaResumir());
        return novo;
    }
}