package com.xianlinbox.hystrix.dao;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.xianlinbox.hystrix.model.Address;
import org.apache.http.client.fluent.Request;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddressHystrixCommand extends HystrixCommand<Address> {
    private Logger logger = LoggerFactory.getLogger(AddressHystrixCommand.class);
    private String customerId;

    public AddressHystrixCommand(String customerId) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Address")));
        this.customerId = customerId;
    }

    @Override
    public Address run() throws Exception {
        logger.info("Get address for customer {}", customerId);
        String response = Request.Get("http://localhost:9090/customer/" + customerId + "/address")
                .connectTimeout(1000)
                .socketTimeout(1000)
                .execute()
                .returnContent()
                .asString();

        return new ObjectMapper().readValue(response, Address.class);
    }

    @Override
    protected Address getFallback() {
        logger.info("Met error, using fallback value: {}", customerId);
        return null;
    }
}
