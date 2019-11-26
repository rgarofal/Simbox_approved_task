package it.fastweb.simboxbatch;

import com.jcraft.jsch.ChannelSftp;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import java.util.Vector;

public class SimboxReader implements ItemReader<Vector<ChannelSftp.LsEntry>> {

    private ChannelSftp channel;
    private Vector<ChannelSftp.LsEntry> fileList;

    @Autowired
    public SimboxReader(ChannelSftp channel) {
        this.channel = channel;
    }

    /**
     * si collega alla directory esterna e ne salva il contenuto in un @vector
     **/
    @Nullable
    @Override
    public Vector<ChannelSftp.LsEntry> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

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

        return fileList;

    }
}
