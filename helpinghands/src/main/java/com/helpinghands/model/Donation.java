package com.helpinghands.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long amount;             // Donation amount in INR
    private String donorName;        // Donor's name
    private LocalDateTime donatedAt; // Donation timestamp

    @ManyToOne
    @JoinColumn(name = "child_id")
    private Child child;             // Child for donation

    private String orderId;          // Razorpay order ID
    private String paymentId;        // Razorpay payment ID
    private String status;           // CREATED, PAID, FAILED

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }

    public String getDonorName() { return donorName; }
    public void setDonorName(String donorName) { this.donorName = donorName; }

    public LocalDateTime getDonatedAt() { return donatedAt; }
    public void setDonatedAt(LocalDateTime donatedAt) { this.donatedAt = donatedAt; }

    public Child getChild() { return child; }
    public void setChild(Child child) { this.child = child; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}