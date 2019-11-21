package it.fastweb.simboxbatch;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jmx.export.MBeanExporter;

import javax.sql.DataSource;
import java.io.File;
import java.util.Date;
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

    private Session session;
    private ChannelSftp c;


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

            c = (ChannelSftp) channel;

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("************************* OPEN CHANNEL *************************");
        return c;
    }

    @Bean(name = "maxDate")
    public Date queryDate(final DataSource dataSource) {

        JdbcCursorItemReader itemReader = new JdbcCursorItemReader();
        itemReader.setDataSource(dataSource);
        itemReader.setSql("SELECT max(date) date FROM simbox_batch.simbox_timestamp_idx s WHERE s.folder = 'approved_csv';");
        itemReader.setRowMapper(new SimboxRowMapper());
        ExecutionContext executionContext = new ExecutionContext();
        itemReader.open(executionContext);
        SimboxTimestampIdx s = null;

        try {
            s = (SimboxTimestampIdx) itemReader.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
        itemReader.close();

        System.out.println("************************* MAX DATE: " + s.getDate());
        return s.getDate();
    }

    @Bean(name = "simboxTempDir")
    public File createDirectory() {

        File simboxTempDir = new File("C:\\Users\\delia\\IdeaProjects\\SimboxTemp");
        if (simboxTempDir.mkdir()) {
            System.out.println("************************* Directory creata");
        } else System.out.println("************************* Directory già esistente");

        return simboxTempDir;
    }

    @Bean
    public Step step1() {

        return stepBuilderFactory.get("step1")
                .<Vector<ChannelSftp.LsEntry>, Vector<ChannelSftp.LsEntry>>chunk(1)
                .reader(new SimboxReader(c))
                .processor(new SimboxProcessor())
                .writer(new SimboxWriter(session, c))
                .build();
    }

    @Bean
    public Job importSimboxJob(Step step1) {
        return jobBuilderFactory.get("importSimboxJob")
                .incrementer(new RunIdIncrementer())
                .flow(step1)
                .end()
                .build();
    }


}
