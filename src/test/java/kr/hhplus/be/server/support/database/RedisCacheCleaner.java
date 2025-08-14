package kr.hhplus.be.server.support.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;

@Component
@Profile("test")
public class RedisCacheCleaner {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


}
