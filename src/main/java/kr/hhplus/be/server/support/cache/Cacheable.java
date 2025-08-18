package kr.hhplus.be.server.support.cache;

import java.time.Duration;

public interface Cacheable {

    String createKey(String key);

    String cacheName();

    Duration ttl();
}
