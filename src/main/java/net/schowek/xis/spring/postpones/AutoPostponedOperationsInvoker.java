package net.schowek.xis.spring.postpones;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;

import javax.annotation.PostConstruct;

public class AutoPostponedOperationsInvoker {
    private static final Logger logger = LoggerFactory.getLogger(AutoPostponedOperationsInvoker.class);
    private final PostponedOperationsInvoker postponedOperationsInvoker;
    private final TaskExecutor executor;
    private final boolean autoInvoke;
    private final long sleepTime;

    public AutoPostponedOperationsInvoker(PostponedOperationsInvoker postponedOperationsInvoker,
                                          TaskExecutor executor, boolean autoInvoke, long sleepTime) {
        this.postponedOperationsInvoker = postponedOperationsInvoker;
        this.executor = executor;
        this.autoInvoke = autoInvoke;
        this.sleepTime = sleepTime;
    }

    @PostConstruct
    public void postConstruct() {
        if (autoInvoke) {
            logger.info("Starting auto postponedOperationsInvoker loop with sleepTime {}ms", sleepTime);
            executor.execute(run());
        }
    }

    private Runnable run() {
        return () -> {
            while (true) {
                postponedOperationsInvoker.invokeQueued();
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                }
            }
        };
    }
}
