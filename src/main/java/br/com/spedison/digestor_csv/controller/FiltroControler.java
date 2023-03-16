package br.com.spedison.digestor_csv.controller;

import br.com.spedison.digestor_csv.dto.FiltroComparadorDTO;
import br.com.spedison.digestor_csv.infra.ListadorColunasArquivo;
import br.com.spedison.digestor_csv.infra.ListadorDiretoriosEArquivos;
import br.com.spedison.digestor_csv.model.FiltroCriterioVO;
import br.com.spedison.digestor_csv.model.FiltroVO;
import br.com.spedison.digestor_csv.model.TipoComparacaoEnum;
import br.com.spedison.digestor_csv.processadores.service.ProcessadorJobFiltroService;
import br.com.spedison.digestor_csv.service.FiltroService;
import br.com.spedison.digestor_csv.service.RegistroNaoLocalizadoException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

@Controller
@Log4j2
@RequestMapping("/filtro")
public class FiltroControler {

    @Autowired
    FiltroService filtroService;

    @Autowired
    ProcessadorJobFiltroService pfs;

    @Autowired
    private ListadorDiretoriosEArquivos listadorDiretoriosEArquivos;

    @Autowired
    ListadorColunasArquivo listadorColunasArquivos;

    /***
     * Lista todos os filtros GET "/filtros
     * @param model
     * @return
     */
    @GetMapping("")
    public String list(Model model, @RequestParam(required = false) String msg) {
        List<FiltroVO> filtroVOS = filtroService.listar();
        model.addAttribute("filtros", filtroVOS);
        model.addAttribute("mensagem", msg == null ? "" : msg);
        return "filtro_listar";
    }

    @GetMapping("/novo")
    public String adicionar(Model model) {
        String diretoriosEntrada = listadorDiretoriosEArquivos.lerPrimeiroDiretorioParaEntrada().toString();
        FiltroVO novo = filtroService.criaNovoFiltro(diretoriosEntrada);
        return "redirect:/filtro/" + novo.getId() + "?msg="
                + URLEncoder.encode("Novo filtro criado com o id " + novo.getId(), StandardCharsets.UTF_8);
    }

    /***
     * Salva o filtro Corrente e volta para a mesma página.
     * Salva somente os diretórios.
     * @param filtroVO
     * @return
     */
    @PostMapping("")
    public String gravaFiltro(FiltroVO filtroVO) {
        FiltroVO f = filtroService.gravaDadosDoFiltro(filtroVO);
        return "redirect:/filtro/" + f.getId() + "?msg="
                + URLEncoder.encode("Gravação realizada com sucesso", StandardCharsets.UTF_8);
    }

    @GetMapping("/{id}")
    public String show(@PathVariable(value = "id") long id, @RequestParam(required = false) String msg, Model model) {

        String mensagem = msg == null ? "" : msg;

        try {
            FiltroVO filtroVO = filtroService.getFiltroSemComparadores(id);

            List<String> colunasArquivo = listadorColunasArquivos.getListaColunasFiltro(filtroVO.getId());
            model.addAttribute("filtro", filtroVO);
            List<FiltroCriterioVO> comparadores = filtroService.getCriteriosDoFiltro(id);

            if (comparadores == null) {
                comparadores = new LinkedList<>();
            }

            model.addAttribute("comparador", new FiltroComparadorDTO(filtroVO.getId(),
                    null, TipoComparacaoEnum.VAZIO,
                    "", BigDecimal.ZERO, BigDecimal.ZERO,
                    "-1;LINHA_TODA"));

            model.addAttribute("comparadores", comparadores);

            List<String> direatoriosEntrada = listadorDiretoriosEArquivos.lerDiretoriosParaEntradaString();
            model.addAttribute("diretoriosEntrada", direatoriosEntrada);
            model.addAttribute("colunasArquivo", colunasArquivo);
            model.addAttribute("mensagem", mensagem);
            return "filtro_adicionar";

        } catch (RegistroNaoLocalizadoException rnl) {
            model.addAttribute("mensagem_linha1", rnl.getMessage());
            model.addAttribute("mensagem_linha2", "O filtro que você deseja não existe. " +
                    "Favor retorne para a página de listagem de filtros");
            model.addAttribute("link", "/filtro");
            model.addAttribute("nome_link", "Listagem de filtros");
            return "erro_mensagem";
        }
    }

    @GetMapping("/{idFiltro}/comparador/{idCriterio}/deletar")
    public String show(@PathVariable long idFiltro,
                       @PathVariable long idCriterio,
                       Model model) {
        filtroService.removeComparador(idFiltro, idCriterio);
        return "redirect:/filtro/" + idFiltro + "?msg="
                + URLEncoder.encode("Critério #" + idCriterio + " removido com sucesso", StandardCharsets.UTF_8);
    }

    @GetMapping("/{id}/executar")
    public String execute(@PathVariable(value = "id") long id, Model model) {
        pfs.executa(id);
        return "redirect:/filtro?msg="
                + URLEncoder.encode("Filtro #" + id + " será executado em instantes.", StandardCharsets.UTF_8);
    }

    @GetMapping("/{id}/deletar")
    public String deletarFiltro(@PathVariable(value = "id") long id, Model model) {
        filtroService.removeFiltro(id);
        return "redirect:/filtro?msg="
                + URLEncoder.encode("O filtro #" + id + " foi removido.", StandardCharsets.UTF_8);

    }

    @PostMapping(value = "/comparador")
    public String adicionaCriterio(FiltroComparadorDTO filtroComparadorDTO) {
        FiltroVO filtroVO = new FiltroVO();
        filtroVO.setId(filtroComparadorDTO.getIdFiltro());
        FiltroCriterioVO filtroCriterioVO = new FiltroCriterioVO();
        filtroComparadorDTO.preencheVO(filtroCriterioVO);
        filtroCriterioVO.setFiltroVO(filtroVO);
        FiltroCriterioVO fcvo = filtroService.adicionaCriterio(filtroCriterioVO);
        return "redirect:/filtro/" + filtroVO.getId() + "?msg="
                + URLEncoder.encode(
                "Critério #" + fcvo.getId() + " foi adicionado com sucesso.", StandardCharsets.UTF_8);
    }

}
