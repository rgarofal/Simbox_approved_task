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

public class SimboxProcessor implements ItemProcessor<Vector<ChannelSftp.LsEntry>, List<SimboxTimestampIdx>> {

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
     * Se il file è recente rispetto all'ultimo caricamento a Db, viene aggiunto in una lista
     **/
    @Nullable
    @Override
    public List<SimboxTimestampIdx> process(Vector<ChannelSftp.LsEntry> fileList) throws Exception {

        System.out.println("************************* PROCESSOR *************************");

        List<SimboxTimestampIdx> newFileList = new ArrayList<>();

        fileList.forEach(f -> {
            String dateFile = f.getAttrs().getMtimeString();

            try {
                currentFileDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(dateFile);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimboxTimestampIdx s = new SimboxTimestampIdx();

            if (maxDate.compareTo(currentFileDate) < 0) {

                s.setDate(currentFileDate);
                s.setFolder("approved_csv");
                s.setFilename(f.getFilename());
                s.setDl("CSB.SimShelf@fastweb.it");

                newFileList.add(s);

            }
        });
        System.out.println("********** LISTAAAAAA NEW " + newFileList.size());

        channel.exit();
        System.out.println("CHANNEL APERTO: " + channel.isConnected());
        session.disconnect();
        System.out.println("SESSION APERTA: " + session.isConnected());

        return newFileList;
    }

    public SimboxProcessor() {
    }
}