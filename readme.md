# 1강 : 프로젝트 생성
- [커밋](https://github.com/jhs512/p-14107-4/commit/0001)

# 2강 : Spring Boot Docker Compose로 Redis 자동 실행
- [커밋](https://github.com/jhs512/p-14107-4/commit/0002)
- `compose.yaml`에 Redis 서비스 추가
- Spring Boot 실행 시 `spring-boot-docker-compose`가 자동으로 Redis 컨테이너 실행
- 테스트 시에는 사용되지 않음(developmentOnly 스코프로 분리)

# 3강 : Testcontainers로 테스트 시 Redis 자동 실행
- [커밋](https://github.com/jhs512/p-14107-4/commit/0003)
- `spring-boot-starter-data-redis` 의존성 추가
- `TestcontainersConfiguration`에 Redis 컨테이너 Bean 등록
- `@ServiceConnection(name = "redis")`로 Spring에 연결 정보 자동 주입
- 테스트 종료 시 컨테이너 자동 삭제

# 4강 : Docker Compose로 Redpanda Kafka 자동 실행
- [커밋](https://github.com/jhs512/p-14107-4/commit/0004)
- `compose.yaml`에 Redpanda 서비스 추가
- `--mode dev-container`로 개발용 최소 설정
- Zookeeper 없이 단독 실행 가능

# 5강 : Redpanda Console 웹 UI 추가
- [커밋](https://github.com/jhs512/p-14107-4/commit/0005)
- Redpanda Console 서비스 추가 (http://localhost:8090)
- 내부/외부 네트워크 분리 (`internal://redpanda:9092`, `external://localhost:19092`)
- Admin API 연동으로 클러스터 상태 확인 가능

# 6강 : Kafka 의존성 추가
- [커밋](https://github.com/jhs512/p-14107-4/commit/0006)
- `spring-boot-starter-kafka` 의존성 추가
- `spring-kafka-test` 테스트 의존성 추가

# 7강 : Embedded Kafka로 이벤트 발행/수신 테스트
- `@EmbeddedKafka`로 테스트용 인메모리 Kafka 사용
- `KafkaTemplate`으로 이벤트 발행
- `@KafkaListener`로 이벤트 수신
- `CountDownLatch`로 비동기 메시지 수신 대기

## src/test/java/com/back/infra/kafka/TestEvent.java
```java
package com.back.infra.kafka;

public record TestEvent(String message) {
}
```

## src/test/java/com/back/infra/kafka/TestEventListener.java
```java
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
```

## src/test/java/com/back/infra/kafka/KafkaTest.java
```java
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
```

## src/test/resources/application.properties
```properties
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.group-id=test-group
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer
spring.kafka.consumer.properties.spring.json.trusted.packages=com.back.infra.kafka
```
