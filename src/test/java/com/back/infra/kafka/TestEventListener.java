package com.back.infra.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class TestEventListener {

    private final CountDownLatch latch = new CountDownLatch(1);
    private TestEvent receivedEvent;

    @KafkaListener(topics = "test-topic")
    public void listen(TestEvent event) {
        this.receivedEvent = event;
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public TestEvent getReceivedEvent() {
        return receivedEvent;
    }
}
