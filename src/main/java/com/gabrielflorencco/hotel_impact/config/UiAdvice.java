package com.gabrielflorencco.hotel_impact.config;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@ControllerAdvice
public class UiAdvice {

    @ModelAttribute
    public void adicionarAtributosComuns(Model model) {
        model.addAttribute("hoje", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }
}