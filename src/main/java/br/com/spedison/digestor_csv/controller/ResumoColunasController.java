package br.com.spedison.digestor_csv.controller;

import br.com.spedison.digestor_csv.dto.ResumoColunasCampoDTO;
import br.com.spedison.digestor_csv.dto.ResumoColunasDTO;
import br.com.spedison.digestor_csv.infra.ListadorColunasArquivo;
import br.com.spedison.digestor_csv.infra.ListadorDiretoriosEArquivos;
import br.com.spedison.digestor_csv.infra.ResumoColunasVoUtils;
import br.com.spedison.digestor_csv.model.FiltroVO;
import br.com.spedison.digestor_csv.model.ResumoColunasCampoVO;
import br.com.spedison.digestor_csv.model.ResumoColunasVO;
import br.com.spedison.digestor_csv.processadores.service.ProcessadorJobResumoColunasService;
import br.com.spedison.digestor_csv.service.ResumoColunasService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Controller
@Log4j2
@RequestMapping("/resumo")
public class ResumoColunasController {

    @Autowired
    ResumoColunasService resumoColunasService;

    @Autowired
    ListadorDiretoriosEArquivos listadorDiretoriosEArquivos;

    @Autowired
    ListadorColunasArquivo listadorColunasArquivo;

    @Autowired
    ProcessadorJobResumoColunasService job;

    @Autowired
    ResumoColunasVoUtils resumoColunasVoUtils;

    @GetMapping("")
    public String lista(Model model) {
        model.addAttribute("itens", resumoColunasService.listar());
        return "resumo_colunas_listar";
    }

    @GetMapping("/{id}")
    public String lista(@PathVariable Long id, Model model) {

        ResumoColunasVO resumoColunas = resumoColunasService.getResumoSemCampos(id);
        List<ResumoColunasCampoVO> listaCamposResumo = resumoColunasService.getCampos(id);
        ResumoColunasDTO resumoColunasDTO = new ResumoColunasDTO();
        BeanUtils.copyProperties(resumoColunas, resumoColunasDTO);
        resumoColunasVoUtils.setColunaResumida(resumoColunas, resumoColunasDTO);

        List<String> diretoriosEntradaDisponiveis = listadorDiretoriosEArquivos.lerDiretoriosParaEntradaFormatoString();
        List<String> camposDoArquivo = listadorColunasArquivo.getListaColunasResumoColunas(id);
        model.addAttribute("resumoColunas", resumoColunasDTO);
        model.addAttribute("diretoriosEntrada", diretoriosEntradaDisponiveis);

        ResumoColunasCampoDTO novoResumoCampoDTO = new ResumoColunasCampoDTO(resumoColunas.getId(), null, "");
        model.addAttribute("campo", novoResumoCampoDTO);
        model.addAttribute("campos", camposDoArquivo);
        model.addAttribute("listaCamposResumo", listaCamposResumo);
        return "resumo_colunas_adicionar";
    }

    @GetMapping("/criar")
    public String adicionar(Model model) {
        List<String> diretoriosEntrada = listadorDiretoriosEArquivos.lerDiretoriosParaEntradaFormatoString();
        ResumoColunasVO novo = resumoColunasService.criaNovo(diretoriosEntrada.get(0));
        return "redirect:/resumo/" + novo.getId();
    }

    @PostMapping({"/", ""})
    public String save(Long id,String nomeDaTarefa, String diretorioEntrada, String colunaSumarizada) {
        ResumoColunasVO resumoColunasVO = resumoColunasService.getResumoSemCampos(id);
        //resumoColunasVO.setId(id);
        resumoColunasVO.setDiretorioEntrada(diretorioEntrada);
        resumoColunasVO.setNomeDaTarefa(nomeDaTarefa);
        if (!Objects.isNull(colunaSumarizada)) {
            String[] dadosCampoSumarizadoSeparado = colunaSumarizada.split("[;]");
            resumoColunasVO.setNumeroColunaSumarizada(Integer.parseInt(dadosCampoSumarizadoSeparado[0]));
            resumoColunasVO.setNomeColunaSumarizada(dadosCampoSumarizadoSeparado[1]);
        } else {
            resumoColunasVO.setNumeroColunaSumarizada(null);
            resumoColunasVO.setNomeColunaSumarizada(null);
        }
        resumoColunasService.salvaDiretoriosECampoSumarizado(resumoColunasVO);
        return "redirect:/resumo/" + resumoColunasVO.getId();
    }

    @PostMapping("/campo")
    public String saveCampo(@ModelAttribute("resumoColunasCampo") ResumoColunasCampoDTO removeColunasCampoDTO) {
        ResumoColunasCampoVO vo = new ResumoColunasCampoVO();
        removeColunasCampoDTO.preencheVO(vo);
        resumoColunasService.adicionaCampos(vo);
        return "redirect:/resumo/" + removeColunasCampoDTO.getIdResumoColunas();
    }

    @GetMapping("/{id}/campo/{idCampo}/deletar")
    public String removeCampo(@PathVariable long id, @PathVariable long idCampo) {
        resumoColunasService.removeCampo(id, idCampo);
        return "redirect:/resumo/" + id;
    }

    @GetMapping("/{id}/deletar")
    public String remove(@PathVariable Long id) {
        resumoColunasService.remove(id);
        return "redirect:/resumo/";
    }

    @GetMapping("/{id}/executar")
    public String executa(@PathVariable Long id) {
        job.executa(id);
        return "redirect:/resumo/";
    }
    @GetMapping("/{id}/copiar")
    public String copiar(@PathVariable long id,
                         Model model) {

       ResumoColunasVO v = resumoColunasService.copiar(id);

        if (v != null) {
            return "redirect:/resumo?msg="
                    + URLEncoder.encode("Resumo de Colunas #" + id + " copiado para o Resumo de Colunas #" + v.getId() + " com sucesso.", StandardCharsets.UTF_8)
                    + "&tab=1";
        } else {
            model.addAttribute("mensagem_linha1", "Não exite o resumo de colunas " + id);
            model.addAttribute("mensagem_linha2", "Retorne para a listagem de resumo de colunas para refazer o processo ");
            model.addAttribute("link", "/resumo");
            model.addAttribute("nome_link", "Listagem de Resumo de Colunas");
            return "erro_mensagem";
        }

    }

}