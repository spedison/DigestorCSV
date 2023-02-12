package br.com.spedison.digestor_csv.processadores.service;

import br.com.spedison.digestor_csv.processadores.ProcessadorAgrupa;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class ProcessadorJobAgrupaService {

    @Inject
    @Autowired
    private JobScheduler jobScheduler;

    @Autowired
    @Inject
    private ProcessadorAgrupa processadorAgrupa;

    @Job
    public void executaAgrupa(Long agrupaId, JobContext jobContext) {
        processadorAgrupa.executar(agrupaId, jobContext);
    }

    public void executa(Long id) {
        jobScheduler.enqueue(() -> this.executaAgrupa(id, JobContext.Null));
    }

}
