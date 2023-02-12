package br.com.spedison.digestor_csv.processadores.service;

import br.com.spedison.digestor_csv.processadores.ProcessadorFiltro;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class ProcessadorJobFiltroService {

    @Inject
    @Autowired
    private JobScheduler jobScheduler;

    @Autowired
    @Inject
    private ProcessadorFiltro processadorFiltro;

    @Job
    public void executaFiltro(Long filtroId, JobContext jobContext) {
        processadorFiltro.executar(filtroId, jobContext);
    }

    public void executa(Long id) {
        jobScheduler.enqueue(() -> this.executaFiltro(id, JobContext.Null));
    }

}
