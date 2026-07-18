package com.protocol.lab.ui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class WebController {

    @GetMapping({"/", "/phase1"})
    public String phase1(Model model) {
        model.addAttribute("title", "Phase 1: HTTP/REST");
        model.addAttribute("activeTab", "phase1");
        return "phase1-http";
    }

    @GetMapping("/phase2")
    public String phase2(Model model) {
        model.addAttribute("title", "Phase 2: WebSocket");
        model.addAttribute("activeTab", "phase2");
        return "phase2-websocket";
    }

    @GetMapping("/phase3")
    public String phase3(Model model) {
        model.addAttribute("title", "Phase 3: Socket.IO");
        model.addAttribute("activeTab", "phase3");
        return "phase3-socketio";
    }
}
