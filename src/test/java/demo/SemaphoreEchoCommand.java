package demo;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SemaphoreEchoCommand extends HystrixCommand<String> {
    private Logger logger = LoggerFactory.getLogger(ThreadEchoCommand.class);

    private String input;

    protected SemaphoreEchoCommand(String input) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Semaphore Echo"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("Echo"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(2)));
        this.input = input;
    }

    @Override
    protected String run() throws Exception {
        logger.info("Run command with input: {}", input);
        Thread.currentThread().sleep(100);
        return "Echo: " + input;
    }
}
