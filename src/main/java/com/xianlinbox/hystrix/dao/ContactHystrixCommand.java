package com.xianlinbox.hystrix.dao;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.xianlinbox.hystrix.model.Contact;
import org.apache.http.client.fluent.Request;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContactHystrixCommand extends HystrixCommand<Contact> {
    private Logger logger = LoggerFactory.getLogger(ContactHystrixCommand.class);
    private String customerId;

    public ContactHystrixCommand(String customerId) {
        super(HystrixCommandGroupKey.Factory.asKey("Contact"));
        this.customerId = customerId;
    }

    @Override
    public Contact run() throws Exception {
        logger.info("Get contact for customer {}", customerId);
        String response = Request.Get("http://localhost:9090/customer/" + customerId + "/contact")
                .connectTimeout(1000)
                .socketTimeout(1000)
                .execute()
                .returnContent()
                .asString();

        return new ObjectMapper().readValue(response, Contact.class);
    }

    @Override
    protected Contact getFallback() {
        logger.info("Met error, using fallback value: {}", customerId);
        return null;
    }
}
