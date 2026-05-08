package com.helpinghands.controller;

import com.helpinghands.model.Child;
import com.helpinghands.model.Donation;
import com.helpinghands.repository.ChildRepository;
import com.helpinghands.repository.DonationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
public class DonationController {

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private ChildRepository childRepository;

    @PostMapping("/donate/{childId}")
    public Donation saveDonation(
            @PathVariable Long childId,
            @RequestParam Long amount,
            @RequestParam String donorName,
            @RequestParam String orderId,
            @RequestParam String paymentId,
            @RequestParam String status
    ) {

        Child child = childRepository.findById(childId).orElse(null);

        Donation donation = new Donation();
        donation.setAmount(amount);
        donation.setDonorName(donorName);
        donation.setOrderId(orderId);
        donation.setPaymentId(paymentId);
        donation.setStatus(status);
        donation.setDonatedAt(LocalDateTime.now());
        donation.setChild(child);

        return donationRepository.save(donation);
    }
    
    
    
    
}

