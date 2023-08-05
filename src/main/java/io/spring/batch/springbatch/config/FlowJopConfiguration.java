package io.spring.batch.springbatch.config;

import io.spring.batch.springbatch.CustomTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FlowJopConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job flowJob() {

        return jobBuilderFactory.get("flowJob")
            .start(flowStep1())
            .incrementer(new RunIdIncrementer())
            .on("COMPLETED").to(flowStep3())
            .from(flowStep1())
            .on("FAILED").to(flowStep2())
            .end()
            .build();
    }

    @Bean
    public Step flowStep1() {
        return stepBuilderFactory.get("flowStep1")
            .tasklet(new Tasklet() {
                @Override
                public RepeatStatus execute(StepContribution stepContribution,
                    ChunkContext chunkContext) throws Exception {

                    System.out.println("Hello!! spring batch!!");

                    throw new RuntimeException();

//                    return RepeatStatus.FINISHED;
                }
            }).build();
    }

    @Bean
    public Step flowStep2() {
        return stepBuilderFactory.get("step2")
            .tasklet(new Tasklet() {
                @Override
                public RepeatStatus execute(StepContribution stepContribution,
                    ChunkContext chunkContext) throws Exception {

                    System.out.println("This is For Fail step2");

                    return RepeatStatus.FINISHED;
                }
            })
            .build();
    }

    @Bean
    public Step flowStep3() {
        return stepBuilderFactory.get("step3")
            .tasklet(new Tasklet() {
                @Override
                public RepeatStatus execute(StepContribution stepContribution,
                    ChunkContext chunkContext) throws Exception {


                    System.out.println("This is Complete step3");

                    return RepeatStatus.FINISHED;
                }
            })
            .build();
    }


}
