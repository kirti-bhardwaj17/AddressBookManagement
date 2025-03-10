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
    public ResponseEntity<?> getAllAddresses() {
        try {
            log.info("Received request to fetch all addresses");
            return ResponseEntity.ok(addressService.getAllAddresses());
        } catch (Exception e) {
            log.error("Error fetching all addresses", e);
            return ResponseEntity.internalServerError().body("An error occurred while fetching addresses");
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get address by ID", description = "Fetches a specific address using its ID")
    public ResponseEntity<?> getAddressById(@PathVariable Long id) {
        try {
            log.info("Received request to fetch address with ID: {}", id);
            Optional<Address> address = addressService.getAddressById(id);
            return address.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error fetching address with ID: {}", id, e);
            return ResponseEntity.internalServerError().body("An error occurred while fetching the address");
        }
    }

    @PostMapping
    @Operation(summary = "Create a new address", description = "Stores a new address in the system")
    public ResponseEntity<?> createAddress(@Valid @RequestBody AddressDTO addressDTO) {
        try {
            log.info("Received request to create address: {}", addressDTO);
            return ResponseEntity.ok(addressService.createAddress(addressDTO));
        } catch (Exception e) {
            log.error("Error creating address", e);
            return ResponseEntity.internalServerError().body("An error occurred while creating the address");
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an address", description = "Updates an existing address by ID")
    public ResponseEntity<?> updateAddress(@PathVariable Long id, @Valid @RequestBody AddressDTO addressDTO) {
        try {
            log.info("Received request to update address with ID: {}", id);
            Address updatedAddress = addressService.updateAddress(id, addressDTO);
            return updatedAddress != null ? ResponseEntity.ok(updatedAddress) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating address with ID: {}", id, e);
            return ResponseEntity.internalServerError().body("An error occurred while updating the address");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an address", description = "Deletes an existing address by ID")
    public ResponseEntity<?> deleteAddress(@PathVariable Long id) {
        try {
            log.info("Received request to delete address with ID: {}", id);
            addressService.deleteAddress(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting address with ID: {}", id, e);
            return ResponseEntity.internalServerError().body("An error occurred while deleting the address");
        }
    }
}
