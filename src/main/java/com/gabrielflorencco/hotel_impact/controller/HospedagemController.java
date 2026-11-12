package com.gabrielflorencco.hotel_impact.controller;

import jakarta.servlet.http.HttpServletRequest;
import com.gabrielflorencco.hotel_impact.dto.ConsumoForm;
import com.gabrielflorencco.hotel_impact.dto.ConsumoResumoView;
import com.gabrielflorencco.hotel_impact.dto.HospedagemDetalheView;
import com.gabrielflorencco.hotel_impact.dto.HospedagemResumoView;
import com.gabrielflorencco.hotel_impact.dto.PagamentoForm;
import com.gabrielflorencco.hotel_impact.dto.PagamentoResumoView;
import com.gabrielflorencco.hotel_impact.model.Hospedagem;
import com.gabrielflorencco.hotel_impact.enums.EstadoHospedagem;
import com.gabrielflorencco.hotel_impact.enums.FormaPagamento;
import com.gabrielflorencco.hotel_impact.exception.NegocioException;
import com.gabrielflorencco.hotel_impact.repository.ItemCatalogoRepository;
import com.gabrielflorencco.hotel_impact.service.ConsumoService;
import com.gabrielflorencco.hotel_impact.service.HospedagemService;
import com.gabrielflorencco.hotel_impact.service.PagamentoService;
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

@Controller
@RequestMapping("/hospedagens")
public class HospedagemController {

    private static final int REGISTROS_POR_PAGINA = 15;

    private final HospedagemService service;
    private final ConsumoService consumoService;
    private final PagamentoService pagamentoService;
    private final ItemCatalogoRepository itens;

    public HospedagemController(HospedagemService service, ConsumoService consumoService,
                                PagamentoService pagamentoService, ItemCatalogoRepository itens) {
        this.service = service;
        this.consumoService = consumoService;
        this.pagamentoService = pagamentoService;
        this.itens = itens;
    }

    @GetMapping
    public String listar(HttpServletRequest request, @RequestParam(required = false) EstadoHospedagem estado,
                         @RequestParam(defaultValue = "0") int page,
                         Model model) {
        int numeroPagina = Math.max(0, page);
        Pageable pageable = PageRequest.of(numeroPagina, REGISTROS_POR_PAGINA);
        Page<HospedagemResumoView> pagina = service.buscar(estado, pageable).map(HospedagemResumoView::de);

        model.addAttribute("pagina", pagina);
        model.addAttribute("estados", EstadoHospedagem.values());
        model.addAttribute("filtroEstado", estado);
        model.addAttribute("queryString", estado == null ? "" : "&estado=" + estado);
        model.addAttribute("titulo", "Hospedagens");
        return Pagina.renderizar(request, "hospedagens/lista");
    }

    @GetMapping("/{id}")
    public String detalhe(HttpServletRequest request, @PathVariable Long id, Model model) {
        prepararDetalhe(service.buscarPorId(id), model);
        return Pagina.renderizar(request, "hospedagens/detalhe");
    }

    @PostMapping("/{id}/check-out")
    public String checkOut(HttpServletRequest request, @PathVariable Long id,
                           @RequestParam(required = false) String motivoExcecao,
                           RedirectAttributes redirect) {
        try {
            service.checkOut(id, motivoExcecao);
            redirect.addAttribute("sucesso", "Check-out realizado. Quarto enviado para limpeza.");
        } catch (NegocioException ex) {
            redirect.addAttribute("erro", ex.getMessage());
        }
        return "redirect:/hospedagens/" + id;
    }

