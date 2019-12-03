package it.fastweb.simboxbatch.config;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.lang.Nullable;

public class JobListener implements JobExecutionListener, StepExecutionListener{

    private Session session;
    private ChannelSftp channelSftp;
    private static final Logger log = LoggerFactory.getLogger(JobListener.class);

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
    public void beforeJob(JobExecution jobExecution) {
    }

    @Override
    public void afterJob(JobExecution jobExecution) {

    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
    }

    @Nullable
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        return null;
    }
}
