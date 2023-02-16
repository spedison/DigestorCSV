package br.com.spedison.digestor_csv.controller;

import br.com.spedison.digestor_csv.dto.RemoveColunasCampoDTO;
import br.com.spedison.digestor_csv.dto.ResumoColunasCampoDTO;
import br.com.spedison.digestor_csv.infra.ListadorColunasArquivo;
import br.com.spedison.digestor_csv.infra.ListadorDiretoriosEArquivos;
import br.com.spedison.digestor_csv.model.RemoveColunasCampoVO;
import br.com.spedison.digestor_csv.model.ResumoColunasCampoVO;
import br.com.spedison.digestor_csv.model.ResumoColunasVO;
import br.com.spedison.digestor_csv.processadores.service.ProcessadorJobResumoColunasService;
import br.com.spedison.digestor_csv.service.ResumoColunasService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Log4j2
@RequestMapping("/resumo_colunas")
public class ResumoColunasController {

    @Autowired
    ResumoColunasService resumoColunasService;

    @Autowired
    ListadorDiretoriosEArquivos listadorDiretoriosEArquivos;

    @Autowired
    ListadorColunasArquivo listadorColunasArquivo;

    @Autowired
    ProcessadorJobResumoColunasService job;

    @GetMapping("")
    public String lista(Model model) {
        model.addAttribute("itens", resumoColunasService.listar());
        return "resumo_colunas_listar";
    }

    @GetMapping("/{id}")
    public String lista(@PathVariable Long id, Model model) {
        ResumoColunasVO resumoColunas = resumoColunasService.getResumoSemCampos(id);
        List<String> diretoriosEntrada = listadorDiretoriosEArquivos.lerDiretoriosParaEntradaString();
        List<String> campos = listadorColunasArquivo.getListaColunasRemoveColunas(id);
        model.addAttribute("resumoColunas", resumoColunas);
        model.addAttribute("diretoriosEntrada", diretoriosEntrada);
        ResumoColunasCampoDTO novoFiltroComparadorDTO = new ResumoColunasCampoDTO(resumoColunas.getId(), null, "");
        model.addAttribute("campo", novoFiltroComparadorDTO);
        model.addAttribute("campos", campos);
        model.addAttribute("diretoriosEntrada", diretoriosEntrada);
        return "resumo_colunas_adicionar";
    }

    @GetMapping("/novo")
    public String adicionar(Model model) {
        List<String> diretoriosEntrada = listadorDiretoriosEArquivos.lerDiretoriosParaEntradaString();
        ResumoColunasVO novo = resumoColunasService.criaNovo(diretoriosEntrada.get(0));
        return "redirect:/resumo_colunas/" + novo.getId();
    }

    @PostMapping({"/", ""})
    public String save(Long id, String diretorioEntrada, String dadosCampoSumarizado) {
        ResumoColunasVO resumoColunasVO = new ResumoColunasVO();
        resumoColunasVO.setId(id);
        resumoColunasVO.setDiretorioEntrada(diretorioEntrada);
        String[] dadosCampoSumarizadoSeparado = dadosCampoSumarizado.split("[;]");
        resumoColunasVO.setNumercoColunaSumarizada(Integer.parseInt(dadosCampoSumarizadoSeparado[0]));
        resumoColunasVO.setNomeColunaSumarizada(dadosCampoSumarizadoSeparado[1]);
        resumoColunasService.salvaDiretoriosECampoResumo(resumoColunasVO);
        return "redirect:/resumo_colunas/" + resumoColunasVO.getId();
    }

    @PostMapping("/campo")
    public String saveCampo(@ModelAttribute("resumoColunasCampo") ResumoColunasCampoDTO removeColunasCampoDTO) {
        ResumoColunasCampoVO vo = new ResumoColunasCampoVO();
        removeColunasCampoDTO.preencheVO(vo);
        resumoColunasService.adicionaCampos(vo);
        return "redirect:/resumo_colunas/" + removeColunasCampoDTO.getIdResumoColunas();
    }

    @GetMapping("/{id}/campo/{idCampo}/deletar")
    public String removeCampo(@PathVariable long id, @PathVariable long idCampo) {
        resumoColunasService.removeCampo(id, idCampo);
        return "redirect:/resumo_colunas/" + id;
    }

    @GetMapping("/{id}/deletar")
    public String remove(@PathVariable Long id) {
        resumoColunasService.remove(id);
        return "redirect:/resumo_colunas/";
    }

    @GetMapping("/{idAgrupa}/executar")
    public String executa(@PathVariable Long idAgrupa) {
        job.executa(idAgrupa);
        return "redirect:/resumo_colunas/";
    }
}