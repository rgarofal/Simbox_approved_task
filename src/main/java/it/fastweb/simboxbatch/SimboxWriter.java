package it.fastweb.simboxbatch;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import java.io.*;
import java.util.List;
import java.util.Vector;

public class SimboxWriter implements ItemWriter<SimboxTimestampIdx> {

    @Bean
    public void writer(Vector<ChannelSftp.LsEntry> fileList, ChannelSftp c, Session s) {

        System.out.println("/////////////////////////// WRITER");
        //prende il file esterno e lo copia in una directory temporanea
        System.out.println("File da caricare: " + fileList.get(0).getFilename());
        String copy = "C:\\Users\\delia\\IdeaProjects\\SimboxTemp\\" + fileList.get(0).getFilename();
        File fileCopy = new File(copy);

        InputStream in = null;
        OutputStream out = null;
        try {
            in = c.get("/home/rco/inventia_w/approved_csv/" + fileList.get(0).getFilename());
            out = new FileOutputStream(fileCopy);

            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (IOException | SftpException e) {
            e.printStackTrace();
        }
        c.exit();
        s.disconnect();
    }

    @Override
    public void write(List<? extends SimboxTimestampIdx> list) throws Exception {

    }
}
