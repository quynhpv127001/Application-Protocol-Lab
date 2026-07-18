package com.protocol.lab.ui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class WebController {

    @GetMapping
    public String index() {
        return "redirect:/phase1";
    }

    @GetMapping("/phase1")
    public String phase1Http(Model model) {
        model.addAttribute("activeTab", "phase1");
        model.addAttribute("title", "Phase 1: HTTP/REST");
        return "phase1-http";
    }
}
