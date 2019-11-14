package it.fastweb.simboxbatch;

import com.jcraft.jsch.*;
import org.springframework.batch.item.*;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;
import javax.sql.DataSource;
import java.util.Date;
import java.util.Vector;

public class SimboxReader implements ItemReader<SimboxTimestampIdx> {

    private Vector<ChannelSftp.LsEntry> fileList = null;
    private ChannelSftp c;
    private Session session;

    @Bean
    public SimboxReader reader() {

        System.out.println("////////////////////////////////// READER");

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

            Channel channel = session.openChannel("sftp");
            channel.setInputStream(System.in);
            channel.setOutputStream(System.out);
            channel.connect();

            c = (ChannelSftp) channel;

            c.cd("/home/rco/inventia_w/approved_csv");  //cambia directory

            fileList = c.ls("*.csv");

            if (fileList.capacity() == 0) {
                System.out.println("Cartella vuota, file non trovati");
            } else System.out.println("TOTALE FILES NELLA CARTELLA : " + fileList.size());

            System.out.println("CHANNEL " + c);
            System.out.println("SESSION " + session);
//            c.exit();
//            session.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }

    @Bean
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

        return s.getDate();
    }


    @Nullable
    @Override
    public SimboxTimestampIdx read() throws Exception {
        return null;
    }

    public Vector<ChannelSftp.LsEntry> getFileList() {
        return fileList;
    }

    public ChannelSftp getC() {
        return c;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void setFileList(Vector<ChannelSftp.LsEntry> fileList) {
        this.fileList = fileList;
    }

    public void setC(ChannelSftp c) {
        this.c = c;
    }
}
