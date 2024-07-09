package com.example.springBatchTutorial.job.DbDataReadWrite;

public class ApiDbTest {

    public class ApartmentInfoTasklet implements Tasklet {

        private final RestTemplate restTemplate;

        public ApartmentInfoTasklet(RestTemplate restTemplate) {
            this.restTemplate = restTemplate;
        }

        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
            String apiUrl = "http://example.com/api/apartment"; // API URL
            List<ApartmentDto> apartmentList = restTemplate.getForObject(apiUrl, List.class);

            // 가져온 데이터를 JobExecutionContext에 저장
            chunkContext.getStepContext().getStepExecution().getJobExecution()
                    .getExecutionContext().put("apartmentList", apartmentList);

            return RepeatStatus.FINISHED;
        }
    }


    @Configuration
    @EnableBatchProcessing
    @RequiredArgsConstructor
    public class ApartmentBatchConfig {

        private final JobBuilderFactory jobBuilderFactory;
        private final StepBuilderFactory stepBuilderFactory;
        private final SqlSessionFactory sqlSessionFactory;
        private final ApartmentMapper apartmentMapper;

        @Bean
        public Job apartmentInfoJob() {
            return jobBuilderFactory.get("apartmentInfoJob")
                    .incrementer(new RunIdIncrementer())
                    .start(apartmentInfoStep())
                    .next(apartmentSaveStep())
                    .build();
        }

        @Bean
        public Step apartmentInfoStep() {
            return stepBuilderFactory.get("apartmentInfoStep")
                    .tasklet(apartmentInfoTasklet())
                    .build();
        }

        @Bean
        public Step apartmentSaveStep() {
            return stepBuilderFactory.get("apartmentSaveStep")
                    .<ApartmentDto, ApartmentEntity>chunk(10)
                    .processor(apartmentProcessor())
                    .writer(apartmentWriter())
                    .build();
        }

        @Bean
        public ApartmentInfoTasklet apartmentInfoTasklet() {
            return new ApartmentInfoTasklet(new RestTemplate());
        }

        @Bean
        public ItemProcessor<ApartmentDto, ApartmentEntity> apartmentProcessor() {
            return apartmentDto -> {
                ApartmentEntity apartmentEntity = new ApartmentEntity();
                apartmentEntity.setName(apartmentDto.getName());
                apartmentEntity.setAddress(apartmentDto.getAddress());
                apartmentEntity.setPrice(apartmentDto.getPrice());
                return apartmentEntity;
            };
        }

        @Bean
        public ItemWriter<ApartmentEntity> apartmentWriter() {
            return items -> items.forEach(apartmentMapper::insertApartment);
        }

        @Bean
        public MyBatisPagingItemReader<ApartmentEntity> apartmentReader() {
            MyBatisPagingItemReader<ApartmentEntity> reader = new MyBatisPagingItemReader<>();
            reader.setSqlSessionFactory(sqlSessionFactory);
            reader.setQueryId("com.example.springBatchTutorial.core.mapper.ApartmentMapper.findAll");
            reader.setPageSize(5);
            return reader;
        }
    }

}
