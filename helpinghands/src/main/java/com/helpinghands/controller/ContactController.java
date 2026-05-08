package com.helpinghands.controller;

import com.helpinghands.model.Contact;
import com.helpinghands.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
public class ContactController {

    @Autowired
    private ContactRepository contactRepository;

    // 📩 CONTACT PAGE
    @GetMapping("/contact")
    public String contactPage(Model model) {
        model.addAttribute("contact", new Contact());
        return "contact";
    }

    // 📩 SAVE CONTACT
    @PostMapping("/contact")
    public String saveContact(@ModelAttribute Contact contact) {

        // ✅ Set created date (VERY IMPORTANT)
        contact.setCreatedAt(LocalDateTime.now());

        contactRepository.save(contact);

        return "redirect:/contact?success";
    }

    // 🔐 ADMIN VIEW CONTACT LIST
    @GetMapping("/admin/contacts")
    public String viewContacts(Model model) {

        // ✅ Get all contacts
        var contacts = contactRepository.findAll();

        model.addAttribute("contacts", contacts);

        return "admin-contacts";
    }
}