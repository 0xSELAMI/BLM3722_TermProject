package com.dealership.service;

import com.dealership.model.Customer;
import com.dealership.model.InteractionEvent;
import java.util.List;

public interface CustomerService {
    boolean registerCustomer(Customer customer);
    Customer findCustomerById(String customerId);
    boolean addInteractionEvent(InteractionEvent event);
    List<InteractionEvent> getInteractionHistoryForCustomer(String customerId);
    List<Customer> getAllCustomers();
}
