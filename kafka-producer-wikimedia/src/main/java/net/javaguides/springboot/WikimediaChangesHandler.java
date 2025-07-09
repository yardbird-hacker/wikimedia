package net.javaguides.springboot;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.MessageEvent;
import net.javaguides.springboot.annotation.LogExecutionTime;
import net.javaguides.springboot.dataprocessing.DataProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.*;

public class WikimediaChangesHandler implements EventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikimediaChangesHandler.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    @Value("${spring.kafka.topic.name}")
    private String topic;
    private final ThreadPoolExecutor threadPool;
    private final DataProcess dataProcess;

    // RejectedExecutionHandler 구현체
    private static class LoggingRejectedExecutionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            LOGGER.error("⚠️ 작업이 거부되었습니다! 스레드풀 포화 상태입니다.");
            // 여기서 거부된 작업을 별도 저장하거나 재처리 로직 추가 가능
        }
    }

    @Autowired
    public WikimediaChangesHandler(KafkaTemplate<String, String> kafkaTemplate, DataProcess dataProcess) {
        this.kafkaTemplate = kafkaTemplate;
        this.dataProcess = dataProcess;

        // 큐 크기 제한 및 RejectedExecutionHandler 지정
        this.threadPool = new ThreadPoolExecutor(
                4, // corePoolSize
                4, // maximumPoolSize
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(20), // 제한된 큐 크기
                new LoggingRejectedExecutionHandler()
        );
    }

    @Override
    public void onOpen() {
        LOGGER.info("🔗 Wikimedia stream 연결됨");
    }

    @Override
    public void onClosed() {
        LOGGER.info("🔌 Wikimedia stream 연결 종료");
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                LOGGER.warn("스레드풀이 정상 종료되지 않아 강제 종료 시도");
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void onMessage(String s, MessageEvent messageEvent) {
        handleMessageInternal(messageEvent.getData());
    }


    private void handleMessageInternal(String data) {
        try {
            threadPool.submit(() -> {
                try {
                    long threadId = Thread.currentThread().getId();
                    //LOGGER.info("스레드 ID: {}, 처리 시작", threadId);

                    //String data = messageEvent.getData();
                    LOGGER.info("📥 처리중인 이벤트 데이터: {}", data);

                    //Thread.sleep(100); // 처리 지연 시뮬레이션
                    dataProcess.process();


                    kafkaTemplate.send(topic, data);
                    LOGGER.info("📤 Kafka로 전송 완료");
                    //LOGGER.info("스레드 ID: {}, 처리 완료", threadId);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.error("스레드가 인터럽트됨", e);
                } catch (Exception ex) {
                    LOGGER.error("이벤트 처리 중 에러 발생", ex);
                }
            });
        } catch (RejectedExecutionException rex) {
            LOGGER.error("⚠️ 작업이 스레드풀에 제출되지 못했습니다. 작업 거부됨", rex);
            // 필요 시 거부된 작업 저장 또는 알림 처리 가능
        }
    }



    @Override
    public void onComment(String s) {
        // 필요 시 구현
    }

    @Override
    public void onError(Throwable throwable) {
        LOGGER.error("스트림 에러 발생", throwable);
    }
}
