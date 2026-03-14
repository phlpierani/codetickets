package br.com.alura.demo;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImportacaoController {

    @Autowired
    private JobOperator jobOperator;

    @Autowired
    private Job job;

    @GetMapping("/importar")
    public void importaArquivo() throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        jobOperator.start(job, jobParameters);
    }
}
