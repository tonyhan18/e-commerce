package kr.hhplus.be.server.support;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

import kr.hhplus.be.server.support.database.RedisKeyCleaner;
import kr.hhplus.be.server.support.database.RedisCacheCleaner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class IntegrationTestSupport extends ContainerTestSupport{
    @Autowired
    private RedisKeyCleaner redisKeyCleaner;

    @Autowired
    protected RedisCacheCleaner redisCacheCleaner;

    @Autowired
    protected ApplicationEvents events;

    @AfterEach
    void tearDown() {
        redisKeyCleaner.clean();
        redisCacheCleaner.clean();
    }
}