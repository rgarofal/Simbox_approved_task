package it.fastweb.simboxbatch;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class SimboxWriter implements ItemWriter<List<SimboxTimestampIdx>> {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public SimboxWriter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void write(List<? extends List<SimboxTimestampIdx>> newFileList) throws Exception {

        insertDb(newFileList.get(0));
        System.out.println("file inseriti " + newFileList.get(0).size());
    }

    private int[] insertDb(List<SimboxTimestampIdx> simboxTimestampIdx) {
        return jdbcTemplate.batchUpdate("INSERT INTO simbox_batch.simbox_timestamp_idx (date, folder, filename, dl) VALUES (?,?,?,?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setDate(1, new java.sql.Date(simboxTimestampIdx.get(i).getDate().getTime()));
                        ps.setString(2, simboxTimestampIdx.get(i).getFolder());
                        ps.setString(3, simboxTimestampIdx.get(i).getFilename());
                        ps.setString(4, simboxTimestampIdx.get(i).getDl());
                    }

                    @Override
                    public int getBatchSize() {
                        return simboxTimestampIdx.size();
                    }
                }
        );
    }

}
