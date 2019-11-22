package it.fastweb.simboxbatch;

import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SimboxPreparedStatement implements ItemPreparedStatementSetter<SimboxTimestampIdx> {

    @Override
    public void setValues(SimboxTimestampIdx simboxTimestampIdx, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setDate(1, (Date) simboxTimestampIdx.getDate());
        preparedStatement.setString(2, simboxTimestampIdx.getFolder());
        preparedStatement.setString(3, simboxTimestampIdx.getFilename());
        preparedStatement.setString(4, simboxTimestampIdx.getDl());
    }
}
