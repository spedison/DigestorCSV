package br.com.spedison.digestor_csv.processadores.service;

import br.com.spedison.digestor_csv.processadores.ProcessadorResumoColunas;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class ProcessadorJobResumoColunasService {

    @Inject
    @Autowired
    private JobScheduler jobScheduler;

    @Autowired
    @Inject
    private ProcessadorResumoColunas processadorResumoColunas;

    @Job
    public void executa(Long id, JobContext jobContext) {
        processadorResumoColunas.executar(id, jobContext);
    }

    public void executa(Long id) {
        jobScheduler.enqueue(() -> this.executa(id, JobContext.Null));
    }

}
