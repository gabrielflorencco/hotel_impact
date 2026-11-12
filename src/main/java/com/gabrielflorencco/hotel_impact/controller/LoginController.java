package com.gabrielflorencco.hotel_impact.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller para a página de login: injeta o título na model para o template Thymeleaf.
 */
@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("titulo", "Entrar no sistema");
        return "login";
    }
}