package com.back.infra.kafka;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = "test-topic")
class KafkaTest {
    @Autowired
    private KafkaTemplate<String, TestEvent> kafkaTemplate;
    @Autowired
    private TestEventListener listener;

    @Test
    @DisplayName("이벤트 발행 및 수신 테스트")
    void t001() throws InterruptedException {
        TestEvent event = new TestEvent("hello kafka");

        kafkaTemplate.send("test-topic", event);

        boolean received = listener.getLatch().await(10, TimeUnit.SECONDS);

        assertThat(received).isTrue();
        assertThat(listener.getReceivedEvent().message()).isEqualTo("hello kafka");
    }
}