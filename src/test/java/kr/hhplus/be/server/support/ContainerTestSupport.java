package kr.hhplus.be.server.support;

import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.RedisContainer;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@ExtendWith(
    MySQLContainerExtension.class,
    RedisContainerExtension.class
)
public abstract class ContainerTestSupport {

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {

        MySQLContainer mySQLContainer = MySQLContainerExtension.getInstance();
        RedisContainer redisContainer = RedisContainerExtension.getInstance();

        registry.add("spring.datasource.url", () -> mySQLContainer.getJdbcUrl() + "?characterEncoding=UTF-8&serverTimezone=UTC");
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);

        registry.add("spring.redis.host", redisContainer::getHost);
        registry.add("spring.redis.port", redisContainer::getMappedPort);

    }
}
