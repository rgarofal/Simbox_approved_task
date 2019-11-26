package it.fastweb.simboxbatch;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.lang.Nullable;

public class JobExecutionListener implements StepExecutionListener {


    @Override
    public void beforeStep(StepExecution stepExecution) {
        System.out.println("***************************** BEFORE");
    }

    @Nullable
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        System.out.println("********************** AFTER");
        return null;
    }
}
