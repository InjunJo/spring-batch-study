package io.spring.batch.springbatch.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class ChunkJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job chunkJob(){

        return jobBuilderFactory.get("chunk")
            .start(chunkStep1())
            .build();
    }

    @Bean
    public Step chunkStep1() {
        return stepBuilderFactory.get("chunkStep")
            .<String,String>chunk(5)
            .reader(new ListItemReader<>(List.of("item1","item2","item3","item4","item5")))
            .processor(new ItemProcessor<String, String>() {
                @Override
                public String process(String s) throws Exception {
                    System.out.println("item : "+s);
                    return s.toUpperCase();
                }
            })
            .writer(new ItemWriter<String>() {
                @Override
                public void write(List<? extends String> list) throws Exception {

                    list.forEach(System.out::println);
                }
            })
            .build();
    }
}
