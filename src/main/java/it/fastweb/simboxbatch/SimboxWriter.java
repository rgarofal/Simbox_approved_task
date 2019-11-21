package it.fastweb.simboxbatch;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class SimboxWriter implements ItemWriter<Vector<ChannelSftp.LsEntry>> {


    @Override
    public void write(List<? extends Vector<ChannelSftp.LsEntry>> list) throws Exception {
        System.out.println("SONO WRITER E SONO VUOTO");



    }
}
