package it.fastweb.simboxbatch;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.springframework.batch.item.ItemProcessor;
import java.io.File;

public class SimboxProcessor implements ItemProcessor<SimboxTimestampIdx, SimboxTimestampIdx> {

    @Override
    public SimboxTimestampIdx process(final SimboxTimestampIdx simbox) throws Exception {

        System.out.println("////////////////////////////////// PROCESSOR");
        File simboxTempDir = new File("C:\\Users\\delia\\IdeaProjects\\SimboxTemp");
        if (simboxTempDir.mkdir()) {
            System.out.println("Directory temporanea creata");
        } else System.out.println("Directory temporanea già esistente");

        return null;
    }

}