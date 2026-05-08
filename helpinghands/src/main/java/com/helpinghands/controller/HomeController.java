package com.helpinghands.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.helpinghands.model.Child;
import com.helpinghands.repository.ChildRepository;
import com.helpinghands.repository.DonationRepository;

import java.util.*;

@Controller
public class HomeController {

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private DonationRepository donationRepository;

    @GetMapping("/")
    public String home(Model model) {

        List<Child> children = childRepository.findAll();

        Map<Long, Long> donationMap = new HashMap<>();

        for (Child child : children) {

            Long total = donationRepository.getTotalDonationByChildId(child.getId());

            donationMap.put(child.getId(), total != null ? total : 0);
        }

        model.addAttribute("children", children);
        model.addAttribute("donationMap", donationMap);

        return "index";
    }
}