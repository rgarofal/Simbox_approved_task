package it.fastweb.simboxbatch.config;

import it.fastweb.simboxbatch.client.DataClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import javax.sql.DataSource;
import java.sql.SQLException;

//@Configuration
public class DatabaseConfiguration {

    private DataClient dataClient;

    @Autowired
    public DatabaseConfiguration(DataClient dataClient) {

        this.dataClient = dataClient;
    }

//    @Bean(name="data_config")
//    @Primary
//    public DataSource batchDataSource() throws SQLException {
//
//        final SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
//        dataSource.setDriver(new com.mysql.cj.jdbc.Driver());
//        dataSource.setUrl("jdbc:mysql://localhost:3306/simbox_batch?useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC");
//        dataSource.setUsername("root");
//        dataSource.setPassword("root");
//        dataSource.setSchema("simbox_batch");
//        return dataSource;
//    }
//
//    @Bean(name="data_business")
//    public DataSource businesDataSource() throws SQLException {
//
//        final SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
//        dataSource.setDriver(new com.mysql.cj.jdbc.Driver());
//        dataSource.setUrl("jdbc:mysql://localhost:3306/spr_batch_business?useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC");
//        dataSource.setUsername("root");
//        dataSource.setPassword("root");
//        dataSource.setSchema("spr_batch_business");
//        return dataSource;
//    }



    @Bean(name="data_config")
    @Primary
    public DataSource batchDataSource() throws SQLException {

        final SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriver(new com.mysql.cj.jdbc.Driver());
        dataSource.setUrl(dataClient.getUrl_config());
        dataSource.setUsername(dataClient.getUsername_config());
        dataSource.setPassword(dataClient.getPassword_config());
        dataSource.setSchema(dataClient.getSchema_config());
        return dataSource;
    }

    @Bean(name="data_business")
    public DataSource businesDataSource() throws SQLException {

        final SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriver(new com.mysql.cj.jdbc.Driver());
        dataSource.setUrl(dataClient.getUrl_business());
        dataSource.setUsername(dataClient.getUsername_business());
        dataSource.setPassword(dataClient.getPassword_business());
        dataSource.setSchema(dataClient.getSchema_business());
        return dataSource;
    }
}
