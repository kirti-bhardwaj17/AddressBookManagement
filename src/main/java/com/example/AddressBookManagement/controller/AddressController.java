package com.example.AddressBookManagement.controller;

import com.example.AddressBookManagement.model.Address;
import com.example.AddressBookManagement.repository.AddressRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/addresses")
public class AddressController {

    private final AddressRepository addressRepository;

    public AddressController(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    // GET all addresses
    @GetMapping
    public ResponseEntity<List<Address>> getAllAddresses() {
        return ResponseEntity.ok(addressRepository.findAll());
    }

    // GET address by ID
    @GetMapping("/{id}")
    public ResponseEntity<Address> getAddressById(@PathVariable Long id) {
        Optional<Address> address = addressRepository.findById(id);
        return address.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST - Create a new address
    @PostMapping
    public ResponseEntity<Address> createAddress(@RequestBody Address address) {
        Address savedAddress = addressRepository.save(address);
        return ResponseEntity.ok(savedAddress);
    }

    // PUT - Update address by ID
    @PutMapping("/{id}")
    public ResponseEntity<Address> updateAddress(@PathVariable Long id, @RequestBody Address addressDetails) {
        return addressRepository.findById(id).map(address -> {
            address.setName(addressDetails.getName());
            address.setPhone(addressDetails.getPhone());
            address.setEmail(addressDetails.getEmail());
            address.setCity(addressDetails.getCity());
            return ResponseEntity.ok(addressRepository.save(address));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DELETE - Remove an address
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        if (addressRepository.existsById(id)) {
            addressRepository.deleteById(id);
            return ResponseEntity.noContent().build(); // ✅ Fix: Properly returns ResponseEntity<Void>
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
