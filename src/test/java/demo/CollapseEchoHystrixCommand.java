package demo;

import com.netflix.hystrix.HystrixCollapser;
import com.netflix.hystrix.HystrixCollapserKey;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollapseEchoHystrixCommand extends HystrixCollapser<List<String>, String, String> {
    private Logger logger = LoggerFactory.getLogger(CollapseEchoHystrixCommand.class);
    private String input;

    public CollapseEchoHystrixCommand(String input) {
        super(HystrixCollapser.Setter
                .withCollapserKey(HystrixCollapserKey.Factory.asKey("Echo Collapse")));
        this.input = input;
    }

    @Override
    public String getRequestArgument() {
        logger.info("Get argument");
        return input;
    }

    @Override
    protected HystrixCommand<List<String>> createCommand(Collection<CollapsedRequest<String, String>> collapsedRequests) {
        logger.info("Create batch command");
        return new BatchCommand(collapsedRequests);
    }

    @Override
    protected void mapResponseToRequests(List<String> batchResponse, Collection<CollapsedRequest<String, String>> collapsedRequests) {
        logger.info("Mapping response to Request");
        int count = 0;
        for (CollapsedRequest<String, String> request : collapsedRequests) {
            request.setResponse(batchResponse.get(count++));
        }

    }

    private class BatchCommand extends HystrixCommand<List<String>> {
        private Collection<CollapsedRequest<String, String>> requests;

        public BatchCommand(Collection<CollapsedRequest<String, String>> requests) {
            super(HystrixCommandGroupKey.Factory.asKey("Batch"));
            this.requests = requests;
        }

        @Override
        protected List<String> run() throws Exception {
            logger.info("Run batch command");
            List<String> responses = new ArrayList<String>();
            for (CollapsedRequest<String, String> request : requests) {
                logger.info("Run request: {}", request.getArgument());
                responses.add("Echo: " + request.getArgument());
            }
            return responses;
        }
    }
}
