package io.spring.batch.springbatch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.job.DefaultJobParametersExtractor;
import org.springframework.batch.core.step.job.JobParametersExtractor;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class JobStepConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final JobLauncher jobLauncher;

    @Bean
    public Job job(){

        return jobBuilderFactory.get("parentJob")
            .start(step1())
            .next(parentStep())
            .incrementer(new RunIdIncrementer())
            .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .job(childJob())
            .launcher(jobLauncher)
            .parametersExtractor(jobParametersExtractor())
            .listener(new StepExecutionListener() {
                @Override
                public void beforeStep(StepExecution stepExecution) {
                    stepExecution.getExecutionContext().put("name","user1");
                    stepExecution.getExecutionContext().put("age","14");
                }
                @Override
                public ExitStatus afterStep(StepExecution stepExecution) {
                    return null;
                }
            })
            .build();
    }

    private JobParametersExtractor jobParametersExtractor() {
        DefaultJobParametersExtractor extractor = new DefaultJobParametersExtractor();
        extractor.setKeys(new String[]{"name","age"});
        return extractor;
    }

    @Bean
    public Job childJob() {
        return jobBuilderFactory.get("childJob")
            .start(childStep1())
            .build();
    }

    @Bean
    public Step childStep1() {
        return stepBuilderFactory.get("childStep1")
            .tasklet((stepContribution, chunkContext) -> {
                System.out.println("I'm child Step1");

                System.out.println(chunkContext.getStepContext().getStepExecutionContext().get("age"));
                System.out.println(chunkContext.getStepContext().getJobExecutionContext().get("age"));
                return RepeatStatus.FINISHED;
            })
            .build();
    }

    @Bean
    public Step parentStep(){

        return stepBuilderFactory.get("parentStep")
            .tasklet((stepContribution, chunkContext) -> {

                System.out.println("I'm parentStep");
                return RepeatStatus.FINISHED;
            })
            .build();
    }

}
