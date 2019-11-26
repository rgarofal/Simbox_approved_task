package it.fastweb.simboxbatch;

import com.jcraft.jsch.*;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.*;
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
import java.util.Vector;

@Configuration
@EnableBatchProcessing
@Import({DatabaseConfiguration.class, MBeanExporter.class})
public class SimboxConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    @Autowired
    @Qualifier("data_config")
    private DataSource dataSource;
    @Autowired
    private SimpleJobLauncher jobLauncher;

    private Session session;
    private ChannelSftp channelSftp;
    private Date maxDate;

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

    @Bean(name = "session")
    public Session openSession() {

        try {
            String user = "rco";
            int port = 2222;
            String host = "93.41.198.4";
            JSch jsch = new JSch();

            String privateKey = "C:\\Users\\delia\\IdeaProjects\\gs-batch-processing-master\\complete\\src\\main\\resources\\rco-sftp.ppk";
            jsch.addIdentity(privateKey);
            session = jsch.getSession(user, host, port);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("************************* OPEN SESSION *************************");
        return session;
    }

    @Bean(name = "channel")
    public ChannelSftp openChannel() {

        try {
            Channel channel = session.openChannel("sftp");
            channel.setInputStream(System.in);
            channel.setOutputStream(System.out);
            channel.connect();

            channelSftp = (ChannelSftp) channel;

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("************************* OPEN CHANNEL *************************");
        return channelSftp;
    }

//    @Bean(name = "maxDate")
//    public Date queryDate(final DataSource dataSource) {
//
//        JdbcCursorItemReader itemReader = new JdbcCursorItemReader();
//        itemReader.setDataSource(dataSource);
//        itemReader.setSql("SELECT max(date) date FROM simbox_batch.simbox_timestamp_idx s WHERE s.folder = 'approved_csv';");
//        itemReader.setRowMapper(new SimboxRowMapper());
//        ExecutionContext executionContext = new ExecutionContext();
//        itemReader.open(executionContext);
//
//        try {
//            s = (SimboxTimestampIdx) itemReader.read();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        itemReader.close();
//
//        maxDate = s.getDate();
//        System.out.println("************************* MAX DATE: " + maxDate);
//        return maxDate;
//    }

    @Bean(name = "jdbcTemplate")
    JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }


    @Scheduled(cron = "*/10 * * * * *")
    public void perform() throws Exception {

        System.out.println("Job Started at :" + new Date());

        JobParameters param = new JobParametersBuilder().addString("JobID", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();

        JobExecution execution = jobLauncher.run(importSimboxJob(), param);

        System.out.println("Job finished with status :" + execution.getStatus());
    }

    @Bean
    public Job importSimboxJob() {
        return jobBuilderFactory.get("importSimboxJob")
                .incrementer(new RunIdIncrementer())
                .flow(step1())
                .end()
                .build();
    }

    @Bean
    public Step step1() {

        return stepBuilderFactory.get("step1")
                .<List<SimboxTimestampIdx>, List<SimboxTimestampIdx>>chunk(1)
                .reader(new SimboxReader(dataSource, channelSftp, session))
//                .processor(new SimboxProcessor(maxDate, channelSftp, session))
                .writer(new SimboxWriter(jdbcTemplate(dataSource)))
                .listener(new JobExecutionListener())
//                .startLimit(1)
                .build();
    }



}
