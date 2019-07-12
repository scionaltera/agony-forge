package com.agonyforge.core.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PublicController {
    @RequestMapping("/public/privacy")
    public String privacy() {
        return "privacy";
    }

    @RequestMapping("/play")
    public String play() {
        return "play";
    }
}
