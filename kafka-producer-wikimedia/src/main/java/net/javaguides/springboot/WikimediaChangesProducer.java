package net.javaguides.springboot;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;
import net.javaguides.springboot.dataprocessing.DataProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.concurrent.TimeUnit;

@Service
public class WikimediaChangesProducer {

    @Value("${spring.kafka.topic.name}")
    private String topicName;

    private static final Logger LOGGER = LoggerFactory.getLogger(WikimediaChangesProducer.class);

    private KafkaTemplate<String, String> kafkaTemplate;
    private DataProcess dataProcess;

    public WikimediaChangesProducer(KafkaTemplate<String, String> kafkaTemplate, DataProcess dataProcess) {

        this.kafkaTemplate = kafkaTemplate;
        this.dataProcess = dataProcess;
    }

    public void sendMessage() throws InterruptedException {
        // to read real time stream data from wikimedia, we use event source
        EventHandler eventHandler = new WikimediaChangesHandler(kafkaTemplate, dataProcess);
        String url = "https://stream.wikimedia.org/v2/stream/recentchange";
        EventSource.Builder builder = new EventSource.Builder(eventHandler, URI.create(url));
        EventSource eventSource = builder.build();
        eventSource.start();

        TimeUnit.MINUTES.sleep(10);
    }
}
