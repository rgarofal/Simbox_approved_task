package it.fastweb.simboxbatch.config;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import it.fastweb.simboxbatch.batch.SimboxReader;
import it.fastweb.simboxbatch.batch.SimboxWriter;
import it.fastweb.simboxbatch.model.SimboxTimestampIdx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.scheduling.annotation.Scheduled;
import javax.sql.DataSource;
import java.util.Date;
import java.util.List;

@Configuration
@EnableBatchProcessing
@Import({DatabaseConfiguration.class, MBeanExporter.class})
public class JobConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    @Autowired
    @Qualifier("data_config")
    private DataSource dataSource;
    @Autowired
    private SimpleJobLauncher jobLauncher;
    @Autowired
    Session session;
    @Autowired
    ChannelSftp channelSftp;

    private static final Logger log = LoggerFactory.getLogger(JobConfiguration.class);

    @Bean(name = "jdbcTemplate")
    JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public ResourcelessTransactionManager transactionManager(){
        return new ResourcelessTransactionManager();
    }

    @Bean
    public MapJobRepositoryFactoryBean mapJobRepositoryFactoryBean(ResourcelessTransactionManager txManager) throws Exception {
        MapJobRepositoryFactoryBean factory = new MapJobRepositoryFactoryBean(txManager);
        factory.afterPropertiesSet();
        return factory;
    }

    @Bean
    public JobRepository jobRepository(MapJobRepositoryFactoryBean factory) throws Exception {
        return factory.getObject();
    }

    @Bean
    public SimpleJobLauncher jobLauncher(JobRepository jobRepository) {
        SimpleJobLauncher launcher = new SimpleJobLauncher();
        launcher.setJobRepository(jobRepository);
        return launcher;
    }

    @Scheduled(cron = "*/15 * * * * *")
    public void runJobScheduled() throws Exception {

        log.info("Job Started at :" + new Date());

        JobParameters param = new JobParametersBuilder().addString("JobID", String.valueOf(System.currentTimeMillis())).toJobParameters();
        JobExecution execution = jobLauncher.run(simboxJob(), param);

        log.info("Job finished with status :" + execution.getStatus());
    }

    @Bean
    public Job simboxJob() {
        return jobBuilderFactory.get("simboxJob")
                .incrementer(new RunIdIncrementer())
                .flow(step1())
                .end()
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<List<SimboxTimestampIdx>, List<SimboxTimestampIdx>>chunk(1)
                .reader(new SimboxReader(dataSource, channelSftp))
                .writer(new SimboxWriter(jdbcTemplate(dataSource)))
                .listener(new JobExecutionListener())
                .startLimit(1)
                .build();
    }



}
