package kr.hhplus.be.server.support;

import kr.hhplus.be.server.support.container.MySQLContainerExtension;
import kr.hhplus.be.server.support.container.RedisContainerExtension;
import kr.hhplus.be.server.support.container.KafkaContainerExtension;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.KafkaContainer;

@ExtendWith({
    MySQLContainerExtension.class, 
    RedisContainerExtension.class,
    KafkaContainerExtension.class
})
public abstract class ContainerTestSupport {

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {

        MySQLContainer<?> mySQLContainer = MySQLContainerExtension.getInstance();
        GenericContainer<?> redisContainer = RedisContainerExtension.getContainer();
        KafkaContainer kafkaContainer = KafkaContainerExtension.getContainer();

        registry.add("spring.datasource.url", () -> mySQLContainer.getJdbcUrl() + "?characterEncoding=UTF-8&serverTimezone=UTC");
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);

        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));

        // Kafka
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }
}
