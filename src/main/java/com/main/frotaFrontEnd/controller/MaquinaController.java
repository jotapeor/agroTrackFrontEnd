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

import java.util.List;
import java.util.Map;

@Controller
public class MaquinaController {

    @Autowired
    private ApiService apiService;

    @GetMapping("/maquinas")
    public String listar(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/login";
        }
        try {
            List<Map<String, Object>> maquinas = apiService.listarMaquinas(token);
            model.addAttribute("maquinas", maquinas != null ? maquinas : List.of());
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("maquinas", List.of());
        }
        return "lista-maquinas";
    }

    @GetMapping("/maquinas/editar/{id}")
    public String editar(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!isProprietario(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/dashboard";
        }
        String token = (String) session.getAttribute("token");
        try {
            Map<String, Object> maquina = apiService.buscarMaquina(id, token);
            model.addAttribute("maquina", maquina);
            List<Map<String, Object>> fazendas = apiService.listarFazendas(token);
            model.addAttribute("fazendas", fazendas);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Máquina não encontrada.");
            return "redirect:/maquinas";
        }
        return "editar-maquina";
    }

    @PostMapping("/maquinas/editar/{id}")
    public String salvarEdicao(@PathVariable Long id,
                               @RequestParam("nome") String nome,
                               @RequestParam("tipo") String tipo,
                               @RequestParam("marca") String marca,
                               @RequestParam("modelo") String modelo,
                               @RequestParam("ano") int ano,
                               @RequestParam(value = "numero_serie", required = false) String numeroSerie,
                               @RequestParam(value = "placa", required = false) String placa,
                               @RequestParam("hodometro_inicial") String hodometroInicial,
                               @RequestParam(value = "capacidade_tanque", required = false) String capacidadeTanque,
                               @RequestParam(value = "tipo_combustivel", required = false) String tipoCombustivel,
                               @RequestParam(value = "intervalo_troca_oleo_horas", required = false) String intervaloTrocaOleo,
                               @RequestParam(value = "intervalo_inspecao_horas", required = false) String intervaloInspecao,
                               @RequestParam(value = "consumo_medio", required = false) String consumoMedio,
                               @RequestParam(value = "id_fazenda", required = false) String idFazenda,
                               @RequestParam(value = "id_talhao", required = false) String idTalhao,
                               @RequestParam(value = "status", required = false) String status,
                               @RequestParam(value = "nivel_risco", required = false) String nivelRisco,
                               @RequestParam(value = "data_aquisicao", required = false) String dataAquisicao,
                               @RequestParam(value = "valor_aquisicao", required = false) String valorAquisicao,
                               @RequestParam(value = "observacoes", required = false) String observacoes,
                               @RequestParam(value = "foto", required = false) MultipartFile foto,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (!isProprietario(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/dashboard";
        }
        String token = (String) session.getAttribute("token");
        try {
            apiService.atualizarMaquina(foto, id, nome, tipo, marca, modelo, ano,
                    numeroSerie, placa, hodometroInicial, capacidadeTanque, tipoCombustivel,
                    intervaloTrocaOleo, intervaloInspecao, consumoMedio, idFazenda, idTalhao,
                    status, nivelRisco, dataAquisicao, valorAquisicao, observacoes, token);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Máquina atualizada com sucesso!");
            return "redirect:/maquinas";
        } catch (HttpStatusCodeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar: " + ex.getResponseBodyAsString());
            return "redirect:/maquinas/editar/" + id;
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro inesperado: " + ex.getMessage());
            return "redirect:/maquinas/editar/" + id;
        }
    }

    @PostMapping("/maquinas/excluir/{id}")
    public String excluir(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isProprietario(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/dashboard";
        }
        String token = (String) session.getAttribute("token");
        try {
            apiService.excluirMaquina(id, token);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Máquina arquivada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao arquivar máquina.");
        }
        return "redirect:/maquinas";
    }

    @GetMapping("/maquinas/{id}/detalhes")
    public String detalhesMaquina(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }
        try {
            Map<String, Object> maquina = apiService.buscarMaquina(id, token);
            List<Map<String, Object>> historico = apiService.obterHistoricoCompleto(id, token);
            model.addAttribute("maquina", maquina);
            model.addAttribute("historico", historico);
            return "detalhes-maquina";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao carregar histórico: " + e.getMessage());
            return "redirect:/maquinas";
        }
    }

    @GetMapping("/nova-maquina")
    public String exibirFormulario(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!isProprietario(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/dashboard";
        }
        String token = (String) session.getAttribute("token");
        try {
            List<Map<String, Object>> fazendas = apiService.listarFazendas(token);
            model.addAttribute("fazendas", fazendas);
        } catch (Exception e) {
            model.addAttribute("fazendas", List.of());
        }
        return "nova-maquina";
    }

    @PostMapping("/nova-maquina")
    public String cadastrar(@RequestParam("nome") String nome,
                            @RequestParam("tipo") String tipo,
                            @RequestParam("marca") String marca,
                            @RequestParam("modelo") String modelo,
                            @RequestParam("ano") int ano,
                            @RequestParam(value = "numero_serie", required = false) String numeroSerie,
                            @RequestParam(value = "placa", required = false) String placa,
                            @RequestParam("hodometro_inicial") String hodometroInicial,
                            @RequestParam(value = "capacidade_tanque", required = false) String capacidadeTanque,
                            @RequestParam(value = "tipo_combustivel", required = false) String tipoCombustivel,
                            @RequestParam(value = "intervalo_troca_oleo_horas", required = false) String intervaloTrocaOleo,
                            @RequestParam(value = "intervalo_inspecao_horas", required = false) String intervaloInspecao,
                            @RequestParam(value = "consumo_medio", required = false) String consumoMedio,
                            @RequestParam(value = "id_fazenda", required = false) String idFazenda,
                            @RequestParam(value = "id_talhao", required = false) String idTalhao,
                            @RequestParam(value = "data_aquisicao", required = false) String dataAquisicao,
                            @RequestParam(value = "valor_aquisicao", required = false) String valorAquisicao,
                            @RequestParam(value = "observacoes", required = false) String observacoes,
                            @RequestParam(value = "foto", required = false) MultipartFile foto,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        if (!isProprietario(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/dashboard";
        }
        String token = (String) session.getAttribute("token");
        try {
            apiService.cadastrarMaquina(foto, nome, tipo, marca, modelo, ano,
                    numeroSerie, placa, hodometroInicial, capacidadeTanque, tipoCombustivel,
                    intervaloTrocaOleo, intervaloInspecao, consumoMedio, idFazenda, idTalhao,
                    dataAquisicao, valorAquisicao, observacoes, token);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Máquina cadastrada com sucesso!");
            return "redirect:/dashboard";
        } catch (HttpStatusCodeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao cadastrar máquina: " + ex.getResponseBodyAsString());
            return "redirect:/nova-maquina";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro inesperado: " + ex.getMessage());
            return "redirect:/nova-maquina";
        }
    }

    @GetMapping("/api/maquinas/talhoes")
    @ResponseBody
    public List<Map<String, Object>> listarTalhoes(@RequestParam("id_fazenda") Long idFazenda, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) return List.of();
        try {
            return apiService.listarTalhoes(String.valueOf(idFazenda), token);
        } catch (Exception e) {
            return List.of();
        }
    }

    @GetMapping("/maquinas/{id}/status")
    public String trocarStatusForm(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/login";
        }
        try {
            Map<String, Object> maquina = apiService.buscarMaquina(id, token);
            model.addAttribute("maquina", maquina);
            List<Map<String, Object>> historico = apiService.listarHistoricoMaquina(id, token);
            model.addAttribute("historico", historico);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Máquina não encontrada.");
            return "redirect:/maquinas";
        }
        return "trocar-status";
    }

    @PostMapping("/maquinas/{id}/status")
    public String salvarStatus(@PathVariable Long id,
                               @RequestParam("novoStatus") String novoStatus,
                               @RequestParam(value = "confirmacao", required = false) boolean confirmacao,
                               @RequestParam(value = "pesoCarregado", required = false) String pesoCarregado,
                               @RequestParam(value = "hodometroFim", required = false) String hodometroFim,
                               @RequestParam(value = "observacoes", required = false) String observacoes,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/login";
        }
        try {
            Map<String, Object> resumo = apiService.trocarStatusMaquina(id, novoStatus, confirmacao, pesoCarregado, hodometroFim, observacoes, token);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Status atualizado com sucesso!");
            if (resumo != null && !resumo.isEmpty()) {
                redirectAttributes.addFlashAttribute("resumoOperacao", resumo);
                return "redirect:/maquinas/" + id + "/status";
            }
            return "redirect:/maquinas";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/maquinas/" + id + "/status";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro inesperado: " + ex.getMessage());
            return "redirect:/maquinas/" + id + "/status";
        }
    }

    @PostMapping("/maquinas/{id}/autorizar-risco")
    public String autorizarRisco(@PathVariable Long id,
                                 @RequestParam("justificativa") String justificativa,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null || !"PROPRIETARIO".equals(session.getAttribute("role"))) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/login";
        }
        try {
            apiService.autorizarRisco(id, justificativa, token);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Operação temporária autorizada com sucesso!");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro inesperado.");
        }
        return "redirect:/maquinas/" + id + "/status";
    }

    @GetMapping("/maquinas/{id}/telemetria")
    public String telemetria(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        String role = (String) session.getAttribute("role");
        if (token == null || (!"PROPRIETARIO".equals(role) && !"SOCIO".equals(role))) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/maquinas";
        }
        try {
            Map<String, Object> maquina = apiService.buscarMaquina(id, token);
            model.addAttribute("maquina", maquina);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Máquina não encontrada.");
            return "redirect:/maquinas";
        }
        return "telemetria";
    }

    @GetMapping("/api/maquinas/{id}/telemetria/dados")
    @ResponseBody
    public Map<String, Object> telemetriaDados(@PathVariable Long id, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) return Map.of("error", "Não autenticado");
        try {
            return apiService.obterTelemetria(id, token);
        } catch (Exception e) {
            return Map.of("error", e.getMessage());
        }
    }

    private boolean isProprietario(HttpSession session) {
        return session.getAttribute("token") != null && "PROPRIETARIO".equals(session.getAttribute("role"));
    }
}
