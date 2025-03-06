package com.example.AddressBookManagement.controller;

import com.example.AddressBookManagement.dto.AddressDTO;
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

    @GetMapping
    public ResponseEntity<List<Address>> getAllAddresses() {
        return ResponseEntity.ok(addressRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Address> getAddressById(@PathVariable Long id) {
        Optional<Address> address = addressRepository.findById(id);
        return address.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Address> createAddress(@RequestBody AddressDTO addressDTO) {
        Address address = new Address(null, addressDTO.getName(), addressDTO.getPhone(), addressDTO.getEmail(), addressDTO.getCity());
        return ResponseEntity.ok(addressRepository.save(address));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Address> updateAddress(@PathVariable Long id, @RequestBody AddressDTO addressDTO) {
        return addressRepository.findById(id).map(address -> {
            address.setName(addressDTO.getName());
            address.setPhone(addressDTO.getPhone());
            address.setEmail(addressDTO.getEmail());
            address.setCity(addressDTO.getCity());
            return ResponseEntity.ok(addressRepository.save(address));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        if (addressRepository.existsById(id)) {
            addressRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
