package kr.hhplus.be.server.support.database;

import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;

@Component
@Profile("test")
public class RedisCacheCleaner {
    
    public void clean() {
        // Redis 캐시 정리 로직이 필요한 경우 여기에 구현
    }
}
