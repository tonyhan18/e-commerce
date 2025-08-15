package kr.hhplus.be.server.support.lock;

import java.util.concurrent.TimeUnit;

public interface LockTemplate {

    <T> T executeWithLock(String key, long waitTime, long leaseTime, TimeUnit timeUnit, LockCallback<T> callback) throws Throwable;

    LockStrategy getLockStrategy();

    void acquireLock(String key, long waitTime, long leaseTime, TimeUnit timeUnit) throws InterruptedException;

    void releaseLock(String key);
}
