package it.fastweb.simboxbatch;

import com.jcraft.jsch.ChannelSftp;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.Nullable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

public class SimboxProcessor implements ItemProcessor<Vector<ChannelSftp.LsEntry>, Vector<ChannelSftp.LsEntry>> {

    private Date currentFileDate;

    /**
     * scorre tutti gli elementi dell'elenco. Estrae la data dal nome di ogni file esterno e la converte in Date
     **/
    @Nullable
    @Override
    public Vector<ChannelSftp.LsEntry> process(Vector<ChannelSftp.LsEntry> fileList) throws Exception {

        fileList.forEach(f -> {
            System.out.println("************** FILE CONVERT: " + f);
            String dateFile = f.getAttrs().getMtimeString();
            try {
                currentFileDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(dateFile);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        });
        return fileList;
    }


}