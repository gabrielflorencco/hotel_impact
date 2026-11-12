package com.gabrielflorencco.hotel_impact.controller;

import jakarta.servlet.http.HttpServletRequest;
import com.gabrielflorencco.hotel_impact.dto.ReservaResumoView;
import com.gabrielflorencco.hotel_impact.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final DashboardService dashboardService;

    public HomeController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/painel")
    public String index(HttpServletRequest request, Model model) {
        model.addAttribute("ind", dashboardService.indicadores());
        model.addAttribute("chegadas", dashboardService.chegadasDeHoje().stream()
                .map(ReservaResumoView::de)
                .toList());
        model.addAttribute("saidas", dashboardService.saidasDeHoje().stream()
                .map(ReservaResumoView::de)
                .toList());
        model.addAttribute("atividades", dashboardService.atividadesRecentes());
        model.addAttribute("titulo", "Dashboard");
        return Pagina.renderizar(request, "dashboard/index");
    }
}