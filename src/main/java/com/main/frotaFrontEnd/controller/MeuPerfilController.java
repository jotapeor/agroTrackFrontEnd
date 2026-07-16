package com.main.frotaFrontEnd.controller;

import com.main.frotaFrontEnd.service.ApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class MeuPerfilController {

    @Autowired
    private ApiService apiService;

    @GetMapping("/meu-perfil")
    public String exibir(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("token") == null) {
            return "redirect:/login";
        }
        String role = (String) session.getAttribute("role");
        if (!"PROPRIETARIO".equals(role) && !"SOCIO".equals(role)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/dashboard";
        }
        String token = (String) session.getAttribute("token");
        try {
            Map<String, Object> dados = apiService.buscarMeusDados(token);
            model.addAttribute("usuario", dados);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao carregar seus dados.");
            return "redirect:/dashboard";
        }
        return "meu-perfil";
    }

    @PostMapping("/meu-perfil")
    public String salvar(@RequestParam(value = "nome", required = false) String nome,
                         @RequestParam(value = "email", required = false) String email,
                         @RequestParam(value = "foto", required = false) MultipartFile foto,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        if (session.getAttribute("token") == null) {
            return "redirect:/login";
        }
        String role = (String) session.getAttribute("role");
        if (!"PROPRIETARIO".equals(role) && !"SOCIO".equals(role)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/dashboard";
        }
        String token = (String) session.getAttribute("token");
        try {
            Map<String, Object> resposta = apiService.atualizarMeusDados(nome, email, foto, token);

            // Atualizar dados na sessão se veio novo token
            String novoTicket = (String) resposta.get("token");
            if (novoTicket != null) {
                session.setAttribute("token", novoTicket);
                session.setAttribute("nome", apiService.extrairNome(novoTicket));
                session.setAttribute("role", apiService.extrairRole(novoTicket));
                session.setAttribute("primeiroAcesso", apiService.extrairPrimeiroAcesso(novoTicket));
                session.setAttribute("userId", apiService.extrairUserId(novoTicket));
            }

            redirectAttributes.addFlashAttribute("mensagemSucesso", "Dados atualizados com sucesso!");
        } catch (HttpStatusCodeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar: " + ex.getResponseBodyAsString());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro inesperado: " + ex.getMessage());
        }
        return "redirect:/meu-perfil";
    }
}
