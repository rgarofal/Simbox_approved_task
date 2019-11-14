package it.fastweb.simboxbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jmx.export.MBeanExporter;
import javax.sql.DataSource;
import java.util.Date;

@Configuration
@EnableBatchProcessing
@Import({DatabaseConfiguration.class, MBeanExporter.class})
public class SimboxConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    @Autowired
    @Qualifier("data_config") private DataSource dataSource;

    @Bean
    public SimboxReader reader() {

        SimboxReader r = new SimboxReader();
        r.reader();

        Date date = r.queryDate(dataSource);
        System.out.println("MAX DATE " + date);

        return r;
    }

    @Bean
    public SimboxProcessor processor() {

        SimboxProcessor p = new SimboxProcessor();
        SimboxTimestampIdx s = new SimboxTimestampIdx();
        try {
            p.process(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p;
    }

    @Bean
    public SimboxWriter writer(SimboxReader r) {

        SimboxWriter w = new SimboxWriter();
        w.writer(r.getFileList(), r.getC(), r.getSession());

        return w;
    }

    @Bean
    public Step step1() {

        SimboxReader x;

        return stepBuilderFactory.get("step1")
                .<SimboxTimestampIdx, SimboxTimestampIdx> chunk(10)
                .reader(x = reader())
                .processor(processor())
                .writer(writer(x))
                .build();
    }

    @Bean
    public Job importUserJob(Step step1) {
        return jobBuilderFactory.get("importSimboxJob")
                .incrementer(new RunIdIncrementer())
                .flow(step1)
                .end()
                .build();
    }


}