    @PostMapping("/{id}/consumos")
    public String lancarConsumo(HttpServletRequest request, @PathVariable Long id, @Valid @ModelAttribute("consumoForm") ConsumoForm form,
                                BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            prepararDetalhe(service.buscarPorId(id), model);
            return Pagina.renderizar(request, "hospedagens/detalhe");
        }
        try {
            consumoService.lancar(id, form);
            redirect.addAttribute("sucesso", "Consumo lançado na conta da hospedagem.");
        } catch (NegocioException ex) {
            prepararDetalhe(service.buscarPorId(id), model);
            model.addAttribute("erro", ex.getMessage());
            return Pagina.renderizar(request, "hospedagens/detalhe");
        }
        return "redirect:/hospedagens/" + id;
    }

    @PostMapping("/{id}/consumos/{consumoId}/cancelar")
    public String cancelarConsumo(HttpServletRequest request, @PathVariable Long id, @PathVariable Long consumoId,
                                  RedirectAttributes redirect) {
        try {
            consumoService.cancelar(id, consumoId);
            redirect.addAttribute("sucesso", "Lançamento cancelado. O estoque foi restituído quando aplicável.");
        } catch (NegocioException ex) {
            redirect.addAttribute("erro", ex.getMessage());
        }
        return "redirect:/hospedagens/" + id;
    }

    @PostMapping("/{id}/pagamentos")
    public String registrarPagamento(HttpServletRequest request, @PathVariable Long id, @Valid @ModelAttribute("pagamentoForm") PagamentoForm form,
                                     BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            prepararDetalhe(service.buscarPorId(id), model);
            return Pagina.renderizar(request, "hospedagens/detalhe");
        }
        try {
            pagamentoService.registrar(id, form);
            redirect.addAttribute("sucesso", "Pagamento registrado na conta da hospedagem.");
        } catch (NegocioException ex) {
            prepararDetalhe(service.buscarPorId(id), model);
            model.addAttribute("erroPagamento", ex.getMessage());
            return Pagina.renderizar(request, "hospedagens/detalhe");
        }
        return "redirect:/hospedagens/" + id;
    }

    @PostMapping("/{id}/pagamentos/{pagamentoId}/estornar")
    public String estornarPagamento(HttpServletRequest request, @PathVariable Long id, @PathVariable Long pagamentoId,
                                    RedirectAttributes redirect) {
        try {
            pagamentoService.estornar(id, pagamentoId);
            redirect.addAttribute("sucesso", "Pagamento estornado. O valor deixou de contar como recebido.");
        } catch (NegocioException ex) {
            redirect.addAttribute("erro", ex.getMessage());
        }
        return "redirect:/hospedagens/" + id;
    }

    private void prepararDetalhe(Hospedagem hospedagem, Model model) {
        model.addAttribute("hospedagem", HospedagemDetalheView.de(hospedagem));
        model.addAttribute("consumos", consumoService.listarPorHospedagem(hospedagem.getId()).stream()
                .map(ConsumoResumoView::de)
                .toList());
        model.addAttribute("itensAtivos", itens.findAllByAtivoTrueOrderByNomeAsc());
        model.addAttribute("podeLancar", hospedagem.getEstado() == EstadoHospedagem.EM_ANDAMENTO);

        com.gabrielflorencco.hotel_impact.domain.Conta conta = pagamentoService.contaDe(hospedagem.getId());
        model.addAttribute("pagamentos", pagamentoService.listarPorHospedagem(hospedagem.getId()).stream()
                .map(PagamentoResumoView::de)
                .toList());
        model.addAttribute("formas", FormaPagamento.values());
        model.addAttribute("totalConsumos", conta.totalConsumos());
        model.addAttribute("totalPago", conta.pago());
        model.addAttribute("totalConta", conta.total());
        model.addAttribute("saldo", conta.saldoDevedor());
        model.addAttribute("podePagar", hospedagem.getEstado() != EstadoHospedagem.CANCELADA);

        if (!model.containsAttribute("consumoForm")) {
            model.addAttribute("consumoForm", new ConsumoForm());
        }
        if (!model.containsAttribute("pagamentoForm")) {
            model.addAttribute("pagamentoForm", new PagamentoForm());
        }
        model.addAttribute("titulo", "Detalhes da hospedagem");
    }
}