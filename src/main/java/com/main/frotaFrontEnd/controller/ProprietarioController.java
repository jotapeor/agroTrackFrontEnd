package com.main.frotaFrontEnd.controller;

import com.main.frotaFrontEnd.model.UserDTO;
import com.main.frotaFrontEnd.service.ApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
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
        model.addAttribute("userDTO", new UserDTO());
        return "novo-colaborador";
    }

    @PostMapping("/novo-colaborador")
    public String cadastrarColaborador(@ModelAttribute("userDTO") UserDTO userDTO, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isProprietario(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/dashboard";
        }
        try {
            apiService.novoColaborador(userDTO, (String) session.getAttribute("token"));
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Colaborador cadastrado com sucesso!");
        } catch (HttpStatusCodeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao cadastrar.");
            return "redirect:/novo-colaborador";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro inesperado.");
            return "redirect:/novo-colaborador";
        }
        return "redirect:/dashboard";
    }

    private boolean isProprietario(HttpSession session) {
        return session.getAttribute("token") != null && "PROPRIETARIO".equals(session.getAttribute("role"));
    }
}
