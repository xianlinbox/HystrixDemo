package com.xianlinbox.hystrix.dao;

import com.xianlinbox.hystrix.model.Contact;
import org.apache.http.client.fluent.Request;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ContactDao {
    private Logger logger = LoggerFactory.getLogger(ContactDao.class);

    public Contact getContact(String customerId) throws IOException {
        logger.info("Get contact for customer {}", customerId);
        String response = Request.Get("http://localhost:9090/customer/" + customerId + "/contact")
                .connectTimeout(1000)
                .socketTimeout(1000)
                .execute()
                .returnContent()
                .asString();

        return new ObjectMapper().readValue(response, Contact.class);
    }
}
