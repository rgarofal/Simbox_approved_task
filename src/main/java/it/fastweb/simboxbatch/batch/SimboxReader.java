package it.fastweb.simboxbatch.batch;

        import com.jcraft.jsch.ChannelSftp;
        import it.fastweb.simboxbatch.model.SimboxTimestampIdx;
        import it.fastweb.simboxbatch.config.SimboxRowMapper;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;
        import org.springframework.batch.item.*;
        import org.springframework.batch.item.database.JdbcCursorItemReader;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.lang.Nullable;

        import javax.sql.DataSource;
        import java.text.SimpleDateFormat;
        import java.util.*;

public class SimboxReader implements ItemReader<List<SimboxTimestampIdx>> {

    private Vector<ChannelSftp.LsEntry> fileList;
    private Date currentFileDate;
    private ChannelSftp channel;
    private SimboxTimestampIdx s;
    private DataSource dataSource;
    private static final Logger log = LoggerFactory.getLogger(SimboxReader.class);

    @Autowired
    public SimboxReader(DataSource dataSource, ChannelSftp channel) {
        this.dataSource = dataSource;
        this.channel = channel;
    }

    /**
     * si collega alla directory esterna e ne salva il contenuto in un @vector che scorre prendendo solo i file
     * recenti e salvandoli in una @list da passare al writer
     **/
    @Nullable
    @Override
    public List<SimboxTimestampIdx> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        try {
            channel.cd("/home/rco/inventia_w/approved_csv");
            fileList = channel.ls("*.csv");
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<SimboxTimestampIdx> newFileList = new ArrayList<>();
        Date maxDate = queryDate(dataSource); //query DB
        log.info("Ultimi dati inseriti a Db in data: " + maxDate);


        if (fileList.capacity() == 0) {
            log.info("************************* Cartella vuota, file non trovati");
            return null;
        } else {
            log.info("************************* TOTALE FILES NELLA CARTELLA : " + fileList.size());
            fileList.forEach(f -> {
                String dateFile = f.getAttrs().getMtimeString();

                try {
                    currentFileDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(dateFile);
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }

                SimboxTimestampIdx s = new SimboxTimestampIdx();

                if (maxDate == null || maxDate.compareTo(currentFileDate) < 0) {
                    s.setDate(currentFileDate);
                    s.setFolder("approved_csv");
                    s.setFilename(f.getFilename());
                    s.setDl("CSB.SimShelf@fastweb.it");

                    newFileList.add(s);
                }
            });
            log.info("************************* TOTALE FILE DA CARICARE : " + newFileList.size());
        }

        if (newFileList.size() == 0) {
            log.info("Non ci sono file da mandare al writer");
            return null;
        } else {
            newFileList.forEach(f -> System.out.println("File da caricare: " + f.getFilename()));
            return newFileList;
        }
    }

    private Date queryDate(final DataSource dataSource) {

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

        return s.getDate();
    }
}
