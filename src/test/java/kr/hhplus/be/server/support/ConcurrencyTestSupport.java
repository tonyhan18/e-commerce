package kr.hhplus.be.server.support;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.AfterEach;
import kr.hhplus.be.server.support.database.DatabaseCleaner;

public abstract class ConcurrencyTestSupport extends IntegrationTestSupport{

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @AfterEach
    void tearDown() {
        super.tearDown();
        databaseCleaner.clean();
    }

    protected void executeConcurrency(int threadCount, Runnable runnable) {
        executeConcurrency(IntStream.range(0, threadCount)
            .mapToObj(i -> runnable)
            .toList());
    }

    protected void executeConcurrency(List<Runnable> runnables) {
        ExecutorService executorService = Executors.newFixedThreadPool(runnables.size());

        List<CompletableFuture<Void>> futures = runnables.stream()
            .map(runnable -> CompletableFuture.runAsync(() -> {
                try {
                    runnable.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, executorService))
            .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executorService.shutdown();
    }
} 