package net.javaguides.springboot.dataprocessing;

import net.javaguides.springboot.annotation.LogExecutionTime;
import org.springframework.stereotype.Service;

@Service
public class DataProcess{


    @LogExecutionTime
    public void process() throws InterruptedException {

        Thread.sleep(100); // 처리 지연 시뮬레이션
    }

}