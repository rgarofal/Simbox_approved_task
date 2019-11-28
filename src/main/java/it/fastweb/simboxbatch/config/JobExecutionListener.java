package it.fastweb.simboxbatch.config;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
@Configuration
public class JobExecutionListener implements StepExecutionListener {

    private Session session;
    private ChannelSftp channelSftp;
    private static final Logger log = LoggerFactory.getLogger(JobExecutionListener.class);

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

        log.info("Sessione aperta: " + session.isConnected());
        return session;
    }

    @Bean(name = "channelSftp")
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

        log.info("Channel aperto: " + channelSftp.isConnected());
        return channelSftp;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("************************* BEFORE STEP *************************");
        openSession();
        openChannel();
    }

    @Nullable
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("************************* AFTER STEP *************************");
        channelSftp.exit();
        log.info("Channel aperto: " + channelSftp.isConnected());
        session.disconnect();
        log.info("Sessione aperta: " + session.isConnected());
        return null;
    }




}
