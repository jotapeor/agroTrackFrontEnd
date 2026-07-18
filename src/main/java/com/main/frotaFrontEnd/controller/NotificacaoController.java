package com.main.frotaFrontEnd.controller;

import com.main.frotaFrontEnd.service.ApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/notificacoes")
public class NotificacaoController {

    @Autowired
    private ApiService apiService;

    @GetMapping
    public String listarNotificacoes(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/login";
        }
        try {
            List<Map<String, Object>> notificacoes = apiService.listarNotificacoes(token);
            model.addAttribute("notificacoes", notificacoes != null ? notificacoes : List.of());
        } catch (Exception e) {
            model.addAttribute("notificacoes", List.of());
            model.addAttribute("errorMessage", "Erro ao carregar notificações.");
        }
        return "lista-notificacoes";
    }

    @PostMapping("/{id}/lida")
    public String marcarComoLida(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        try {
            apiService.marcarNotificacaoLida(id, token);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar notificação.");
        }
        return "redirect:/notificacoes";
    }
}
