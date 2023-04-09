package br.com.spedison.digestor_csv.controller;

import br.com.spedison.digestor_csv.dto.AgrupaCampoDTO;
import br.com.spedison.digestor_csv.infra.ListadorColunasArquivo;
import br.com.spedison.digestor_csv.infra.ListadorDiretoriosEArquivos;
import br.com.spedison.digestor_csv.model.AgrupaCampoVO;
import br.com.spedison.digestor_csv.model.AgrupaVO;
import br.com.spedison.digestor_csv.processadores.service.ProcessadorJobAgrupaService;
import br.com.spedison.digestor_csv.service.AgrupaService;
import br.com.spedison.digestor_csv.service.RegistroNaoLocalizadoException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    public String listar(Model model, @RequestParam(required = false) String msg) {
        model.addAttribute("itens", agrupaService.listar());
        model.addAttribute("mensagem", msg == null ? "" : msg);
        return "agrupa_listar";
    }

    @GetMapping("/criar")
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

        AgrupaVO novo = agrupaService.criaNovoAgrupa(primeiroDirEntrada.toString());
        return "redirect:/agrupa/" + novo.getId() + "?msg="
                + URLEncoder.encode("Novo agrupamento criado com o id " + novo.getId(), StandardCharsets.UTF_8);
    }

    @PostMapping({"/", ""})
    public String gravar(@ModelAttribute("agrupa") AgrupaVO agrupaVO) {
        agrupaService.gravaDiretoriosENomeDaTarefa(agrupaVO);
        return "redirect:/agrupa/" + agrupaVO.getId() + "?msg="
                + URLEncoder.encode("Gravação realizada com sucesso", StandardCharsets.UTF_8);
    }

    @PostMapping("/campo")
    public String saveCampo(@ModelAttribute("agrupaCampo") AgrupaCampoDTO agrupaCampoDTO) {
        AgrupaCampoVO vo = new AgrupaCampoVO();
        agrupaCampoDTO.preencheVO(vo);
        AgrupaCampoVO ac = agrupaService.adicionaCampos(vo);
        return "redirect:/agrupa/" + agrupaCampoDTO.getIdAgrupa() + "?msg="
                + URLEncoder.encode(
                "Campo #" + ac.getId() + " foi adicionado com sucesso.", StandardCharsets.UTF_8)
                + "&tab=2";
    }

    @GetMapping("/{id}")
    public String exibir(@PathVariable Long id,
                         @RequestParam(required = false) String msg,
                         @RequestParam(required = false) Integer tab,
                         Model model) {

        String mensagem = msg == null ? "" : msg;
        Integer mostraTab = tab == null ? 1 : tab;
        try {
            AgrupaVO agrupa = agrupaService.getAgrupaSemCampos(id);
            List<String> diretoriosEntrada = listadorDiretoriosEArquivos.lerDiretoriosParaEntradaFormatoString();
            List<String> campos = listadorColunasArquivo.getListaColunasAgrupa(id);
            model.addAttribute("agrupa", agrupa);
            model.addAttribute("diretoriosEntrada", diretoriosEntrada);
            AgrupaCampoDTO novoAgrupamentoCampoDTO = new AgrupaCampoDTO(agrupa.getId(), null, "");
            model.addAttribute("campo", novoAgrupamentoCampoDTO);
            model.addAttribute("campos", campos);
            model.addAttribute("mostraTab", mostraTab);
            model.addAttribute("mensagem", mensagem);
            return "agrupa_adicionar";
        } catch (RegistroNaoLocalizadoException rnl) {
            model.addAttribute("mensagem_linha1", rnl.getMessage());
            model.addAttribute("mensagem_linha2", "O agrupamento que você deseja não existe. " +
                    "Favor retorne para a página de listagem de agrupamentos");
            model.addAttribute("link", "/agrupa");
            model.addAttribute("nome_link", "Listagem de agrupamentos");
            return "erro_mensagem";
        }
    }


    @GetMapping("/{idAgrupa}/deletar")
    public String remover(@PathVariable Long idAgrupa) {
        agrupaService.removeAgrupa(idAgrupa);
        return "redirect:/agrupa/" + idAgrupa + "?msg="
                + URLEncoder.encode("Critério #" + idAgrupa + " removido com sucesso", StandardCharsets.UTF_8)
                + "&tab=3";
    }

    @GetMapping("/{idAgrupa}/campo/{idCampo}/deletar")
    public String removerCampo(@PathVariable long idAgrupa, @PathVariable long idCampo) {
        agrupaService.removeCampo(idAgrupa, idCampo);
        return "redirect:/agrupa/" + idAgrupa + "?msg="
                + URLEncoder.encode("Campo #" + idCampo + " removido com sucesso", StandardCharsets.UTF_8)
                + "&tab=3";
    }


    @GetMapping("/{idAgrupa}/copiar")
    public String copiar(@PathVariable long idAgrupa,
                         Model model) {

        AgrupaVO v = agrupaService.copiar(idAgrupa);

        if (v != null) {
            return "redirect:/agrupa?msg="
                    + URLEncoder.encode("Agrupamento #" + idAgrupa + " copiado para o Agrupamento #" + v.getId() + " com sucesso.", StandardCharsets.UTF_8)
                    + "&tab=1";
        } else {
            model.addAttribute("mensagem_linha1", "Não exite o agrupamento " + idAgrupa);
            model.addAttribute("mensagem_linha2", "Retorne para a listagem de agrupamentos para refazer o processo ");
            model.addAttribute("link", "/agrupa");
            model.addAttribute("nome_link", "Listagem de Agrupamentos");
            return "erro_mensagem";
        }
    }


    @GetMapping("/{idAgrupa}/executar")
    public String executa(@PathVariable Long idAgrupa) {
        job.executa(idAgrupa);
        return "redirect:/agrupa/?msg="
                + URLEncoder.encode("Agrupamento #" + idAgrupa + " será executado em instantes.", StandardCharsets.UTF_8);
    }
}
