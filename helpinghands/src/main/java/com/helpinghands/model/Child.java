package com.helpinghands.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "child")
public class Child {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageName;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Disease is required")
    private String disease;

    @Min(value = 1, message = "Amount must be greater than 0")
    @Column(name = "required_amount")
    private Long requiredAmount;

    @Column(name = "collected_amount")
    private Long collectedAmount = 0L;

    // ✅ NEW FIELD
    @Column(length = 5000)
    private String description;

    @OneToMany(mappedBy = "child",
               cascade = CascadeType.ALL,
               orphanRemoval = true)

    private List<Donation> donations;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // =========================
    // GETTERS & SETTERS
    // =========================

    public Long getId() {
        return id;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public Long getRequiredAmount() {
        return requiredAmount;
    }

    public void setRequiredAmount(Long requiredAmount) {
        this.requiredAmount = requiredAmount;
    }

    public Long getCollectedAmount() {
        return collectedAmount;
    }

    public void setCollectedAmount(Long collectedAmount) {
        this.collectedAmount = collectedAmount;
    }

    // ✅ DESCRIPTION

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Donation> getDonations() {
        return donations;
    }

    public void setDonations(List<Donation> donations) {
        this.donations = donations;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}