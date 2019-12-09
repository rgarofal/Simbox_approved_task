package it.fastweb.simboxbatch.client;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "simbox-batch")
public class DataClient {

    //configurazione db batch
    private String url_config;
    private String username_config;
    private String password_config;
    private String schema_config;

    //configurazione db tmt
    private String url_business;
    private String username_business;
    private String password_business;
    private String schema_business;



    public String getUrl_config() {
        return url_config;
    }

    public void setUrl_config(String url_config) {
        this.url_config = url_config;
    }

    public String getUsername_config() {
        return username_config;
    }

    public void setUsername_config(String username_config) {
        this.username_config = username_config;
    }

    public String getPassword_config() {
        return password_config;
    }

    public void setPassword_config(String password_config) {
        this.password_config = password_config;
    }

    public String getSchema_config() {
        return schema_config;
    }

    public void setSchema_config(String schema_config) {
        this.schema_config = schema_config;
    }

    public String getUrl_business() {
        return url_business;
    }

    public void setUrl_business(String url_business) {
        this.url_business = url_business;
    }

    public String getUsername_business() {
        return username_business;
    }

    public void setUsername_business(String username_business) {
        this.username_business = username_business;
    }

    public String getPassword_business() {
        return password_business;
    }

    public void setPassword_business(String password_business) {
        this.password_business = password_business;
    }

    public String getSchema_business() {
        return schema_business;
    }

    public void setSchema_business(String schema_business) {
        this.schema_business = schema_business;
    }
}
