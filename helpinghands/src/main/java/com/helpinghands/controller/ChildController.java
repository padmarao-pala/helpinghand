package com.helpinghands.controller;

import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import com.helpinghands.model.Child;
import com.helpinghands.model.Donation;
import com.helpinghands.repository.ChildRepository;
import com.helpinghands.repository.DonationRepository;

@Controller
public class ChildController {

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private DonationRepository donationRepository;

    private static final String UPLOAD_DIR = "uploads/";

    // =========================
    // ✅ ADMIN DASHBOARD
    // =========================
    @GetMapping("/admin")
    public String adminDashboard(Model model) {

        // ✅ Basic stats
        model.addAttribute("totalChildren", childRepository.count());
        model.addAttribute("totalDonations", donationRepository.count());

        Long totalAmount = donationRepository.sumAllDonations();
        model.addAttribute("totalAmount", totalAmount != null ? totalAmount : 0);

        // =========================
        // ✅ TOP FUNDED CHILD
        // =========================
        List<Child> children = childRepository.findAll();

        String topChildName = "N/A";
        Long topAmount = 0L;

        for (Child c : children) {
            Long total = donationRepository.sumByChildId(c.getId());
            total = (total != null) ? total : 0;

            if (total > topAmount) {
                topAmount = total;
                topChildName = c.getName();
            }
        }

        // =========================
        // ✅ PENDING CASES
        // =========================
        long pendingCases = children.stream()
                .filter(c -> {
                    Long collected = donationRepository.sumByChildId(c.getId());
                    collected = (collected != null) ? collected : 0;
                    Long required = c.getRequiredAmount() != null ? c.getRequiredAmount() : 0;
                    return collected < required;
                })
                .count();

        // =========================
        // ✅ CHART DATA (TEMP)
        // =========================
        List<String> months = List.of("Jan","Feb","Mar","Apr","May","Jun");
        List<Long> amounts = List.of(1000L, 2000L, 1500L, 3000L, 2500L, 4000L);

        // =========================
        // ✅ SEND TO UI
        // =========================
        model.addAttribute("topChildName", topChildName);
        model.addAttribute("topAmount", topAmount);
        model.addAttribute("pendingCases", pendingCases);
        model.addAttribute("months", months);
        model.addAttribute("amounts", amounts);

        return "admin-dashboard";
    }

    // =========================
    // ✅ ADMIN CHILDREN
    @GetMapping("/admin/children")
    public String adminChildren(Model model) {

        List<Child> children = childRepository.findAll();

        Map<Long, Long> donationMap = new HashMap<>();

        for (Child child : children) {
            Long total = donationRepository.sumByChildId(child.getId());
            donationMap.put(child.getId(), total != null ? total : 0);
        }

        model.addAttribute("children", children);
        model.addAttribute("donationMap", donationMap);

        return "admin-children";
    }
    // =========================
    // ✅ ADD CHILD FORM
    // =========================
    @GetMapping("/add-child")
    public String showAddForm(Model model) {
        model.addAttribute("child", new Child());
        return "add-child";
    }

    
    
    @PostMapping("/save-child")
    public String saveChild(@Valid @ModelAttribute("child") Child child,
                            BindingResult result,
                            @RequestParam("imageFile") MultipartFile file,
                            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "add-child";
        }

