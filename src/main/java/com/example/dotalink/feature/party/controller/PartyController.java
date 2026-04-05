package com.example.dotalink.feature.party.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PartyController {

    @GetMapping("/party")
    public String partyPlaceholder() {
        return "party/index";
    }
}
