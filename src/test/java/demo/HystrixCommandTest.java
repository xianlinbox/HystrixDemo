package demo;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixEventType;
import com.netflix.hystrix.HystrixRequestLog;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Observer;
import rx.util.functions.Action1;

import java.util.concurrent.Future;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class HystrixCommandTest {
    private Logger logger = LoggerFactory.getLogger(HystrixCommandTest.class);

    @Test
    public void synchronousExecute() throws Exception {
        ThreadEchoCommand command = new ThreadEchoCommand("xianlinbox");
        String result = command.execute();
        assertThat(result, equalTo("Echo: xianlinbox"));
    }

    @Test
    public void asynchronousExecute() throws Exception {
        ThreadEchoCommand command = new ThreadEchoCommand("xianlinbox");
        Future<String> result = command.queue();
        while (!result.isDone()) {
            logger.info("Do other things ...");
        }
        assertThat(result.get(), equalTo("Echo: xianlinbox"));
    }

    @Test
    public void reactiveExecute1() throws Exception {
        ThreadEchoCommand command1 = new ThreadEchoCommand("xianlinbox");
        Observable<ThreadEchoCommand> result = Observable.just(command1);
        result.subscribe(new Action1<ThreadEchoCommand>() {
            @Override
            public void call(ThreadEchoCommand s) {
                logger.info("Command called. Result is:{}", s.execute());
            }
        });

        Thread.sleep(1000);
    }


    @Test
    public void reactiveExecute2() throws Exception {
        ThreadEchoCommand command1 = new ThreadEchoCommand("xianlinbox-1");
        ThreadEchoCommand command2 = new ThreadEchoCommand("xianlinbox-2");

        Observable<ThreadEchoCommand> observable = Observable.from(command1, command2);
        Observer<ThreadEchoCommand> observer = new Observer<ThreadEchoCommand>() {
            @Override
            public void onCompleted() {
                logger.info("Command Completed");
            }

            @Override
            public void onError(Throwable e) {
                logger.error("Command failled", e);
            }

            @Override
            public void onNext(ThreadEchoCommand command) {
                logger.info("Command finished,result is {}", command.execute());
            }
        };
        observable.subscribe(observer);

        Thread.sleep(1000);
    }

    @Test
    public void semaphoresCommandExecute() throws Exception {
        SemaphoreEchoCommand command = new SemaphoreEchoCommand("xianlinbox");
        assertThat(command.execute(), equalTo("Echo: xianlinbox"));
    }

    @Test
    public void semaphoresCommandMultiExecute() throws Exception {
        for (int i = 0; i < 5; i++) {
            final SemaphoreEchoCommand command = new SemaphoreEchoCommand("xianlinbox-" + i);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    command.queue();
                }
            });
            thread.start();
        }
        Thread.sleep(1000);
    }

    @Test
    public void requestCache() throws Exception {
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        try {
            ThreadEchoCommand command1 = new ThreadEchoCommand("xianlinbox");
            ThreadEchoCommand command2 = new ThreadEchoCommand("xianlinbox");

            assertThat(command1.execute(), equalTo("Echo: xianlinbox"));
            assertThat(command1.isResponseFromCache(), equalTo(false));
            assertThat(command2.execute(), equalTo("Echo: xianlinbox"));
            assertThat(command2.isResponseFromCache(), equalTo(true));
        } finally {
            context.shutdown();
        }

        context = HystrixRequestContext.initializeContext();
        try {
            ThreadEchoCommand command3 = new ThreadEchoCommand("xianlinbox");
            assertThat(command3.execute(), equalTo("Echo: xianlinbox"));
            assertThat(command3.isResponseFromCache(), equalTo(false));
        } finally {
            context.shutdown();
        }
    }

    @Test
    public void flushCacheTest() throws Exception {
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        try {
            ThreadEchoCommand command1 = new ThreadEchoCommand("xianlinbox");
            ThreadEchoCommand command2 = new ThreadEchoCommand("xianlinbox");

            assertThat(command1.execute(), equalTo("Echo: xianlinbox"));
            assertThat(command1.isResponseFromCache(), equalTo(false));
            assertThat(command2.execute(), equalTo("Echo: xianlinbox"));
            assertThat(command2.isResponseFromCache(), equalTo(true));

            ThreadEchoCommand.flushCache("xianlinbox");
            ThreadEchoCommand command3 = new ThreadEchoCommand("xianlinbox");
            assertThat(command3.execute(), equalTo("Echo: xianlinbox"));
            assertThat(command3.isResponseFromCache(), equalTo(false));
        } finally {
            context.shutdown();
        }
    }


    @Test
    public void collapseCommandTest() throws Exception {
        HystrixRequestContext context = HystrixRequestContext.initializeContext();

        try {
            Future<String> result1 = new CollapseEchoHystrixCommand("xianlinbox-1").queue();
            Future<String> result2 = new CollapseEchoHystrixCommand("xianlinbox-2").queue();
            Future<String> result3 = new CollapseEchoHystrixCommand("xianlinbox-3").queue();

            assertThat(result1.get(),equalTo("Echo: xianlinbox-1"));
            assertThat(result2.get(),equalTo("Echo: xianlinbox-2"));
            assertThat(result3.get(),equalTo("Echo: xianlinbox-3"));

            assertEquals(1, HystrixRequestLog.getCurrentRequest().getExecutedCommands().size());
        } finally {
            context.shutdown();
        }
    }

}
