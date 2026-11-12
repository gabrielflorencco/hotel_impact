package com.gabrielflorencco.hotel_impact.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SiteController {

    @GetMapping("/")
    public String inicio() {
        return "site/inicio";
    }

    @GetMapping("/sobre")
    public String sobre() {
        return "site/sobre";
    }

    @GetMapping("/funcionalidades")
    public String funcionalidades() {
        return "site/funcionalidades";
    }
}