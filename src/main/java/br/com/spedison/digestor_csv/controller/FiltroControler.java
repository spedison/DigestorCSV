package br.com.spedison.digestor_csv.controller;

import br.com.spedison.digestor_csv.dto.FiltroCriterioDTO;
import br.com.spedison.digestor_csv.infra.ListadorColunasArquivo;
import br.com.spedison.digestor_csv.infra.ListadorDiretoriosEArquivos;
import br.com.spedison.digestor_csv.model.FiltroCriterioVO;
import br.com.spedison.digestor_csv.model.FiltroVO;
import br.com.spedison.digestor_csv.model.TipoCriterioEnum;
import br.com.spedison.digestor_csv.processadores.service.ProcessadorJobFiltroService;
import br.com.spedison.digestor_csv.service.FiltroService;
import br.com.spedison.digestor_csv.service.RegistroNaoLocalizadoException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
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
        File primeiroDirEntrada = listadorDiretoriosEArquivos.lerPrimeiroDiretorioParaEntrada();

        if (primeiroDirEntrada == null) {
            model.addAttribute("mensagem_linha1", "Não tem dados do diretório de entrada e/ou saída");
            model.addAttribute("mensagem_linha2", "Configure adequadamente o aplicativo colocando o diretório de entrada e saida. " +
                    "Favor retorne para a página de configuração");
            model.addAttribute("link", "/");
            model.addAttribute("nome_link", "Configuração");
            return "erro_mensagem";
        }

        String diretoriosEntrada = primeiroDirEntrada.toString();
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
    public String show(
            @PathVariable(value = "id") long id,
            @RequestParam(required = false) String msg,
            @RequestParam(required = false) Integer tab,
            Model model) {

        String mensagem = msg == null ? "" : msg;
        Integer mostraTab = tab == null ? 1 : tab;

        try {
            FiltroVO filtroVO = filtroService.getFiltroSemCriterios(id);

            List<String> colunasArquivo = listadorColunasArquivos.getListaColunasFiltro(filtroVO.getId());
            model.addAttribute("filtro", filtroVO);
            List<FiltroCriterioVO> criterios = filtroService.getCriteriosDoFiltro(id);

            if (criterios == null) {
                criterios = new LinkedList<>();
            }

            model.addAttribute("criterio", new FiltroCriterioDTO(filtroVO.getId(),
                    null, TipoCriterioEnum.VAZIO,
                    "", BigDecimal.ZERO, BigDecimal.ZERO,
                    "-1;LINHA_TODA"));

            model.addAttribute("criterios", criterios);
            model.addAttribute("mostraTab", mostraTab);

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

    @GetMapping("/{idFiltro}/criterio/{idCriterio}/deletar")
    public String show(@PathVariable long idFiltro,
                       @PathVariable long idCriterio,
                       Model model) {
        filtroService.removeCriterio(idFiltro, idCriterio);
        return "redirect:/filtro/" + idFiltro + "?msg="
                + URLEncoder.encode("Critério #" + idCriterio + " removido com sucesso", StandardCharsets.UTF_8)
                + "&tab=3";
    }

    @GetMapping("/{idFiltro}/copiar")
    public String show(@PathVariable long idFiltro,
                       Model model) {

        FiltroVO v = filtroService.copiar(idFiltro);

        if (v != null) {
            return "redirect:/filtro?msg="
                    + URLEncoder.encode("Filtro #" + idFiltro + " copiado para o Filtro #" + v.getId() + " com sucesso.", StandardCharsets.UTF_8)
                    + "&tab=1";
        } else {
            model.addAttribute("mensagem_linha1", "Não exite o filtro " + idFiltro);
            model.addAttribute("mensagem_linha2", "Retorne para a listagem de filtros para refazer o processo " );
            model.addAttribute("link", "/");
            model.addAttribute("nome_link", "Listagem de Filtros");
            return "erro_mensagem";
        }

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
                + URLEncoder.encode("O filtro #" + id + " foi removido.", StandardCharsets.UTF_8)
                + "&tab=3";
    }

    @PostMapping(value = "/criterio")
    public String adicionaCriterio(FiltroCriterioDTO filtroCriterioDTO) {
        FiltroVO filtroVO = new FiltroVO();
        filtroVO.setId(filtroCriterioDTO.getIdFiltro());
        FiltroCriterioVO filtroCriterioVO = new FiltroCriterioVO();
        filtroCriterioDTO.preencheVO(filtroCriterioVO);
        filtroCriterioVO.setFiltroVO(filtroVO);
        FiltroCriterioVO fcvo = filtroService.adicionaCriterio(filtroCriterioVO);
        return "redirect:/filtro/" + filtroVO.getId() + "?msg="
                + URLEncoder.encode(
                "Critério #" + fcvo.getId() + " foi adicionado com sucesso.", StandardCharsets.UTF_8)
                + "&tab=2";
    }
}
