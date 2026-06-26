package com.example.uservendor.dto;

public class VendorProfileDTO {
    private String businessName;
    private String description;

    public VendorProfileDTO() {}

    public VendorProfileDTO(String businessName, String description) {
        this.businessName = businessName;
        this.description = description;
    }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}