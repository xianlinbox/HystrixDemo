package com.xianlinbox.hystrix.model;

public class Customer {
    private String id;
    private String name;
    private Contact contact;
    private Address address;

    public Customer(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public Contact getContact() {
        return contact;
    }

    public Address getAddress() {
        return address;
    }

    public String getId() {
        return id;
    }
}
