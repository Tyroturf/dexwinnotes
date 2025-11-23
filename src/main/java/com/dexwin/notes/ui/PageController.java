package com.dexwin.notes.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping({"/", "/login", "/signup"})
    public String login() {
        return "login";
    }

    @GetMapping("/notes")
    public String notes() {
        return "notes";
    }
}
