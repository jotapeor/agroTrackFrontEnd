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

@Controller
public class ProprietarioController {
    @Autowired
    ApiService apiService;

    @GetMapping("/novo-colaborador")
    public String exibirFormulario(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!isProprietario(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/dashboard";
        }
        return "novo-colaborador";
    }

    @PostMapping("/novo-colaborador")
    public String cadastrarColaborador(
            @RequestParam("nome") String nome,
            @RequestParam("email") String email,
            @RequestParam("senha") String senha,
            @RequestParam(value = "perfil", defaultValue = "OPERADOR") String perfil,
            @RequestParam(value = "foto", required = false) MultipartFile foto,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!isProprietario(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/dashboard";
        }
        try {
            apiService.novoColaborador(nome, email, senha, perfil, foto, (String) session.getAttribute("token"));
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Colaborador cadastrado com sucesso!");
        } catch (HttpStatusCodeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao cadastrar.");
            return "redirect:/novo-colaborador";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro inesperado.");
            return "redirect:/novo-colaborador";
        }
        return "redirect:/colaboradores";
    }

    private boolean isProprietario(HttpSession session) {
        return session.getAttribute("token") != null && "PROPRIETARIO".equals(session.getAttribute("role"));
    }
}
