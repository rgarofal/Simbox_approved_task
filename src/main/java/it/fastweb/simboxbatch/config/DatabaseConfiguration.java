package it.fastweb.simboxbatch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import javax.sql.DataSource;
import java.sql.SQLException;

public class DatabaseConfiguration {

    @Bean(name="data_config")
    @Primary
    public DataSource batchDataSource() throws SQLException {

        final SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriver(new com.mysql.cj.jdbc.Driver());
        dataSource.setUrl("jdbc:mysql://localhost:3306/simbox_batch");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        return dataSource;
    }


}
