package it.fastweb.simboxbatch;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SimboxProcessor implements ItemProcessor<Vector<ChannelSftp.LsEntry>, Vector<ChannelSftp.LsEntry>> {

    private Date maxDate;
    private Date currentFileDate;
    private ChannelSftp channel;
    private Session session;

    @Autowired
    public SimboxProcessor(Date maxDate, ChannelSftp channel, Session session) {
        this.maxDate = maxDate;
        this.channel = channel;
        this.session = session;
    }


    /**
     * Scorre tutti gli elementi dell'elenco. Estrae la data dal nome di ogni file esterno e la converte in Date.
     *
     **/
    @Nullable
    @Override
    public Vector<ChannelSftp.LsEntry> process(Vector<ChannelSftp.LsEntry> fileList) throws Exception {

        System.out.println("************************* PROCESSOR *************************");

        fileList.forEach(f -> {
            System.out.println("************** FILE CONVERT: " + f);
            String dateFile = f.getAttrs().getMtimeString();

            try {
                currentFileDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(dateFile);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (maxDate.compareTo(currentFileDate) < 0) {
                System.out.println("********CURRENT DATE: " + currentFileDate);
                copyFile(f);

            }

        });
        channel.exit();
        System.out.println("CHANNEL APERTO: " + channel.isConnected());
        session.disconnect();
        System.out.println("SESSION APERTA: " + session.isConnected());
        return fileList;
    }

    public void copyFile(ChannelSftp.LsEntry f) {

        System.out.println("************************* File da caricare: " + f.getFilename());

        //prende il file esterno e lo copia in una directory temporanea
        String copy = "C:\\Users\\delia\\IdeaProjects\\SimboxTemp\\" + f.getFilename();
        File fileCopy = new File(copy);
        InputStream in = null;
        OutputStream out = null;

        try {
             in = channel.get("/home/rco/inventia_w/approved_csv/" + f.getFilename());
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
    }
}