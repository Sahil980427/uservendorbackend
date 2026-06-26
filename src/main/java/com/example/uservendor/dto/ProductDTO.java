package com.example.uservendor.dto;
import com.example.uservendor.entity.Product;
import java.math.BigDecimal;

public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String vendorBusinessName; // Useful for the frontend later

    public ProductDTO() {}

    // Constructor to quickly convert an Entity to a DTO for outbound responses
    public ProductDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.vendorBusinessName = product.getVendorProfile().getBusinessName();
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

    public String getVendorBusinessName() { return vendorBusinessName; }
    public void setVendorBusinessName(String vendorBusinessName) { this.vendorBusinessName = vendorBusinessName; }
}