package com.main.frotaFrontEnd.controller;

import com.main.frotaFrontEnd.model.UserRequestDTO;
import com.main.frotaFrontEnd.service.ApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tools.jackson.databind.ObjectMapper;

@Controller
public class AuthController {
    @Autowired
    private ApiService restService;

    @GetMapping("/")
    public String home(HttpSession session) {
        if (session.getAttribute("token") != null) {
            return "redirect:/dashboard";
        }
        return "home";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        if (session.getAttribute("token") == null) {
            return "redirect:/login";
        }
        return "dashboard";
    }

    @GetMapping("/login")
    public String login(Model model) {
        if (!model.containsAttribute("credenciais")) {
            model.addAttribute("credenciais", new UserRequestDTO());
        }
        return "login";
    }

    @PostMapping("/logar")
    public String logar(@ModelAttribute("credenciais") UserRequestDTO credenciais, BindingResult result, RedirectAttributes redirectAttributes, HttpSession session) {
        if (result.hasErrors()) {
            return "login";
        }

        try {
            String token = restService.logar(credenciais);

            session.setAttribute("token", token);
            session.setAttribute("role", restService.extrairRole(token));
            session.setAttribute("email", credenciais.getEmail());

            String nome = restService.extrairNome(token);
            session.setAttribute("nome", nome != null ? nome : credenciais.getEmail().split("@")[0]);

            return "redirect:/dashboard";

        } catch (HttpStatusCodeException ex) {
            try {
                String mensagem = new ObjectMapper()
                        .readTree(ex.getResponseBodyAsString())
                        .get("message").asText();
                redirectAttributes.addFlashAttribute("errorMessage", mensagem);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Ocorreu um erro inesperado na comunicação.");
            }
            redirectAttributes.addFlashAttribute("credenciais", credenciais);
            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/login";
        }
    }
}