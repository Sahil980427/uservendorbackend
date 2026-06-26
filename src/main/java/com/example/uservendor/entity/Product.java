package com.example.uservendor.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // BigDecimal is the industry standard for handling money in Java
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // Links this product directly to the vendor who created it
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_profile_id", nullable = false)
    private VendorProfile vendorProfile;

    public Product() {
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public VendorProfile getVendorProfile() { return vendorProfile; }
    public void setVendorProfile(VendorProfile vendorProfile) { this.vendorProfile = vendorProfile; }
}