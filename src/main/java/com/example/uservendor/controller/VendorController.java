package com.example.uservendor.controller;
import com.example.uservendor.dto.ProductDTO;
import com.example.uservendor.entity.Product;
import com.example.uservendor.entity.User;
import com.example.uservendor.entity.VendorProfile;
import com.example.uservendor.repository.ProductRepository;
import com.example.uservendor.repository.UserRepository;
import com.example.uservendor.repository.VendorProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vendor")
@CrossOrigin(origins = "*")
public class VendorController {

    private final ProductRepository productRepository;
    private final VendorProfileRepository vendorProfileRepository;
    private final UserRepository userRepository;

    // Constructor Injection
    public VendorController(ProductRepository productRepository,
                            VendorProfileRepository vendorProfileRepository,
                            UserRepository userRepository) {
        this.productRepository = productRepository;
        this.vendorProfileRepository = vendorProfileRepository;
        this.userRepository = userRepository;
    }

    // --- HELPER METHOD: Get the exact VendorProfile of the currently logged-in user ---
    private VendorProfile getAuthenticatedVendorProfile() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UserDetails) principal).getUsername();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return vendorProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Vendor Profile not found"));
    }

    // 1. CREATE: Add a new product
    @PostMapping("/products")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
        VendorProfile currentProfile = getAuthenticatedVendorProfile();

        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());

        // Lock this product strictly to the logged-in vendor
        product.setVendorProfile(currentProfile);

        Product savedProduct = productRepository.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ProductDTO(savedProduct));
    }

    // 2. READ: Get all products strictly for the logged-in vendor
    @GetMapping("/products")
    public ResponseEntity<List<ProductDTO>> getMyProducts() {
        VendorProfile currentProfile = getAuthenticatedVendorProfile();

        List<ProductDTO> myProducts = productRepository.findByVendorProfileId(currentProfile.getId())
                .stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(myProducts);
    }

    // 3. UPDATE: Modify an existing product (with security ownership check)
    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        VendorProfile currentProfile = getAuthenticatedVendorProfile();
        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Product product = optionalProduct.get();

        // SECURITY GATE: Does this product actually belong to this vendor?
        if (!product.getVendorProfile().getId().equals(currentProfile.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to edit this product.");
        }

        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());

        Product updatedProduct = productRepository.save(product);
        return ResponseEntity.ok(new ProductDTO(updatedProduct));
    }

    // 4. DELETE: Remove a product (with security ownership check)
    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        VendorProfile currentProfile = getAuthenticatedVendorProfile();
        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();

            // SECURITY GATE: Does this product actually belong to this vendor?
            if (product.getVendorProfile().getId().equals(currentProfile.getId())) {
                productRepository.delete(product);
                return ResponseEntity.ok("Product deleted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to delete this product.");
            }
        }
        return ResponseEntity.notFound().build();
    }

    // 5. READ: Get the vendor's own business profile
    @GetMapping("/profile")
    public ResponseEntity<com.example.uservendor.dto.VendorProfileDTO> getVendorProfile() {
        VendorProfile currentProfile = getAuthenticatedVendorProfile();
        return ResponseEntity.ok(new com.example.uservendor.dto.VendorProfileDTO(
                currentProfile.getBusinessName(),
                currentProfile.getDescription()
        ));
    }

    // 6. UPDATE: Modify the business profile
    @PutMapping("/profile")
    @Transactional
    public ResponseEntity<?> updateVendorProfile(@RequestBody com.example.uservendor.dto.VendorProfileDTO profileDTO) {
        VendorProfile currentProfile = getAuthenticatedVendorProfile();

        currentProfile.setBusinessName(profileDTO.getBusinessName());
        currentProfile.setDescription(profileDTO.getDescription());

        // Save to MySQL
        vendorProfileRepository.saveAndFlush(currentProfile);

        return ResponseEntity.ok("Business details updated successfully.");
    }
}