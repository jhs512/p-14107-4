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

