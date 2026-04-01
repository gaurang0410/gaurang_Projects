package service;

import model.Customer;
import dao.CustomerDAO;
import java.util.List;

public class CustomerService {
    private CustomerDAO customerDAO;

    public CustomerService() {
        this.customerDAO = new CustomerDAO();
    }

    public boolean addCustomer(String name, String phone, String email, String address) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Name cannot be empty!");
            return false;
        }
        if (phone == null || phone.trim().isEmpty()) {
            System.out.println("Phone cannot be empty!");
            return false;
        }

        Customer customer = new Customer(name, phone, email, address);
        return customerDAO.addCustomer(customer);
    }

    public boolean updateCustomer(int customerId, String name, String phone, String email, String address) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Name cannot be empty!");
            return false;
        }

        Customer customer = new Customer(customerId, name, phone, email, address);
        return customerDAO.updateCustomer(customer);
    }

    public boolean deleteCustomer(int customerId) {
        return customerDAO.deleteCustomer(customerId);
    }

    public Customer getCustomerById(int customerId) {
        return customerDAO.getCustomerById(customerId);
    }

    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }

    public List<Customer> searchCustomerByName(String name) {
        return customerDAO.searchCustomerByName(name);
    }
}
