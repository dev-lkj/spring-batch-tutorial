package com.example.springBatchTutorial.job.JobListener;

import com.example.springBatchTutorial.job.ValidatedParam.validator.FileParamValicator;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;


/**
 * 
 * run: --spring.batch.job.names=jobListenerJob
 */
@Configuration
@RequiredArgsConstructor
public class JobListenerConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job jobListenerJob(Step jobListenerStep){
        return jobBuilderFactory.get("jobListenerJob")
                .incrementer(new RunIdIncrementer())
                .listener(new JobLoggerListener())
                .start(jobListenerStep)
                .build();
    }

    @JobScope
    @Bean
    public Step jobListenerStep(Tasklet jobListenerTasklet){
        return stepBuilderFactory.get("jobListenerStep")
                .tasklet(jobListenerTasklet)
                .build();

    }

    @StepScope
    @Bean
    public Tasklet jobListenerTasklet(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

                System.out.println("jobListener Tasklet ");
                return RepeatStatus.FINISHED;
//                throw new Exception("Failed!!");
            }
        };
    }

}
