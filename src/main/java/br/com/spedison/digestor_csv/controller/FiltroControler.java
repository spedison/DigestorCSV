package br.com.spedison.digestor_csv.controller;

import br.com.spedison.digestor_csv.dto.FiltroComparadorDTO;
import br.com.spedison.digestor_csv.infra.ListadorColunasArquivo;
import br.com.spedison.digestor_csv.infra.ListadorDiretoriosEArquivos;
import br.com.spedison.digestor_csv.model.FiltroComparadorVO;
import br.com.spedison.digestor_csv.model.FiltroVO;
import br.com.spedison.digestor_csv.model.TipoComparacaoEnum;
import br.com.spedison.digestor_csv.processadores.service.ProcessadorJobFiltroService;
import br.com.spedison.digestor_csv.service.FiltroService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
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
    public String list(Model model) {
        List<FiltroVO> filtroVOS = filtroService.listar();
        model.addAttribute("filtros", filtroVOS);
        return "filtro_listar";
    }

    @GetMapping("/novo")
    public String adicionar(Model model) {
        List<String> diretoriosEntrada = listadorDiretoriosEArquivos.lerDiretoriosParaEntradaString();
        FiltroVO novo = filtroService.criaNovoFiltro(diretoriosEntrada.get(0));
        FiltroComparadorDTO novoFiltroComparadorDTO = new FiltroComparadorDTO(null, novo.getId(), TipoComparacaoEnum.TXT_INICIA,
                "test", null, null, "-1;LINHA TODA");
        model.addAttribute("filtro", novo);
        model.addAttribute("comparador", novoFiltroComparadorDTO);
        model.addAttribute("comparadores", novo.getComparadores());
        model.addAttribute("diretoriosEntrada", diretoriosEntrada);
        return "filtro_adicionar";
    }

    /***
     * Salva o filtro Corrente e volta para a mesma página.
     * Salva somente os diretórios.
     * @param filtroVO
     * @return
     */
    @PostMapping("")
    public String save(FiltroVO filtroVO) {
        FiltroVO f = filtroService.salvaDiretorios(filtroVO);
        return "redirect:/filtro/" + f.getId();
    }

    @GetMapping("/{id}")
    public String show(@PathVariable(value = "id") long id, Model model) {
        FiltroVO filtroVO = filtroService.getFiltroSemComparadores(id);
        List<String> colunasArquivo = listadorColunasArquivos.getListaColunasFiltro(filtroVO.getId());
        model.addAttribute("filtro", filtroVO);
        List<FiltroComparadorVO> comparadores = filtroService.getComparadoresDoFiltro(id);

        if (comparadores == null) {
            comparadores = new LinkedList<>();
        }

        model.addAttribute("comparador", new FiltroComparadorDTO(filtroVO.getId(),
                null, TipoComparacaoEnum.TXT_IGUAL,
                "", BigDecimal.ZERO, BigDecimal.ZERO,
                 "-1;LINHA_TODA"));

        model.addAttribute("comparadores", comparadores);

        List<String> direatoriosEntrada = listadorDiretoriosEArquivos.lerDiretoriosParaEntradaString();
        model.addAttribute("diretoriosEntrada", direatoriosEntrada);
        model.addAttribute("colunasArquivo", colunasArquivo);

        return "filtro_adicionar";
    }

    @GetMapping("/{idFiltro}/comparador/{idComparador}/deletar")
    public String show(@PathVariable long idFiltro,
                       @PathVariable long idComparador,
                       Model model) {
        filtroService.removeComparador(idFiltro, idComparador);
        return "redirect:/filtro/" + idFiltro;
    }

    @GetMapping("/{id}/executar")
    public String execute(@PathVariable(value = "id") long id, Model model) {
        pfs.executa(id);
        return "redirect:/filtro";
    }

    @GetMapping("/{id}/deletar")
    public String deletarFiltro(@PathVariable(value = "id") long id, Model model) {
        filtroService.removeFiltro(id);
        return "redirect:/filtro";
    }

    @PostMapping(value = "/comparador")
    public String adicionaComparador(FiltroComparadorDTO filtroComparadorDTO) {
        FiltroVO filtroVO = new FiltroVO();
        filtroVO.setId(filtroComparadorDTO.getIdFiltro());
        FiltroComparadorVO filtroComparadorVO = new FiltroComparadorVO();
        filtroComparadorDTO.preencheVO(filtroComparadorVO);
        filtroComparadorVO.setFiltroVO(filtroVO);
        filtroService.adicionaComparador(filtroComparadorVO);
        return "redirect:/filtro/" + filtroVO.getId();
    }

}