        try {

            // =========================
            // ✅ IMAGE UPLOAD
            // =========================

            if (!file.isEmpty()) {

                String fileName =
                        System.currentTimeMillis() + "_" + file.getOriginalFilename();

                Path uploadPath = Paths.get(UPLOAD_DIR);

                Files.createDirectories(uploadPath);

                Files.copy(file.getInputStream(),
                        uploadPath.resolve(fileName),
                        StandardCopyOption.REPLACE_EXISTING);

                child.setImageName(fileName);
            }

            // =========================
            // ✅ UPDATE EXISTING CHILD
            // =========================

            if (child.getId() != null) {

                Child existing =
                        childRepository.findById(child.getId()).orElse(null);

                if (existing != null) {

                    existing.setName(child.getName());

                    existing.setDisease(child.getDisease());

                    existing.setRequiredAmount(child.getRequiredAmount());

                    // ✅ NEW DESCRIPTION
                    existing.setDescription(child.getDescription());

                    // ✅ IMAGE UPDATE
                    if (child.getImageName() != null) {
                        existing.setImageName(child.getImageName());
                    }

                    childRepository.save(existing);
                }

            }

            // =========================
            // ✅ ADD NEW CHILD
            // =========================

            else {

                child.setCollectedAmount(0L);

                childRepository.save(child);
            }

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Child Saved Successfully"
            );

        }

        catch (Exception e) {

            e.printStackTrace();

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Something went wrong"
            );
        }

        return "redirect:/admin/children";
    }
    // =========================
    // ✅ EDIT CHILD
    // =========================
    @GetMapping("/edit-child/{id}")
    public String editChild(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {

        Child child = childRepository.findById(id).orElse(null);

        if (child == null) {
            redirectAttributes.addFlashAttribute("error", "Child Not Found");
            return "redirect:/admin/children";
        }

        model.addAttribute("child", child);
        return "edit-child";
    }

    // =========================
    // ✅ UPDATE CHILD (SAFE VERSION)
    // =========================
    @PostMapping("/update-child")
    public String updateChild(@RequestParam Long id,
                              @RequestParam String name,
                              @RequestParam String disease,
                              @RequestParam Long requiredAmount,
                              @RequestParam("imageFile") MultipartFile file,
                              RedirectAttributes redirectAttributes) {

        try {
            Child existing = childRepository.findById(id).orElse(null);

            if (existing == null) {
                redirectAttributes.addFlashAttribute("error", "Child not found");
                return "redirect:/admin/children";
            }

            existing.setName(name);
            existing.setDisease(disease);
            existing.setRequiredAmount(requiredAmount);

            // ✅ image update
            if (!file.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

                Path uploadPath = Paths.get(UPLOAD_DIR);
                Files.createDirectories(uploadPath);

                Files.copy(file.getInputStream(),
                        uploadPath.resolve(fileName),
                        StandardCopyOption.REPLACE_EXISTING);

                existing.setImageName(fileName);
            }

            childRepository.save(existing);

            redirectAttributes.addFlashAttribute("success", "Updated Successfully");

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Update Failed");
        }

        return "redirect:/admin/children";
    }

    
    // =========================
    // ✅ DELETE CHILD
    // =========================
    @GetMapping("/delete-child/{id}")
    public String deleteChild(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        try {
            donationRepository.deleteAll(donationRepository.findByChildId(id));
            childRepository.deleteById(id);

            redirectAttributes.addFlashAttribute("success", "Deleted Successfully");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Delete Failed");
        }

        return "redirect:/admin/children";
    }

    // =========================
    @GetMapping("/donate/{id}")
    public String donatePage(@PathVariable Long id, Model model) {

        Child child = childRepository.findById(id).orElse(null);

        model.addAttribute("child", child); // ✅ IMPORTANT FIX

        return "donate";
    }

    // =========================
    // ✅ VIEW DONATIONS
    // =========================
    @GetMapping("/admin/donations")
    public String viewDonations(Model model) {
        model.addAttribute("donations", donationRepository.findAll());
        return "admin-donations";
    }
    
    
    @GetMapping("/case/{id}")
    public String caseDetails(@PathVariable Long id, Model model) {

        Child child = childRepository.findById(id).orElse(null);

        model.addAttribute("child", child);

        return "case-details";
    }

    // =========================
    // ✅ ABOUT PAGE
    // =========================
    @GetMapping("/about")
    public String aboutPage() {
        return "about";
    }

    // =========================
    // ✅ PAYMENT SUCCESS
    // =========================
    @PostMapping("/payment-success")
    public String paymentSuccess(@RequestParam Long childId,
                                 @RequestParam String donorName,
                                 @RequestParam Long amount) {

        Child child = childRepository.findById(childId).orElse(null);

        if (child != null) {

            Donation donation = new Donation();
            donation.setAmount(amount);
            donation.setDonorName(donorName);
            donation.setChild(child);
            donation.setDonatedAt(LocalDateTime.now());

            donationRepository.save(donation);

            child.setCollectedAmount(child.getCollectedAmount() + amount);
            childRepository.save(child);
        }

        return "redirect:/?success";
    }
}