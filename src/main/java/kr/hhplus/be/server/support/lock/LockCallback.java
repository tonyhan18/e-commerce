package kr.hhplus.be.server.support.lock;

@FunctionalInterface
public interface LockCallback<T> {

    T doInLock() throws Throwable;
}
