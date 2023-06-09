package com.shoptech.site.address;

import com.shoptech.entity.Address;
import com.shoptech.entity.Customer;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class AddressService {
    @Autowired private AddressRopository repo;

    public List<Address> listAddressBook(Customer customer){
        return repo.findByCustomer(customer);
    }
    public void save(Address address){
        repo.save(address);
    }
    public Address get(Integer addressId, Integer customerId){
        return repo.findByIdAndCustomer(addressId, customerId);
    }
    public void delete(Integer adressId, Integer customerId){
        repo.deleteByIdAndCustomer(adressId,customerId);
    }
    public void setDefaultAddress(Integer defaultAddressId, Integer customerId ){
        if (defaultAddressId > 0 ){
            repo.setDefaultAddress(defaultAddressId);
        }
        repo.setNonDefaultForOthers(defaultAddressId,customerId);
    }
    public Address getDefaultAddress (Customer customer){
        return repo.findDefaultByCustomer(customer.getId());
    }
}
