package com.example.springBatchTutorial.job.DbDataReadWrite;

import com.example.springBatchTutorial.core.domain.accounts.Accounts;
import com.example.springBatchTutorial.core.domain.accounts.AccountsRepository;
import com.example.springBatchTutorial.core.domain.orders.Orders;
import com.example.springBatchTutorial.core.domain.orders.OrdersRepository;
import com.example.springBatchTutorial.job.JobListener.JobLoggerListener;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * desc: 주문 테이블 -> 정산 테이블 데이터 이관
 * run: --spring.batch.job.names=trMigrationJob
 */
@Configuration
@RequiredArgsConstructor
public class trMigrationConfig {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

//    @Autowired
//    private final OrdersMapper ordersMapper;
    @Bean
    public Job trMigrationJob(Step trMigrationStep){
        return jobBuilderFactory.get("trMigrationJob")
                .incrementer(new RunIdIncrementer())
                .start(trMigrationStep)
                .build();
    }

    @JobScope
    @Bean
    public Step trMigrationStep(ItemReader trOrdersReader, ItemProcessor trOrderProccesor, ItemWriter trOrderWriter){
        return stepBuilderFactory.get("trMigrationStep")
                .<Orders, Accounts>chunk(5)
                .reader(trOrdersReader)
//                .writer(new ItemWriter() {
//                    @Override
//                    public void write(List items) throws Exception {
//                        items.forEach(System.out::println);
//                    }
//                })
                .processor(trOrderProccesor)
                .writer(trOrderWriter)
                .build();

    }

    @StepScope
    @Bean
    public ItemProcessor<Orders, Accounts> trOrderProccesor(){
        return new ItemProcessor<Orders, Accounts>() {
            @Override
            public Accounts process(Orders item) throws Exception {
                return new Accounts(item);
            }
        };

    }

//    @StepScope
//    @Bean
//    public RepositoryItemWriter<Accounts> trOrderWriter(){
//        return new RepositoryItemWriterBuilder<Accounts>()
//                .repository(accountsRepository)
//                .methodName("save")
//                .build();
//    }

    @StepScope
    @Bean
    public ItemWriter<Accounts> trOrderWriter(){
        return new ItemWriter<Accounts>() {
            @Override
            public void write(List<? extends Accounts> items) throws Exception {
                items.forEach(item -> accountsRepository.save(item));
            }
        };
    }

    @StepScope
    @Bean
    public RepositoryItemReader<Orders> trOrdersReader(){
        return new RepositoryItemReaderBuilder<Orders>()
                .name("trOrdersReader")
                .repository(ordersRepository)
                .methodName("findAll")
                .pageSize(5)
                .arguments(Arrays.asList())
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

//    @StepScope
//    @Bean
//    public MyBatisPagingItemReader<Orders> trOrdersReader() {
//        MyBatisPagingItemReader<Orders> reader = new MyBatisPagingItemReader<>();
//        reader.setSqlSessionFactory(sqlSessionFactory);
//        reader.setQueryId("com.example.springBatchTutorial.core.mapper.OrdersMapper.findAll");
//        reader.setPageSize(5);
//        return reader;
//    }


    @StepScope
    @Bean
    public ItemReader<Orders> trOrdersReader() {
        return new ItemReader<Orders>() {
            private int currentPage = 0;
            private static final int PAGE_SIZE = 5;

            @Override
            public Orders read() throws Exception {
//                List<Orders> orders = ordersMapper.findAll(new RowBounds(currentPage * PAGE_SIZE, PAGE_SIZE));
//                if (orders.isEmpty()) {
//                    return null; // 더 이상 읽을 데이터가 없으면 null 반환
//                }
//                currentPage++;
//                return orders.get(0); // 한 번에 한 개씩 처리하기 위해 리스트의 첫 번째 아이템 반환
                return null;
            }
        };
    }



}
