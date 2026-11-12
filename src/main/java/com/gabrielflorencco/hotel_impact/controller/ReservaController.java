package com.gabrielflorencco.hotel_impact.controller;

import jakarta.servlet.http.HttpServletRequest;
import com.gabrielflorencco.hotel_impact.dto.ReservaDetalheView;
import com.gabrielflorencco.hotel_impact.dto.ReservaForm;
import com.gabrielflorencco.hotel_impact.dto.ReservaResumoView;
import com.gabrielflorencco.hotel_impact.model.Hospedagem;
import com.gabrielflorencco.hotel_impact.model.Quarto;
import com.gabrielflorencco.hotel_impact.model.Reserva;
import com.gabrielflorencco.hotel_impact.enums.EstadoQuarto;
import com.gabrielflorencco.hotel_impact.enums.EstadoReserva;
import com.gabrielflorencco.hotel_impact.exception.NegocioException;
import com.gabrielflorencco.hotel_impact.service.HospedagemService;
import com.gabrielflorencco.hotel_impact.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Navegação e formulários de reservas. A criação redireciona para os detalhes
 * da reserva criada, onde o código gerado e o total calculado ficam visíveis.
 */
@Controller
@RequestMapping("/reservas")
public class ReservaController {

    private static final int REGISTROS_POR_PAGINA = 15;

    private final ReservaService service;
    private final HospedagemService hospedagemService;
    private final com.gabrielflorencco.hotel_impact.service.HospedeService hospedeService;
    private final com.gabrielflorencco.hotel_impact.service.QuartoService quartoService;

    public ReservaController(ReservaService service, HospedagemService hospedagemService,
                             com.gabrielflorencco.hotel_impact.service.HospedeService hospedeService,
                             com.gabrielflorencco.hotel_impact.service.QuartoService quartoService) {
        this.service = service;
        this.hospedagemService = hospedagemService;
        this.hospedeService = hospedeService;
        this.quartoService = quartoService;
    }

    @GetMapping
    public String listar(HttpServletRequest request, @RequestParam(required = false) EstadoReserva estado,
                         @RequestParam(defaultValue = "0") int page,
                         Model model) {
        int numeroPagina = Math.max(0, page);
        Pageable pageable = PageRequest.of(numeroPagina, REGISTROS_POR_PAGINA);
        Page<ReservaResumoView> pagina = service.buscar(estado, pageable).map(ReservaResumoView::de);

        model.addAttribute("pagina", pagina);
        model.addAttribute("estados", EstadoReserva.values());
        model.addAttribute("filtroEstado", estado);
        model.addAttribute("queryString", estado == null ? "" : "&estado=" + estado);
        model.addAttribute("titulo", "Reservas");
        return Pagina.renderizar(request, "reservas/lista");
    }

    @GetMapping("/novo")
    public String novo(HttpServletRequest request, Model model) {
        prepararFormulario(model, new ReservaForm());
        return Pagina.renderizar(request, "reservas/formulario");
    }

    @PostMapping
    public String criar(HttpServletRequest request, @Valid @ModelAttribute("form") ReservaForm form, BindingResult result, Model model,
                        RedirectAttributes redirect) {
        if (result.hasErrors()) {
            prepararFormulario(model, form);
            return Pagina.renderizar(request, "reservas/formulario");
        }
        Reserva reserva;
        try {
            reserva = service.criar(form);
        } catch (NegocioException ex) {
            prepararFormulario(model, form);
            model.addAttribute("erro", ex.getMessage());
            return Pagina.renderizar(request, "reservas/formulario");
        }
        redirect.addAttribute("sucesso", "Reserva " + reserva.getCodigo() + " criada com sucesso.");
        return "redirect:/reservas/" + reserva.getId();
    }

