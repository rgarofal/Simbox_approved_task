package it.fastweb.simboxbatch;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SimboxRowMapper implements RowMapper<SimboxTimestampIdx> {

    public static final String ID = "id";
    public static final String DATE = "date";
    public static final String FOLDER = "folder";
    public static final String FILENAME = "filename";
    public static final String DL = "dl";


    public SimboxTimestampIdx mapRow(ResultSet rs, int rowNum) throws SQLException {

        SimboxTimestampIdx simbox = new SimboxTimestampIdx();

//        simbox.setId(rs.getInt(ID));
          simbox.setDate(rs.getTimestamp(DATE));
//        simbox.setFolder(rs.getString(FOLDER));
//        simbox.setFilename(rs.getString(FILENAME));
//        simbox.setDl(rs.getString(DL));

        return simbox;
    }

}

