package com.menghao.batch;

import com.menghao.batch.entity.file.CsvUser;
import com.menghao.batch.listener.DemoListener;
import com.menghao.batch.processor.DemoProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.support.DatabaseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * 读取csv文件并入库
 * @author Yang
 */
@Configuration
@Slf4j
public class CsvBatchDemoConfiguration {

    @Autowired
    private  DataSource dataSource;
    @Autowired
    private  PlatformTransactionManager transactionManager;
    @Autowired
    private DemoListener demoListener;
    @Autowired
    private DemoProcessor demoProcessor;


    @Bean
    public JobRepository jobRepository() throws Exception {
        JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
        jobRepositoryFactoryBean.setDataSource(dataSource);
        jobRepositoryFactoryBean.setTransactionManager(transactionManager);
        jobRepositoryFactoryBean.setDatabaseType(DatabaseType.MYSQL.getProductName());
        return jobRepositoryFactoryBean.getObject();
    }

    @Bean
    public JobLauncher jobLauncher(JobRepository jobRepository) {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        return jobLauncher;
    }

    @Bean
    public Job csvJob(JobRepository jobRepository, Step csvStep) {
        return new JobBuilderFactory(jobRepository).get("csvJob")
                .incrementer(new RunIdIncrementer())
                .listener(demoListener)
                .start(csvStep)
                .build();
    }

    @Bean
    public Step csvStep(JobRepository jobRepository,
                        ItemReader<CsvUser> csvItemReader, ItemWriter<CsvUser> csvItemWriter) {
        return new StepBuilderFactory(jobRepository, transactionManager).get("csvStep")
                .<CsvUser, CsvUser>chunk(5)
                .reader(csvItemReader)
                .processor(demoProcessor)
                .writer(csvItemWriter)
                .build();
    }


    @Bean
    public ItemReader<CsvUser> csvItemReader() {
        return new FlatFileItemReader<CsvUser>() {{
            setResource(new ClassPathResource("test.csv"));
            // 用于设置文件与对象的映射关系
            setLineMapper(new DefaultLineMapper<CsvUser>() {{
                setLineTokenizer(new DelimitedLineTokenizer() {{
                    setNames("name", "age");
                }});
                setFieldSetMapper(new BeanWrapperFieldSetMapper<CsvUser>() {{
                    setTargetType(CsvUser.class);
                }});
            }});
        }};
    }



    @Bean
    public JdbcBatchItemWriter<CsvUser> csvItemWriter() {
        JdbcBatchItemWriter<CsvUser> writer=new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql("insert into t_csv(name,age) values (:name,:age)");
        //CsvUser中属性的值映射到对应的占位符上
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
//        writer.afterPropertiesSet(); //对参数校验是否为空
        return writer;
    }

}