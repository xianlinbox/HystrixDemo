package com.xianlinbox.hystrix.service;

import com.xianlinbox.hystrix.model.Customer;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class CustomerServiceTest {
    private CustomerService service;

    @Before
    public void setUp() throws Exception {
        service = new CustomerService();
    }

    @Test
    public void testGetCustomer() throws Exception {
        Customer customer = service.getCustomer("1234");
        assertThat(customer.getId(), equalTo("1234"));
    }
}
