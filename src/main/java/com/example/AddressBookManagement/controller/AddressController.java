package com.example.AddressBookManagement.controller;

import com.example.AddressBookManagement.DTO.AddressDTO;
import com.example.AddressBookManagement.model.Address;
import com.example.AddressBookManagement.interfaces.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/addresses")
@Tag(name = "Address Controller", description = "API for Address Management")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping
    @Operation(summary = "Get all addresses", description = "Fetches all addresses from the system")
    public ResponseEntity<List<Address>> getAllAddresses() {
        log.info("Received request to fetch all addresses");
        return ResponseEntity.ok(addressService.getAllAddresses());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get address by ID", description = "Fetches a specific address using its ID")
    public ResponseEntity<Address> getAddressById(@PathVariable Long id) {
        log.info("Received request to fetch address with ID: {}", id);
        Optional<Address> address = addressService.getAddressById(id);
        return address.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new address", description = "Stores a new address in the system")
    public ResponseEntity<Address> createAddress(@Valid @RequestBody AddressDTO addressDTO) {
        log.info("Received request to create address: {}", addressDTO);
        return ResponseEntity.ok(addressService.createAddress(addressDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an address", description = "Updates an existing address by ID")
    public ResponseEntity<Address> updateAddress(@PathVariable Long id, @Valid @RequestBody AddressDTO addressDTO) {
        log.info("Received request to update address with ID: {}", id);
        Address updatedAddress = addressService.updateAddress(id, addressDTO);
        return updatedAddress != null ? ResponseEntity.ok(updatedAddress) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an address", description = "Deletes an existing address by ID")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        log.info("Received request to delete address with ID: {}", id);
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}
