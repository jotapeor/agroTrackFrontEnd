package com.main.frotaFrontEnd.controller;

import com.main.frotaFrontEnd.service.ApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Controller
public class AuthController {
    @Autowired
    private ApiService restService;

    @GetMapping("/")
    public String home(HttpSession session) {
        if (session.getAttribute("token") != null) return "redirect:/dashboard";
        return "home";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (session.getAttribute("token") == null) return "redirect:/login";
        model.addAttribute("perfil", session.getAttribute("role"));
        model.addAttribute("nomeUsuario", session.getAttribute("nome"));
        model.addAttribute("primeiroAcesso", session.getAttribute("primeiroAcesso"));
        return "dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/logar")
    public String logar(@RequestParam String email, @RequestParam String senha, RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            String token = restService.logar(email, senha);
            session.setAttribute("token", token);
            session.setAttribute("role", restService.extrairRole(token));
            session.setAttribute("email", email);
            session.setAttribute("primeiroAcesso", restService.extrairPrimeiroAcesso(token));
            session.setAttribute("nome", restService.extrairNome(token));
            session.setAttribute("userId", restService.extrairUserId(token));
            return "redirect:/dashboard";
        } catch (HttpStatusCodeException ex) {
            try {
                redirectAttributes.addFlashAttribute("errorMessage",
                        new ObjectMapper().readTree(ex.getResponseBodyAsString()).get("message").asText());
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Erro inesperado na comunicação.");
            }
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/api/autenticar/verificar-email")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> verificarEmail(@RequestParam("email") String email) {
        try {
            return ResponseEntity.ok(restService.verificarEmail(email));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("disponivel", true));
        }
    }

    @PostMapping("/api/alterar-senha")
    @ResponseBody
    public ResponseEntity<String> alterarSenha(@RequestBody Map<String, String> body, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) return ResponseEntity.status(401).body("Sessão expirada.");
        try {
            restService.alterarSenha(body.get("senha"), token);
            session.setAttribute("primeiroAcesso", "false");
            return ResponseEntity.ok("Senha alterada com sucesso.");
        } catch (HttpStatusCodeException ex) {
            String msg;
            try {
                msg = new ObjectMapper().readTree(ex.getResponseBodyAsString()).get("message").asText();
            } catch (Exception e) {
                msg = "Erro ao alterar senha.";
            }
            return ResponseEntity.status(ex.getStatusCode()).body(msg);
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Erro inesperado.");
        }
    }
}
