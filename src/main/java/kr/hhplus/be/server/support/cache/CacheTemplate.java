package kr.hhplus.be.server.support.cache;

import java.util.Optional;

public interface CacheTemplate {

    <T> Optional<T> get(Cacheable cacheable, String key, Class<T> type);

    <T> void put(Cacheable cacheable, String key, T value);

    void evict(Cacheable cacheable, String key);
}
