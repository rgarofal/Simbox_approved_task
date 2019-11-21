package it.fastweb.simboxbatch;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.*;
import java.util.List;
import java.util.Vector;

public class SimboxWriter implements ItemWriter<Vector<ChannelSftp.LsEntry>> {

    @Autowired
    Session session;
    @Autowired
    ChannelSftp channel;
    @Autowired
    Vector<ChannelSftp.LsEntry> fileList;
//
//    @Bean
//    public void copyFile() {
//
//        System.out.println("************************* WRITER *************************");
//        System.out.println("************************* File da caricare: " + fileList.get(0).getFilename());
//
//        //prende il file esterno e lo copia in una directory temporanea
//        String copy = "C:\\Users\\delia\\IdeaProjects\\SimboxTemp\\" + fileList.get(0).getFilename();
//        File fileCopy = new File(copy);
//
//        InputStream in;
//        OutputStream out;
//
//        try {
//            in = channel.get("/home/rco/inventia_w/approved_csv/" + fileList.get(0).getFilename());
//            out = new FileOutputStream(fileCopy);
//
//            byte[] buf = new byte[1024];
//            int len;
//
//            while ((len = in.read(buf)) > 0) {
//                out.write(buf, 0, len);
//            }
//            in.close();
//            out.close();
//        } catch (IOException | SftpException e) {
//            e.printStackTrace();
//        }
//        channel.exit();
//        session.disconnect();
//    }
//


    @Override
    public void write(List<? extends Vector<ChannelSftp.LsEntry>> list) throws Exception {
        channel.exit();
        session.disconnect();
    }
}
