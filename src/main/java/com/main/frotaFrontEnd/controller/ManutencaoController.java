package com.main.frotaFrontEnd.controller;

import com.main.frotaFrontEnd.service.ApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/ordens")
public class ManutencaoController {

    @Autowired
    private ApiService apiService;

    @GetMapping
    public String listarOrdens(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/login";
        }
        try {
            List<Map<String, Object>> ordens = apiService.listarOrdens(token);
            model.addAttribute("ordens", ordens != null ? ordens : List.of());
        } catch (Exception e) {
            model.addAttribute("ordens", List.of());
            model.addAttribute("errorMessage", "Erro ao carregar ordens de manutenção.");
        }
        return "lista-ordens";
    }

    @GetMapping("/nova")
    public String novaOrdemForm(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/login";
        }
        try {
            List<Map<String, Object>> maquinas = apiService.listarMaquinas(token);
            model.addAttribute("maquinas", maquinas);
        } catch (Exception e) {
            model.addAttribute("maquinas", List.of());
        }
        return "nova-ordem";
    }

    @PostMapping("/nova")
    public String abrirOrdem(
            @RequestParam("idMaquina") Long idMaquina,
            @RequestParam("urgencia") String urgencia,
            @RequestParam("descricao") String descricao,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/login";
        }
        try {
            apiService.abrirOrdem(idMaquina, urgencia, descricao, token);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Ordem de manutenção aberta com sucesso!");
            return "redirect:/ordens";
        } catch (HttpStatusCodeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao abrir ordem: " + ex.getResponseBodyAsString());
            return "redirect:/ordens/nova";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro inesperado.");
            return "redirect:/ordens/nova";
        }
    }

    @PostMapping("/{id}/aprovar")
    public String aprovarOrdem(
            @PathVariable Long id,
            @RequestParam("aprovada") boolean aprovada,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!isProprietario(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/ordens";
        }
        String token = (String) session.getAttribute("token");
        try {
            apiService.aprovarOrdem(id, aprovada, token);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Ordem atualizada com sucesso!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar ordem.");
        }
        return "redirect:/ordens";
    }

    @PostMapping("/{id}/encerrar")
    public String encerrarOrdem(
            @PathVariable Long id,
            @RequestParam("observacao") String observacao,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!isProprietario(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/ordens";
        }
        String token = (String) session.getAttribute("token");
        try {
            apiService.encerrarOrdem(id, observacao, token);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Ordem encerrada com sucesso!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao encerrar ordem.");
        }
        return "redirect:/ordens";
    }

    private boolean isProprietario(HttpSession session) {
        return session.getAttribute("token") != null && "PROPRIETARIO".equals(session.getAttribute("role"));
    }
}