    @GetMapping("/{id}/editar")
    public String editar(HttpServletRequest request, @PathVariable Long id, Model model) {
        Reserva reserva = service.buscarPorId(id);
        ReservaForm form = new ReservaForm();
        form.setId(reserva.getId());
        form.setHospedeId(reserva.getHospede().getId());
        form.setQuartoId(reserva.getQuarto().getId());
        form.setDataCheckIn(reserva.getDataCheckIn());
        form.setDataCheckOut(reserva.getDataCheckOut());
        form.setQuantidadeHospedes(reserva.getQuantidadeHospedes());
        form.setValorDiaria(reserva.getValorDiaria());
        form.setDesconto(reserva.getDesconto());
        form.setMotivoExcecao(reserva.getMotivoExcecao());
        form.setObservacoes(reserva.getObservacoes());
        prepararFormulario(model, form, "Editar reserva " + reserva.getCodigo());
        return Pagina.renderizar(request, "reservas/formulario");
    }

    @PostMapping("/{id}")
    public String atualizar(HttpServletRequest request, @PathVariable Long id,
                            @Valid @ModelAttribute("form") ReservaForm form, BindingResult result,
                            Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            prepararFormulario(model, form, "Editar reserva");
            return Pagina.renderizar(request, "reservas/formulario");
        }
        try {
            service.atualizar(id, form);
        } catch (NegocioException ex) {
            prepararFormulario(model, form, "Editar reserva");
            model.addAttribute("erro", ex.getMessage());
            return Pagina.renderizar(request, "reservas/formulario");
        }
        redirect.addAttribute("sucesso", "Reserva atualizada com sucesso.");
        return "redirect:/reservas/" + id;
    }

    @GetMapping("/{id}")
    public String detalhe(HttpServletRequest request, @PathVariable Long id, Model model) {
        Reserva reserva = service.buscarPorId(id);
        model.addAttribute("reserva", ReservaDetalheView.de(reserva));
        model.addAttribute("hospedagemId",
                hospedagemService.buscarPorReserva(id).map(Hospedagem::getId).orElse(null));
        model.addAttribute("podeCheckIn", hospedagemService.podeFazerCheckIn(reserva));
        model.addAttribute("podeNoShow", service.podeRegistrarNoShow(reserva));
        model.addAttribute("podeEditar", service.podeEditar(reserva));
        model.addAttribute("titulo", "Detalhes da reserva");
        return Pagina.renderizar(request, "reservas/detalhe");
    }

    @PostMapping("/{id}/check-in")
    public String checkIn(HttpServletRequest request, @PathVariable Long id, RedirectAttributes redirect) {
        try {
            Hospedagem hospedagem = hospedagemService.checkIn(id);
            redirect.addAttribute("sucesso", "Check-in realizado. Quarto "
                    + hospedagem.getQuarto().getNumero() + " ocupado.");
            return "redirect:/hospedagens/" + hospedagem.getId();
        } catch (NegocioException ex) {
            redirect.addAttribute("erro", ex.getMessage());
            return "redirect:/reservas/" + id;
        }
    }

    @PostMapping("/{id}/no-show")
    public String registrarNoShow(HttpServletRequest request, @PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.registrarNoShow(id);
            redirect.addAttribute("sucesso", "No-show registrado. O período voltou a ficar disponível.");
        } catch (NegocioException ex) {
            redirect.addAttribute("erro", ex.getMessage());
        }
        return "redirect:/reservas/" + id;
    }

    @PostMapping("/{id}/confirmar")
    public String confirmar(HttpServletRequest request, @PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.confirmar(id);
            redirect.addAttribute("sucesso", "Reserva confirmada.");
        } catch (NegocioException ex) {
            redirect.addAttribute("erro", ex.getMessage());
        }
        return "redirect:/reservas/" + id;
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(HttpServletRequest request, @PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.cancelar(id);
            redirect.addAttribute("sucesso", "Reserva cancelada. O período voltou a ficar disponível.");
        } catch (NegocioException ex) {
            redirect.addAttribute("erro", ex.getMessage());
        }
        return "redirect:/reservas/" + id;
    }

    private void prepararFormulario(Model model, ReservaForm form) {
        prepararFormulario(model, form, "Nova reserva");
    }

    private void prepararFormulario(Model model, ReservaForm form, String titulo) {
        model.addAttribute("form", form);
        model.addAttribute("hospedes", hospedeService.listarTodos());
        model.addAttribute("quartos", quartoService.listarReservaveis());
        model.addAttribute("titulo", titulo);
    }
}