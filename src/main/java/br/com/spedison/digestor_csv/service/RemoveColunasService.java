package br.com.spedison.digestor_csv.service;

import br.com.spedison.digestor_csv.infra.FormatadorData;
import br.com.spedison.digestor_csv.model.ConfiguracaoVO;
import br.com.spedison.digestor_csv.model.EstadoProcessamentoEnum;
import br.com.spedison.digestor_csv.model.RemoveColunasCampoVO;
import br.com.spedison.digestor_csv.model.RemoveColunasVO;
import br.com.spedison.digestor_csv.repository.RemoveColunasCampoRepository;
import br.com.spedison.digestor_csv.repository.RemoveColunasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Service
public class RemoveColunasService {
    @Autowired
    RemoveColunasRepository removeColunasRepository;
    @Autowired
    ConfiguracaoService configuracaoService;
    @Autowired
    RemoveColunasCampoRepository removeColunasCamposRepository;

    @Autowired
    FormatadorData formatadorData;

    public RemoveColunasVO criaNovo(String diretorioEntrada) {
        RemoveColunasVO ret = new RemoveColunasVO();
        String dirSaida = configuracaoService.getConfiguracao(ConfiguracaoVO.nomes[2]);
        List<RemoveColunasCampoVO> campos = new LinkedList<>();
        ret.setId(null);
        ret.setEstado(EstadoProcessamentoEnum.NAO_INICIADO);
        ret.setDataCriacao(LocalDateTime.now());
        ret.setDiretorioEntrada(diretorioEntrada);
        ret.setDiretorioSaida(Paths.get(dirSaida, "remove_colunas", formatadorData.formataDataParaArquivos(ret.getDataCriacao())).toString());
        ret.setCamposParaRemover(campos);
        ret.setNumeroLinhasProcessadas(0L);
        return removeColunasRepository.save(ret);
    }

    public List<Integer> pegaCamposParaAgrupar(Long idAgrupa) {
        return removeColunasRepository
                .buscaPorIdComCampos(idAgrupa)
                .getCamposParaRemover()
                .stream()
                .map(RemoveColunasCampoVO::getNumeroColuna)
                .toList();
    }

    @Transactional
    public void remove(Long id) {
        Integer contaC = removeColunasRepository.deleteTodosCampos(id);
        removeColunasRepository.deleteById(id);
    }

    public List<RemoveColunasVO> listar() {
        return removeColunasRepository.findAll();
    }

    @Transactional
    public Integer salvaDiretorios(RemoveColunasVO agrupaVO) {
        return removeColunasRepository.atualizaDiretoriosEntrada(agrupaVO.getId(), agrupaVO.getDiretorioEntrada());
    }

    @Transactional
    public RemoveColunasVO registrarInicioProcessamento(Long id, String jobId) {
        RemoveColunasVO f = removeColunasRepository.buscaPorIdSemCampos(id);
        if (f == null)
            return null;
        f.setEstado(EstadoProcessamentoEnum.INICIANDO);
        f.setDtInicio(LocalDateTime.now());
        f.setJobId(jobId);
        return removeColunasRepository.save(f);
    }

    @Transactional
    public RemoveColunasVO registrarFimProcessamento(Long idAgrupa, Long numeroDeLinhasProcessadas) {
        RemoveColunasVO f = removeColunasRepository.buscaPorIdSemCampos(idAgrupa);
        if (f == null)
            return null;
        f.setEstado(EstadoProcessamentoEnum.TERMINADO);
        f.setDtFim(LocalDateTime.now());
        f.setNumeroLinhasProcessadas(numeroDeLinhasProcessadas);
        return removeColunasRepository.save(f);
    }

    @Transactional
    public RemoveColunasVO registrarProcessando(Long idAgrupa) {
        RemoveColunasVO f = removeColunasRepository.buscaPorIdSemCampos(idAgrupa);
        if (f == null)
            return null;
        f.setEstado(EstadoProcessamentoEnum.PROCESSANDO);
        return removeColunasRepository.save(f);
    }

    public RemoveColunasVO getRemoveColunasComCampos(long id) {
        return removeColunasRepository.buscaPorIdComCampos(id);
    }

    public RemoveColunasVO getAgrupaSemCampos(long id) {
        return removeColunasRepository.buscaPorIdSemCampos(id);
    }

    @Transactional
    public RemoveColunasCampoVO adicionaCampos(RemoveColunasCampoVO campo) {
        if (campo.getRemoveColunasVO() == null)
            return null;
        return removeColunasCamposRepository.save(campo);
    }

    @Transactional
    public void atualizaLinhasProcessadas(Long idAgrupa, Long linhasProcessadas) {
        removeColunasRepository.atualizaLinhasProcessadas(idAgrupa, linhasProcessadas);
    }

    @Transactional
    public void removeCampo(long idRemoveColunas, long idCampo) {
        removeColunasCamposRepository.removeByIdAndRemoveColunasVO_Id(idCampo, idRemoveColunas);
    }

}