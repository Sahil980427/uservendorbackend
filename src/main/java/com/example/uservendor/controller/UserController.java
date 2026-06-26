package com.example.uservendor.controller;
import com.example.uservendor.dto.ProductDTO;
import com.example.uservendor.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    private final ProductRepository productRepository;

    public UserController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Fetch all products, but ONLY from approved and active vendors
    @GetMapping("/products")
    public ResponseEntity<List<ProductDTO>> getAvailableProducts() {
        List<ProductDTO> availableProducts = productRepository.findAll()
                .stream()
                // SECURITY GATE: Filter out products if the vendor's account is disabled or blocked
                .filter(product -> product.getVendorProfile().getUser().isEnabled())
                .filter(product -> !product.getVendorProfile().getUser().isBlocked())
                .map(ProductDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(availableProducts);
    }

    // Catch the inquiry from the User Dashboard Modal
    @PostMapping("/request-info")
    public ResponseEntity<?> submitInquiry(@RequestBody java.util.Map<String, String> payload) {
        String vendorName = payload.get("vendorName");
        String message = payload.get("message");

        // In a production environment, you would save this to an Inquiry table
        // or trigger an SMTP email to the vendor here.
        System.out.println("🚨 NEW INQUIRY FOR " + vendorName.toUpperCase() + ": " + message);

        return ResponseEntity.ok("Message securely sent to " + vendorName + "!");
    }
}