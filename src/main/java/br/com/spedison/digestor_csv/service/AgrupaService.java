package br.com.spedison.digestor_csv.service;

import br.com.spedison.digestor_csv.infra.AgrupaVoUtils;
import br.com.spedison.digestor_csv.infra.FormatadorData;
import br.com.spedison.digestor_csv.model.AgrupaCampoVO;
import br.com.spedison.digestor_csv.model.AgrupaVO;
import br.com.spedison.digestor_csv.model.EstadoProcessamentoEnum;
import br.com.spedison.digestor_csv.repository.AgrupaCampoRepository;
import br.com.spedison.digestor_csv.repository.AgrupaRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class AgrupaService {
    @Autowired
    AgrupaRepository agrupaRepository;
    @Autowired
    ConfiguracaoService configuracaoService;
    @Autowired
    AgrupaCampoRepository agrupaCamposRepository;

    @Autowired
    FormatadorData formatadorData;

    @Autowired
    AgrupaVoUtils agrupaVoUtils;

    public AgrupaVO criaNovoAgrupa(String diretorioEntrada) {
        AgrupaVO ret = new AgrupaVO();
        List<AgrupaCampoVO> campos = new LinkedList<>();
        ret.setId(null);
        ret.setEstado(EstadoProcessamentoEnum.NAO_INICIADO);
        ret.setDataCriacao(LocalDateTime.now());
        ret.setDiretorioEntrada(diretorioEntrada);
        ret.setNomeTarefa("Tarefa Nova");
        ret.setCamposParaAgrupar(campos);
        ret.setNumeroLinhasProcessadas(0L);
        defineDiretorioSaida(ret);
        return agrupaRepository.save(ret);
    }

    private void defineDiretorioSaida(AgrupaVO ret) {
        String dirSaida = configuracaoService.getDirSaida();
        agrupaVoUtils.formataDirSaida(ret,dirSaida);
    }

    public List<Integer> pegaCamposParaAgrupar(Long idAgrupa) {
        return agrupaRepository
                .buscaPorIdComCampos(idAgrupa)
                .getCamposParaAgrupar()
                .stream()
                .sorted((o1, o2) -> { // Ordena pelo campo Ordem.
                    return o1.getOrdem().compareTo(o2.getOrdem());
                })
                .map(AgrupaCampoVO::getNumeroColuna)
                .toList();
    }

    @Transactional
    public boolean removeAgrupa(Long agrupaId) {
        Integer contaC = agrupaRepository.deleteTodosComparadorDoFiltro(agrupaId);
        Integer contaF = agrupaRepository.deleteAgrupoById(agrupaId);
        return contaC >= 0 && contaF == 1;
    }

    public List<AgrupaVO> listar() {
        return agrupaRepository.findAll();
    }

    @Transactional
    public Integer gravaDiretoriosENomeDaTarefa(AgrupaVO agrupaVO) {
        Optional<AgrupaVO> agrupaBanco = agrupaRepository.buscaPorIdSemCampos(agrupaVO.getId());
        if (agrupaBanco.isEmpty())
            return -1;
        agrupaVO.setDataCriacao(agrupaBanco.get().getDataCriacao());
        defineDiretorioSaida(agrupaVO);
        return agrupaRepository.atualizaDiretoriosETarefa(
                agrupaVO.getId(),
                agrupaVO.getDiretorioEntrada(),
                agrupaVO.getDiretorioSaida(),
                agrupaVO.getNomeTarefa());
    }

    @Transactional
    public AgrupaVO registrarInicioProcessamento(Long idAgrupa, String jobId) {
        Optional<AgrupaVO> f = agrupaRepository.buscaPorIdSemCampos(idAgrupa);
        if (f.isEmpty())
            return null;
        f.get().setEstado(EstadoProcessamentoEnum.INICIANDO);
        f.get().setDtInicio(LocalDateTime.now());
        f.get().setJobId(jobId);
        return agrupaRepository.save(f.get());
    }

    @Transactional
    public AgrupaVO registrarFimProcessamento(Long idAgrupa, Long numeroDeLinhasProcessadas) {
        Optional<AgrupaVO> f = agrupaRepository.buscaPorIdSemCampos(idAgrupa);
        if (f.isEmpty())
            return null;
        f.get().setEstado(EstadoProcessamentoEnum.TERMINADO);
        f.get().setDtFim(LocalDateTime.now());
        f.get().setNumeroLinhasProcessadas(numeroDeLinhasProcessadas);
        return agrupaRepository.save(f.get());
    }

    @Transactional
    public AgrupaVO registrarProcessando(Long idAgrupa) {
        Optional<AgrupaVO> f = agrupaRepository.buscaPorIdSemCampos(idAgrupa);
        if (f.isEmpty())
            return null;
        f.get().setEstado(EstadoProcessamentoEnum.PROCESSANDO);
        return agrupaRepository.save(f.get());
    }

    public AgrupaVO getAgrupaComCampos(long id) {
        return agrupaRepository.buscaPorIdComCampos(id);
    }

    public AgrupaVO getAgrupaSemCampos(long id) {
        Optional<AgrupaVO> ret = agrupaRepository.buscaPorIdSemCampos(id);
        if (ret.isEmpty()) {
            throw new RegistroNaoLocalizadoException("Agrupamento " + id + " Não localizado");
        }
        return ret.get();
    }

    @Transactional
    public AgrupaCampoVO adicionaCampos(AgrupaCampoVO campo) {

        if (campo.getAgrupaVO() == null)
            return null;

        Integer proximaOrdem = agrupaRepository.getProximaOrdem(campo.getAgrupaVO().getId());
        campo.setOrdem(proximaOrdem == null ? 1 : proximaOrdem);
        return agrupaCamposRepository.save(campo);
    }

    public List<AgrupaCampoVO> getComparadoresDoFiltro(long id) {
        return agrupaCamposRepository.findAllByAgrupaVO_Id(id);
    }

    @Transactional
    public void atualizaLinhasProcessadas(Long idAgrupa, Long linhasProcessadas) {
        agrupaRepository.atualizaLinhasProcessadas(idAgrupa, linhasProcessadas);
    }

    @Transactional
    public void removeCampo(long idAgrupa, long idCampo) {
        agrupaCamposRepository.removeByIdAndAgrupaVO_Id(idCampo, idAgrupa);
    }

    @Transactional
    public AgrupaVO copiar(long idFiltro) {
        AgrupaVO v = agrupaRepository.buscaPorIdComCampos(idFiltro);

        if (v == null)
            return null;

        AgrupaVO novo = new AgrupaVO();
        BeanUtils.copyProperties(v, novo);

        novo.setNumeroLinhasProcessadas(0L);
        novo.setCamposParaAgrupar(new LinkedList<>());
        v.getCamposParaAgrupar().forEach(p -> {
            AgrupaCampoVO pp = new AgrupaCampoVO();
            BeanUtils.copyProperties(p, pp);
            pp.setId(null);
            pp.setAgrupaVO(novo);
            novo.getCamposParaAgrupar().add(pp);
        });
        novo.setEstado(EstadoProcessamentoEnum.NAO_INICIADO);
        novo.setDataCriacao(LocalDateTime.now());
        novo.setId(null);
        defineDiretorioSaida(novo);
        agrupaRepository.save(novo);
        agrupaCamposRepository.saveAll(novo.getCamposParaAgrupar());
        return novo;
    }

}
