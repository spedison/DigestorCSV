package br.com.spedison.digestor_csv.controller;

import br.com.spedison.digestor_csv.dto.RemoveColunasCampoDTO;
import br.com.spedison.digestor_csv.infra.ListadorColunasArquivo;
import br.com.spedison.digestor_csv.infra.ListadorDiretoriosEArquivos;
import br.com.spedison.digestor_csv.model.RemoveColunasCampoVO;
import br.com.spedison.digestor_csv.model.RemoveColunasVO;
import br.com.spedison.digestor_csv.processadores.service.ProcessadorJobRemoveColunasService;
import br.com.spedison.digestor_csv.service.RemoveColunasService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Log4j2
@RequestMapping("/remove_colunas")
public class RemoveColunasController {

    @Autowired
    RemoveColunasService removeColunasService;

    @Autowired
    ListadorDiretoriosEArquivos listadorDiretoriosEArquivos;

    @Autowired
    ListadorColunasArquivo listadorColunasArquivo;

    @Autowired
    ProcessadorJobRemoveColunasService job;

    @GetMapping("")
    public String lista(Model model) {
        model.addAttribute("itens", removeColunasService.listar());
        return "remove_colunas_listar";
    }

    @GetMapping("/{id}")
    public String lista(@PathVariable Long id, Model model) {
        RemoveColunasVO removeColunas = removeColunasService.getAgrupaSemCampos(id);
        List<String> diretoriosEntrada = listadorDiretoriosEArquivos.lerDiretoriosParaEntradaFormatoString();
        List<String> campos = listadorColunasArquivo.getListaColunasRemoveColunas(id);
        model.addAttribute("removeColunas", removeColunas);
        model.addAttribute("diretoriosEntrada", diretoriosEntrada);
        RemoveColunasCampoDTO novoFiltroComparadorDTO = new RemoveColunasCampoDTO(removeColunas.getId(), null, "");
        model.addAttribute("campo", novoFiltroComparadorDTO);
        model.addAttribute("campos", campos);
        model.addAttribute("diretoriosEntrada", diretoriosEntrada);
        return "remove_colunas_adicionar";
    }

    @GetMapping("/criar")
    public String adicionar(Model model) {
        List<String> diretoriosEntrada = listadorDiretoriosEArquivos.lerDiretoriosParaEntradaFormatoString();
        RemoveColunasVO novo = removeColunasService.criaNovo(diretoriosEntrada.get(0));
        return "redirect:/remove_colunas/" + novo.getId();
    }

    @PostMapping({"/",""})
    public String save(@ModelAttribute("removeColunas") RemoveColunasVO removeColunasVO) {
        removeColunasService.salvaDiretorios(removeColunasVO);
        return "redirect:/remove_colunas/" + removeColunasVO.getId();
    }

    @PostMapping("/campo")
    public String saveCampo(@ModelAttribute("removeColunasCampo") RemoveColunasCampoDTO removeColunasCampoDTO) {
        RemoveColunasCampoVO vo = new RemoveColunasCampoVO();
        removeColunasCampoDTO.preencheVO(vo);
        removeColunasService.adicionaCampos(vo);
        return "redirect:/remove_colunas/" + removeColunasCampoDTO.getIdRemoveColunas();
    }

    @GetMapping("/{id}/campo/{idCampo}/deletar")
    public String removeCampo(@PathVariable long id, @PathVariable long idCampo) {
        removeColunasService.removeCampo(id, idCampo);
        return "redirect:/remove_colunas/" + id;
    }

    @GetMapping("/{id}/deletar")
    public String remove(@PathVariable Long id) {
        removeColunasService.remove(id);
        return "redirect:/remove_colunas/";
    }

    @GetMapping("/{idAgrupa}/executar")
    public String executa(@PathVariable Long idAgrupa) {
        job.executa(idAgrupa);
        return "redirect:/remove_colunas/";
    }
}
