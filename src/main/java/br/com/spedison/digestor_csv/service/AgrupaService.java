package br.com.spedison.digestor_csv.service;

import br.com.spedison.digestor_csv.infra.FormatadorData;
import br.com.spedison.digestor_csv.model.*;
import br.com.spedison.digestor_csv.repository.AgrupaCampoRepository;
import br.com.spedison.digestor_csv.repository.AgrupaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

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

    public AgrupaVO criaNovoAgrupa(String diretorioEntrada) {
        AgrupaVO ret = new AgrupaVO();
        String dirSaida = configuracaoService.getConfiguracao(ConfiguracaoVO.nomes[2]);
        List<AgrupaCampoVO> campos = new LinkedList<>();
        ret.setId(null);
        ret.setEstado(EstadoProcessamentoEnum.NAO_INICIADO);
        ret.setDataCriacao(LocalDateTime.now());
        ret.setDiretorioEntrada(diretorioEntrada);
        ret.setDiretorioSaida(Paths.get(dirSaida, "agrupa", formatadorData.formataDataParaArquivos(ret.getDataCriacao())).toString());
        ret.setCamposParaAgrupar(campos);
        ret.setNumeroLinhasProcessadas(0L);
        return agrupaRepository.save(ret);
    }

    public List<Integer> pegaCamposParaAgrupar(Long idAgrupa) {
        return agrupaRepository
                .buscaPorIdComCampos(idAgrupa)
                .getCamposParaAgrupar()
                .stream()
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
    public Integer salvaDiretorios(AgrupaVO agrupaVO) {
        return agrupaRepository.atualizaDiretoriosEntrada(agrupaVO.getId(), agrupaVO.getDiretorioEntrada());
    }

    @Transactional
    public AgrupaVO registrarInicioProcessamento(Long idAgrupa, String jobId) {
        AgrupaVO f = agrupaRepository.buscaPorIdSemCampos(idAgrupa);
        if (f == null)
            return null;
        f.setEstado(EstadoProcessamentoEnum.INICIANDO);
        f.setDtInicio(LocalDateTime.now());
        f.setJobId(jobId);
        return agrupaRepository.save(f);
    }

    @Transactional
    public AgrupaVO registrarFimProcessamento(Long idAgrupa, Long numeroDeLinhasProcessadas) {
        AgrupaVO f = agrupaRepository.buscaPorIdSemCampos(idAgrupa);
        if (f == null)
            return null;
        f.setEstado(EstadoProcessamentoEnum.TERMINADO);
        f.setDtFim(LocalDateTime.now());
        f.setNumeroLinhasProcessadas(numeroDeLinhasProcessadas);
        return agrupaRepository.save(f);
    }

    @Transactional
    public AgrupaVO registrarProcessando(Long idAgrupa) {
        AgrupaVO f = agrupaRepository.buscaPorIdSemCampos(idAgrupa);
        if (f == null)
            return null;
        f.setEstado(EstadoProcessamentoEnum.PROCESSANDO);
        return agrupaRepository.save(f);
    }

    public AgrupaVO getAgrupaComCampos(long id) {
        return agrupaRepository.buscaPorIdComCampos(id);
    }

    public AgrupaVO getAgrupaSemCampos(long id) {
        return agrupaRepository.buscaPorIdSemCampos(id);
    }

    @Transactional
    public AgrupaCampoVO adicionaCampos(AgrupaCampoVO campo) {
        if (campo.getAgrupaVO() == null)
            return null;
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
}
