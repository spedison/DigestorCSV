package br.com.spedison.digestor_csv;

import br.com.spedison.digestor_csv.repository.ConfiguracaoRepository;
import br.com.spedison.digestor_csv.service.ConfiguracaoService;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

import java.util.Properties;


public class FiltroTestSpringbootApplication {

    public static void main(String[] args) {

        ApplicationContext ctx =
                new SpringApplicationBuilder(ThymeleafSpringbootApplication.class)
                        .web(WebApplicationType.NONE)
                        .run(args);


        var repo = ctx.getBean(ConfiguracaoRepository.class);
        Properties configuracao = ctx.getBean(ConfiguracaoService.class).getConfiguracao();

        //List<ComparadorVO> comparadores = new LinkedList<>();
        //comparadores.add(new ComparadorVO(1L,null, TipoComparacaoEnum.TXT_INICIA, "13", null, null, 19, "NomeColuna")); // Candidatos que iniciam com "13"
        //ProcessadorFiltro pf = new ProcessadorFiltro("teste inicial", configuracao, "/mnt/dados/entrada/sp_rj_mg", "/mnt/dados/saida",comparadores,true);
        //pf.chamaExecucao();
        //while (pf.getEstadoProcessamento() != EstadoProcessamento.TERMINADO){
        //    try {
        //        Thread.sleep(5000);
        //    }catch (InterruptedException ie){}
        //    pf.logEstado();
        //}
    }

}
