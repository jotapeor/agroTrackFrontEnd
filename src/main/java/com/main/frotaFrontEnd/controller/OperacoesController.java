package com.main.frotaFrontEnd.controller;

import com.main.frotaFrontEnd.service.ApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
public class OperacoesController {

    private final ApiService apiService;

    public OperacoesController(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping("/operacoes")
    public String listarOperacoes(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        String role = (String) session.getAttribute("role");

        if (token == null) {
            return "redirect:/";
        }

        if (!"PROPRIETARIO".equals(role) && !"SOCIO".equals(role)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado. Apenas proprietários e sócios podem ver esta página.");
            return "redirect:/dashboard";
        }

        try {
            List<Map<String, Object>> operacoes = apiService.listarTelemetriaEmOperacao(token);
            model.addAttribute("operacoes", operacoes);
            return "operacoes";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erro ao carregar operações.");
            return "dashboard";
        }
    }

    @GetMapping("/api/operacoes/data")
    @org.springframework.web.bind.annotation.ResponseBody
    public org.springframework.http.ResponseEntity<?> pollingOperacoes(HttpSession session) {
        String token = (String) session.getAttribute("token");
        String role = (String) session.getAttribute("role");

        if (token == null || (!"PROPRIETARIO".equals(role) && !"SOCIO".equals(role))) {
            return org.springframework.http.ResponseEntity.status(403).build();
        }

        try {
            List<Map<String, Object>> operacoes = apiService.listarTelemetriaEmOperacao(token);
            return org.springframework.http.ResponseEntity.ok(operacoes);
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.status(500).build();
        }
    }
}
