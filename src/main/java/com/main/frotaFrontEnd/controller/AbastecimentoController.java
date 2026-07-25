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
@RequestMapping("/abastecimentos")
public class AbastecimentoController {

    @Autowired
    private ApiService apiService;

    @GetMapping
    public String exibirFormulario(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/login";
        }
        try {
            List<Map<String, Object>> maquinas = apiService.listarMaquinas(token);
            model.addAttribute("maquinas", maquinas != null ? maquinas : List.of());
        } catch (Exception e) {
            model.addAttribute("maquinas", List.of());
        }
        return "abastecimento";
    }

    @PostMapping
    public String registrar(@RequestParam("idMaquina") Long idMaquina,
                            @RequestParam(value = "dataAbastecimento", required = false) String dataAbastecimento,
                            @RequestParam("litros") String litros,
                            @RequestParam("tipoCombustivel") String tipoCombustivel,
                            @RequestParam("hodometroAtual") String hodometroAtual,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/login";
        }
        try {
            apiService.registrarAbastecimento(idMaquina, dataAbastecimento, litros, tipoCombustivel, hodometroAtual, token);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Abastecimento registrado com sucesso!");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro inesperado.");
        }
        return "redirect:/abastecimentos";
    }
}
