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
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.scheduling.annotation.Scheduled;
import javax.sql.DataSource;
import java.sql.SQLException;
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
    @Qualifier("dataSource")
    private DataSource dataSource;
    @Autowired
    private SimpleJobLauncher jobLauncher;
    @Autowired
    Session session;
    @Autowired
    ChannelSftp channelSftp;

    private static final Logger log = LoggerFactory.getLogger(JobConfiguration.class);
    private static final JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();


    @Bean(name = "jdbcTemplate")
    JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

//    @Bean(name = "dataSource")
//    public DataSource batchDataSource() throws SQLException {
//
//        final SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
//        dataSource.setDriver(new com.mysql.cj.jdbc.Driver());
//        dataSource.setUrl("jdbc:mysql://localhost:3306/simbox_batch");
//        dataSource.setUsername("root");
//        dataSource.setPassword("root");
//        return dataSource;
//    }

    @Bean
    public ResourcelessTransactionManager transactionManager(){
        return new ResourcelessTransactionManager();
    }

    @Bean
    public JobRepository jobRepository(DataSource dataSource) throws Exception {
        jobRepositoryFactoryBean.setDataSource(dataSource);
        jobRepositoryFactoryBean.setTransactionManager(transactionManager());
        jobRepositoryFactoryBean.setDatabaseType("MYSQL");
        return jobRepositoryFactoryBean.getObject();
    }

    @Bean
    public SimpleJobLauncher jobLauncher(JobRepository jobRepository) {
        SimpleJobLauncher launcher = new SimpleJobLauncher();
        launcher.setJobRepository(jobRepository);
        return launcher;
    }

    @Scheduled(cron = "0 */2 * * * *")
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
                .listener(new JobListener())
                .startLimit(1)
                .build();
    }
}
