package com.example.AddressBookManagement.services;

import com.example.AddressBookManagement.DTO.AddressDTO;
import com.example.AddressBookManagement.model.Address;
import com.example.AddressBookManagement.repository.AddressRepository;
import com.example.AddressBookManagement.interfaces.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Override
    public List<Address> getAllAddresses() {
        return addressRepository.findAll();
    }

    @Override
    public Optional<Address> getAddressById(Long id) {
        return addressRepository.findById(id);
    }

    @Override
    public Address createAddress(AddressDTO addressDTO) {
        Address address = new Address(null, addressDTO.getName(), addressDTO.getPhone(), addressDTO.getEmail(), addressDTO.getCity());
        return addressRepository.save(address);
    }

    @Override
    public Address updateAddress(Long id, AddressDTO addressDTO) {
        return addressRepository.findById(id).map(address -> {
            address.setName(addressDTO.getName());
            address.setPhone(addressDTO.getPhone());
            address.setEmail(addressDTO.getEmail());
            address.setCity(addressDTO.getCity());
            return addressRepository.save(address);
        }).orElse(null);
    }

    @Override
    public void deleteAddress(Long id) {
        addressRepository.deleteById(id);
    }
}
