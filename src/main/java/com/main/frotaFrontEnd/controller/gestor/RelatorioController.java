package com.main.frotaFrontEnd.controller.gestor;

import com.main.frotaFrontEnd.service.ColaboradorApiService;
import com.main.frotaFrontEnd.service.MaquinaApiService;
import com.main.frotaFrontEnd.service.RelatorioApiService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
public class RelatorioController {

    private static final Logger log = LoggerFactory.getLogger(RelatorioController.class);

    @Autowired
    private RelatorioApiService relatorioApiService;
    @Autowired
    private ColaboradorApiService colaboradorApiService;
    @Autowired
    private MaquinaApiService maquinaApiService;

    @GetMapping("/relatorios")
    public String relatorios(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        String role = (String) session.getAttribute("role");

        if (token == null || (!"PROPRIETARIO".equals(role) && !"SOCIO".equals(role))) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/dashboard";
        }

        try {
            List<Map<String, Object>> maquinas = maquinaApiService.listarMaquinas(token);
            model.addAttribute("maquinas", maquinas != null ? maquinas : List.of());

            List<Map<String, Object>> operadores = colaboradorApiService.listarColaboradores(token);
            model.addAttribute("operadores", operadores != null ? operadores : List.of());

        } catch (Exception e) {
            model.addAttribute("maquinas", List.of());
            model.addAttribute("operadores", List.of());
        }

        return "gestor/relatorios";
    }

    @GetMapping("/api/front/relatorios/consumo-por-maquina")
    @ResponseBody
    public List<Map<String, Object>> consumoPorMaquina(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) return List.of();
        try {
            return relatorioApiService.relatorioConsumo(dataInicio, dataFim, token);
        } catch (Exception e) {
            log.error("Erro ao buscar relatório consumo-por-maquina", e);
            return List.of();
        }
    }

    @GetMapping("/api/front/relatorios/risco-distribuicao")
    @ResponseBody
    public Map<String, Long> riscoDistribuicao(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) return Map.of();
        try {
            return relatorioApiService.relatorioRisco(dataInicio, dataFim, token);
        } catch (Exception e) {
            log.error("Erro ao buscar relatório risco-distribuicao", e);
            return Map.of();
        }
    }

    @GetMapping("/api/front/relatorios/ordens-por-status")
    @ResponseBody
    public Map<String, Long> ordensPorStatus(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) return Map.of();
        try {
            return relatorioApiService.relatorioOrdensPorStatus(dataInicio, dataFim, token);
        } catch (Exception e) {
            log.error("Erro ao buscar relatório ordens-por-status", e);
            return Map.of();
        }
    }

    @GetMapping("/api/front/relatorios/horas-km")
    @ResponseBody
    public List<Map<String, Object>> horasKm(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            @RequestParam(required = false) Long idMaquina,
            @RequestParam(required = false) Long idOperador,
            HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) return List.of();
        try {
            return relatorioApiService.relatorioHorasKm(dataInicio, dataFim, idMaquina, idOperador, token);
        } catch (Exception e) {
            log.error("Erro ao buscar relatório horas-km", e);
            return List.of();
        }
    }

    @GetMapping("/api/front/relatorios/alertas-timeline")
    @ResponseBody
    public List<Map<String, Object>> alertasTimeline(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) return List.of();
        try {
            return relatorioApiService.relatorioAlertasTimeline(dataInicio, dataFim, token);
        } catch (Exception e) {
            log.error("Erro ao buscar relatório alertas-timeline", e);
            return List.of();
        }
    }
}
