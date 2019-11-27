package it.fastweb.simboxbatch;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import org.springframework.batch.item.*;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.*;

public class SimboxReader implements ItemReader<SimboxTimestampIdx> {

    private Vector<ChannelSftp.LsEntry> fileList;
    private Date currentFileDate;
    private ChannelSftp channel;
    private Session session;
    private SimboxTimestampIdx s;
    private DataSource dataSource;
    int index = 0;


    @Autowired
    public SimboxReader(DataSource dataSource, ChannelSftp channel, Session session) {
        this.dataSource = dataSource;
        this.channel = channel;
        this.session = session;
    }

    /**
     * si collega alla directory esterna e ne salva il contenuto in un @vector
     **/
    @Nullable
    @Override
    public SimboxTimestampIdx read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        System.out.println("************************* READ DIRECTORY *************************");

        try {

            channel.cd("/home/rco/inventia_w/approved_csv");  //cambia directory

            fileList = channel.ls("*.csv");

            if (fileList.capacity() == 0) {
                System.out.println("************************* Cartella vuota, file non trovati");
            } else System.out.println("************************* TOTALE FILES NELLA CARTELLA : " + fileList.size());

        } catch (Exception e) {
            e.printStackTrace();
        }


        System.out.println("************************* PROCESSOR *************************");

        List<SimboxTimestampIdx> newFileList = new ArrayList<>();

        Date maxDate = queryDate(dataSource);

        System.out.println("1111111111111111111111111111111111111111111111111111111111111111   HO RIFATTO LA QUERY " + maxDate);


        fileList.forEach(f -> {
            String dateFile = f.getAttrs().getMtimeString();

            try {
                currentFileDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(dateFile);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

            SimboxTimestampIdx s = new SimboxTimestampIdx();

            if (maxDate.compareTo(currentFileDate) < 0) {
                System.out.println("CURRENT " + currentFileDate);
                s.setDate(currentFileDate);
                s.setFolder("approved_csv");
                s.setFilename(f.getFilename());
                s.setDl("CSB.SimShelf@fastweb.it");

                newFileList.add(s);

            }
        });
        System.out.println("********** LISTAAAAAA NEW " + newFileList.size());

//        channel.exit();
//        System.out.println("CHANNEL APERTO: " + channel.isConnected());
//        session.disconnect();
//        System.out.println("SESSION APERTA: " + session.isConnected());
        if (newFileList.size() > 0 && index < 3){
            ++index;
            SimboxTimestampIdx simboxTimestampIdx = newFileList.get(index-1);

            return simboxTimestampIdx;
        } else {
            System.out.println("Non ci sono elementi da mandare al writer");
            return null;
        }



//        return newFileList;

    }

    public Date queryDate(final DataSource dataSource) {

        JdbcCursorItemReader itemReader = new JdbcCursorItemReader();
        itemReader.setDataSource(dataSource);
        itemReader.setSql("SELECT max(date) date FROM simbox_batch.simbox_timestamp_idx s WHERE s.folder = 'approved_csv';");
        itemReader.setRowMapper(new SimboxRowMapper());
        ExecutionContext executionContext = new ExecutionContext();
        itemReader.open(executionContext);

        try {
            s = (SimboxTimestampIdx) itemReader.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
        itemReader.close();

        System.out.println("************************* MAX DATE: " + s.getDate());
        return s.getDate();
    }
}
