package demo;

import com.netflix.hystrix.*;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategyDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadEchoCommand extends HystrixCommand<String> {
    public static final HystrixCommandKey COMMAND_KEY = HystrixCommandKey.Factory.asKey("Echo");
    private Logger logger = LoggerFactory.getLogger(ThreadEchoCommand.class);

    private String input;

    protected ThreadEchoCommand(String input) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("EchoGroup"))
                .andCommandKey(COMMAND_KEY)
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("EchoThreadPool"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                        .withCoreSize(10))
        );
        this.input = input;
    }

    @Override
    protected String run() throws Exception {
        logger.info("Run command with input: {}", input);
        Thread.currentThread().sleep(1);
        return "Echo: " + input;
    }

    @Override
    protected String getCacheKey() {
        return input;
    }

    public static void flushCache(String cacheKey) {
        HystrixRequestCache.getInstance(COMMAND_KEY,
                HystrixConcurrencyStrategyDefault.getInstance()).clear(cacheKey);
    }
}
