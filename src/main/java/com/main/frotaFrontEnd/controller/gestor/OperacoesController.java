package com.main.frotaFrontEnd.controller.gestor;

import com.main.frotaFrontEnd.service.OperacaoApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@Controller
public class OperacoesController {

    private final OperacaoApiService operacaoApiService;

    public OperacoesController(OperacaoApiService operacaoApiService) {
        this.operacaoApiService = operacaoApiService;
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
            List<Map<String, Object>> operacoes = operacaoApiService.listarTelemetriaEmOperacao(token);
            model.addAttribute("operacoes", operacoes);
            return "gestor/operacoes";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erro ao carregar operações.");
            return "comum/dashboard";
        }
    }

    @GetMapping("/api/operacoes/data")
    @ResponseBody
    public ResponseEntity<?> pollingOperacoes(HttpSession session) {
        String token = (String) session.getAttribute("token");
        String role = (String) session.getAttribute("role");

        if (token == null || (!"PROPRIETARIO".equals(role) && !"SOCIO".equals(role))) {
            return ResponseEntity.status(403).build();
        }

        try {
            List<Map<String, Object>> operacoes = operacaoApiService.listarTelemetriaEmOperacao(token);
            return ResponseEntity.ok(operacoes);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
