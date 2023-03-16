package br.com.spedison.digestor_csv.controller;

import br.com.spedison.digestor_csv.model.ConfiguracaoVO;
import br.com.spedison.digestor_csv.service.ConfiguracaoService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
@Log4j2
@RequestMapping("/configuracao")
public class ConfiguracaoControler {

    @Autowired
    ConfiguracaoService configuracaoService;

    @GetMapping("")
    public String listaConfiguracoes(Model model, @RequestParam(required = false) String msg) {
        configuracaoService.iniciarConfiguracao();
        model.addAttribute("configuracoes", configuracaoService.getListaParaTela());
        model.addAttribute("mensagem", msg == null ? "" : msg);
        return "configuracao";
    }

    @PostMapping("")
    public String save(HttpServletRequest request,
                       UriComponentsBuilder uriComponentsBuilderdados) {

        List<ConfiguracaoVO> dadosParaGravar = new LinkedList<>();
        var dado = request.getParameterNames();
        while (dado.hasMoreElements()) {
            String chave = dado.nextElement();
            log.debug("Chave = %s  | Valor = %s".formatted(chave, request.getParameter(chave)));
            dadosParaGravar.add(new ConfiguracaoVO(chave, "", null,
                    request.getParameter(chave), true, null));
        }

        configuracaoService.salvaListaDaTela(dadosParaGravar);

        return "redirect:/configuracao?msg=" + URLEncoder.encode("Gravação realizada com sucesso !", StandardCharsets.UTF_8);
    }


/*    @GetMapping("/showFormForUpdate/{id}")
    public String updateForm(@PathVariable(value = "id") long id, Model model) {
        Cozinha cozinha = cozinhaRepository.getReferenceById(id);
        log.debug("Nome = " + cozinha.getNome() + "Habilitado = " + cozinha.isHabilitado());
        model.addAttribute("cozinha", cozinha);
        return "updatecozinha";
    }

    @GetMapping("/deleteEmployee/{id}")
    public String deleteThroughId(@PathVariable(value = "id") long id) {
        cozinhaRepository.deleteById(id);
        return "redirect:/";

    } */
}
