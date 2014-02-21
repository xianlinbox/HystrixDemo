package com.xianlinbox.hystrix.controller;

import com.xianlinbox.hystrix.model.Customer;
import com.xianlinbox.hystrix.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @RequestMapping(value = "/customers/{customerId}", method = RequestMethod.GET)
    public Customer getCustomer(@PathVariable String customerId) {
//        return new CustomerService().getCustomer(customerId);
        return customerService.getCustomerThroughDao(customerId);
    }

    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }
}
