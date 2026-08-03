package com.main.frotaFrontEnd.controller.comum;

import com.main.frotaFrontEnd.service.AuthApiService;
import com.main.frotaFrontEnd.service.ColaboradorApiService;
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
    private AuthApiService authApiService;
    @Autowired
    private ColaboradorApiService colaboradorApiService;

    @GetMapping("/api/usuarios/email-disponivel")
    @ResponseBody
    public Map<String, Object> verificarEmailDisponivel(
            @RequestParam("email") String email,
            @RequestParam(value = "idAtual", required = false) Long idAtual,
            HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return Map.of("disponivel", false);
        }
        try {
            Map<String, Object> resultado = colaboradorApiService.verificarEmailDisponivel(email, idAtual, token);
            return resultado != null ? resultado : Map.of("disponivel", true);
        } catch (Exception e) {
            return Map.of("disponivel", true);
        }
    }

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
            Map<String, Object> dados = authApiService.buscarMeusDados(token);
            model.addAttribute("usuario", dados);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao carregar seus dados.");
            return "redirect:/dashboard";
        }
        return "comum/meu-perfil";
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
            Map<String, Object> resposta = authApiService.atualizarMeusDados(nome, email, foto, token);

            String novoTicket = (String) resposta.get("token");
            if (novoTicket != null) {
                session.setAttribute("token", novoTicket);
                session.setAttribute("nome", authApiService.extrairNome(novoTicket));
                session.setAttribute("role", authApiService.extrairRole(novoTicket));
                session.setAttribute("primeiroAcesso", authApiService.extrairPrimeiroAcesso(novoTicket));
                session.setAttribute("userId", authApiService.extrairUserId(novoTicket));
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
