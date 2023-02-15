package br.com.spedison.digestor_csv.infra;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ExecutadorComControleTempo {
    private long tempoAnterior = System.currentTimeMillis();

    @Value("${processamento.tempo_atualizacao_status:10000}")
    private long timeOut;

    public void executaSeTimeout(Runnable funcao) {
        if (System.currentTimeMillis() - tempoAnterior > timeOut) {
            funcao.run();
            tempoAnterior = System.currentTimeMillis();
        }
    }
}
