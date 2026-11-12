package com.gabrielflorencco.hotel_impact.controller;

import jakarta.servlet.http.HttpServletRequest;
import com.gabrielflorencco.hotel_impact.dto.HospedagemRelatorioView;
import com.gabrielflorencco.hotel_impact.service.RelatorioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/relatorios")
public class RelatorioController {

    private final RelatorioService service;

    public RelatorioController(RelatorioService service) {
        this.service = service;
    }

    @GetMapping
    public String periodo(
            HttpServletRequest request,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            Model model) {

        LocalDate fimPeriodo = fim != null ? fim : LocalDate.now();
        LocalDate inicioPeriodo = inicio != null ? inicio : fimPeriodo.withDayOfMonth(1);
        if (inicioPeriodo.isAfter(fimPeriodo)) {
            inicioPeriodo = fimPeriodo;
        }

        var resumo = service.resumoPeriodo(inicioPeriodo, fimPeriodo);
        model.addAttribute("resumo", resumo);
        model.addAttribute("hospedagens", resumo.hospedagensIniciadas().stream()
                .map(HospedagemRelatorioView::de)
                .toList());
        model.addAttribute("inicio", inicioPeriodo.toString());
        model.addAttribute("fim", fimPeriodo.toString());
        model.addAttribute("titulo", "Relatórios");
        return Pagina.renderizar(request, "relatorios/index");
    }
}