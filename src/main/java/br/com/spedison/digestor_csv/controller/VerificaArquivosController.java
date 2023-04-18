package br.com.spedison.digestor_csv.controller;

import br.com.spedison.digestor_csv.infra.DadosArquivo;
import br.com.spedison.digestor_csv.infra.ListadorDiretoriosEArquivos;
import br.com.spedison.digestor_csv.service.VerificaArquivosService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.util.LinkedList;

@Controller
@Log4j2
@RequestMapping("/verificaarquivos")
public class VerificaArquivosController {

    @Autowired
    ListadorDiretoriosEArquivos listadorDiretoriosEArquivos;

    @Autowired
    VerificaArquivosService verificaArquivosService;

    @GetMapping("")
    public String telaInicial(Model model) {
        model.addAttribute("diretorios", listadorDiretoriosEArquivos.lerDiretoriosParaEntradaFormatoString());
        var teste = new LinkedList<DadosArquivo>();
        model.addAttribute("listaArquivos", teste);
        return "verifica_arquivos";
    }

    @PostMapping("")
    public String mostrArquivoProcessados(Model model, String diretorio, Integer quantidadeArquivos) {
        model.addAttribute("diretorios", listadorDiretoriosEArquivos.lerDiretoriosParaEntradaFormatoString());
        var teste = verificaArquivosService.getDadosArquivos(diretorio, quantidadeArquivos);
        model.addAttribute("listaArquivos", teste);
        return "verifica_arquivos";
    }
}
