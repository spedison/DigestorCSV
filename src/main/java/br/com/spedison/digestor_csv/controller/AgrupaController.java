package br.com.spedison.digestor_csv.controller;

import br.com.spedison.digestor_csv.dto.AgrupaCampoDTO;
import br.com.spedison.digestor_csv.infra.ListadorColunasArquivo;
import br.com.spedison.digestor_csv.infra.ListadorDiretoriosEArquivos;
import br.com.spedison.digestor_csv.model.AgrupaCampoVO;
import br.com.spedison.digestor_csv.model.AgrupaVO;
import br.com.spedison.digestor_csv.processadores.service.ProcessadorJobAgrupaService;
import br.com.spedison.digestor_csv.service.AgrupaService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Log4j2
@RequestMapping("/agrupa")
public class AgrupaController {

    @Autowired
    AgrupaService agrupaService;

    @Autowired
    ListadorDiretoriosEArquivos listadorDiretoriosEArquivos;

    @Autowired
    ListadorColunasArquivo listadorColunasArquivo;

    @Autowired
    ProcessadorJobAgrupaService job;

    @GetMapping("")
    public String listar(Model model) {
        model.addAttribute("itens", agrupaService.listar());
        return "agrupa_listar";
    }

    @GetMapping("/{id}")
    public String lista(@PathVariable Long id, Model model) {
        AgrupaVO agrupa = agrupaService.getAgrupaSemCampos(id);
        List<String> diretoriosEntrada = listadorDiretoriosEArquivos.lerDiretoriosParaEntradaString();
        List<String> campos = listadorColunasArquivo.getListaColunasAgrupa(id);
        model.addAttribute("agrupa", agrupa);
        model.addAttribute("diretoriosEntrada", diretoriosEntrada);
        AgrupaCampoDTO novoFiltroComparadorDTO = new AgrupaCampoDTO(agrupa.getId(), null, "");
        model.addAttribute("campo", novoFiltroComparadorDTO);
        model.addAttribute("campos", campos);
        model.addAttribute("diretoriosEntrada", diretoriosEntrada);
        return "agrupa_adicionar";
    }

    @GetMapping("/novo")
    public String adicionar(Model model) {
        List<String> diretoriosEntrada = listadorDiretoriosEArquivos.lerDiretoriosParaEntradaString();
        AgrupaVO novo = agrupaService.criaNovoAgrupa(diretoriosEntrada.get(0));
        return "redirect:/agrupa/" + novo.getId();
    }

    @PostMapping({"/",""})
    public String save(@ModelAttribute("agrupa") AgrupaVO agrupaVO) {
        agrupaService.salvaDiretorios(agrupaVO);
        return "redirect:/agrupa/" + agrupaVO.getId();
    }

    @PostMapping("/campo")
    public String saveCampo(@ModelAttribute("agrupaCampo") AgrupaCampoDTO agrupaCampoDTO) {
        AgrupaCampoVO vo = new AgrupaCampoVO();
        agrupaCampoDTO.preencheVO(vo);
        agrupaService.adicionaCampos(vo);
        return "redirect:/agrupa/" + agrupaCampoDTO.getIdAgrupa();
    }

    @GetMapping("/{idAgrupa}/campo/{idCampo}/deletar")
    public String removeCampo(@PathVariable long idAgrupa, @PathVariable long idCampo) {
        agrupaService.removeCampo(idAgrupa, idCampo);
        return "redirect:/agrupa/" + idAgrupa;
    }

    @GetMapping("/{idAgrupa}/deletar")
    public String removeAgrupa(@PathVariable Long idAgrupa) {
        agrupaService.removeAgrupa(idAgrupa);
        return "redirect:/agrupa";
    }

    @GetMapping("/{idAgrupa}/executar")
    public String executa(@PathVariable Long idAgrupa) {
        job.executa(idAgrupa);
        return "redirect:/agrupa/";
    }
}
