package br.com.spedison.digestor_csv.service;

import br.com.spedison.digestor_csv.infra.FileUtils;
import br.com.spedison.digestor_csv.infra.FormatadorData;
import br.com.spedison.digestor_csv.model.ConfiguracaoVO;
import br.com.spedison.digestor_csv.model.EstadoProcessamentoEnum;
import br.com.spedison.digestor_csv.model.FiltroCriterioVO;
import br.com.spedison.digestor_csv.model.FiltroVO;
import br.com.spedison.digestor_csv.repository.FiltroComparadorRepository;
import br.com.spedison.digestor_csv.repository.FiltroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class FiltroService {

    @Autowired
    FiltroRepository filtroRepository;

    @Autowired
    ConfiguracaoService configuracaoService;

    @Autowired
    FiltroComparadorRepository filtroComparadorRepository;

    @Autowired
    FormatadorData formatadorData;

    public FiltroVO criaNovoFiltro(String diretorioEntrada) {
        FiltroVO ret = new FiltroVO();
        String dirSaida = configuracaoService.getConfiguracao(ConfiguracaoVO.nomes[2]);

        //ComparadorVO comp = new ComparadorVO(null, ret, TipoComparacaoEnum.TXT_CONTEM, "teste", null, null, -1, "TODALINHA");
        List<FiltroCriterioVO> comparadores = new LinkedList<>();
        //comparadores.add(comp);
        ret.setId(null);
        ret.setEstado(EstadoProcessamentoEnum.NAO_INICIADO);
        ret.setDataCriacao(LocalDateTime.now());
        ret.setDiretorioEntrada(diretorioEntrada);
        ret.setNomeDaTarefa("Nome da Tarefa");
        ret.setDiretorioSaida(
                Paths.get(
                                dirSaida, "filtro",
                                FileUtils.ajustaNome(ret.getNomeTarefas()),
                                formatadorData.formataDataParaArquivos(ret.getDataCriacao()))
                        .toString());
        ret.setComparadores(comparadores);
        ret.setNumeroLinhasProcessadas(0L);
        ret.setTodasCondicoesDevemAtender(false);
        return filtroRepository.save(ret);
    }

    @Transactional
    public boolean removeFiltro(Long filtroId) {
        Integer contaC = filtroRepository.deleteTodosComparadorDoFiltro(filtroId);
        Integer contaF = filtroRepository.deleteFiltroById(filtroId);
        return contaC >= 0 && contaF == 1;
    }

    public List<FiltroVO> listar() {
        return filtroRepository.findAll();
    }

    @Transactional
    public FiltroVO gravaDadosDoFiltro(FiltroVO filtroVO) {
        return atualizaDiretorios(filtroVO);
    }

    @Transactional
    public FiltroVO atualizaDiretorios(FiltroVO filtroVO) {
        Optional<FiltroVO> paraSalvar = filtroRepository.findById(filtroVO.getId());
        String dirSaida = configuracaoService.getConfiguracao(ConfiguracaoVO.nomes[2]);
        if (paraSalvar.isPresent()) {
            paraSalvar.get().setNomeDaTarefa(filtroVO.getNomeDaTarefa());
            paraSalvar.get().setDiretorioEntrada(filtroVO.getDiretorioEntrada());
            paraSalvar.get().setTodasCondicoesDevemAtender(filtroVO.getTodasCondicoesDevemAtender());
            paraSalvar.get().setDiretorioSaida(
                    Paths.get(
                                    dirSaida, "filtro",
                                    FileUtils.ajustaNome(
                                            paraSalvar.get().getNomeDaTarefa()),
                                    formatadorData.formataDataParaArquivos(paraSalvar.get().getDataCriacao()))
                            .toString());
            filtroRepository.save(paraSalvar.get());
            return paraSalvar.get();
        }
        return null;
    }

    @Transactional
    public FiltroVO criaFiltroESalvaDiretorios(FiltroVO filtroVO) {
        FiltroVO f = new FiltroVO(
                null, "Filtro Tarefa 1", filtroVO.getDiretorioEntrada(),
                filtroVO.getDiretorioSaida(), LocalDateTime.now(),
                EstadoProcessamentoEnum.NAO_INICIADO,
                new LinkedList<FiltroCriterioVO>(), null, null, -1L, true, null);
        return filtroRepository.save(f);
    }

    @Transactional
    public FiltroVO registrarInicioProcessamento(Long idFiltro, String jobId) {
        FiltroVO f = getFiltroSemComparadores(idFiltro);
        if (f == null)
            return null;
        f.setEstado(EstadoProcessamentoEnum.INICIANDO);
        f.setDtInicio(LocalDateTime.now());
        f.setJobId(jobId);
        return filtroRepository.save(f);
    }

    @Transactional
    public FiltroVO registrarFimProcessamento(Long idFiltro, Long numeroDeLinhasProcessadas) {
        FiltroVO f = getFiltroSemComparadores(idFiltro);
        if (f == null)
            return null;
        f.setEstado(EstadoProcessamentoEnum.TERMINADO);
        f.setDtFim(LocalDateTime.now());
        f.setNumeroLinhasProcessadas(numeroDeLinhasProcessadas);
        return filtroRepository.save(f);
    }

    @Transactional
    public FiltroVO registrarProcessando(Long idFiltro) {
        FiltroVO f = getFiltroSemComparadores(idFiltro);
        if (f == null)
            return null;
        f.setEstado(EstadoProcessamentoEnum.PROCESSANDO);
        return filtroRepository.save(f);
    }

    public FiltroVO getFiltroComComparadores(long id) {
        FiltroVO f = filtroRepository.buscaPorIdComComparador(id);
        return f;
    }

    public FiltroVO getFiltroSemComparadores(long id) {
        Optional<FiltroVO> ret = filtroRepository.findById(id);
        if (ret.isEmpty()) {
            throw new RegistroNaoLocalizadoException("Filtro " + id + " Não localizado");
        }
        return ret.get();
    }

    @Transactional
    public FiltroCriterioVO adicionaCriterio(FiltroCriterioVO filtroCriterioVO) {
        if (filtroCriterioVO.getFiltroVO() == null)
            return null;
        return filtroComparadorRepository.save(filtroCriterioVO);
    }

    public List<FiltroCriterioVO> getCriteriosDoFiltro(long id) {
        return filtroComparadorRepository.findAllByFiltroVO_Id(id);
    }

    @Transactional
    public void atualizaLinhasProcessadas(Long idFiltro, Long linhasProcessadas) {
        filtroRepository.atualizaLinhasProcessadas(idFiltro, linhasProcessadas);
    }

    @Transactional
    public void removeComparador(long idFiltro, long idComparador) {
        filtroComparadorRepository.removeByIdAndFiltroVO_Id(idComparador, idFiltro);
    }
}