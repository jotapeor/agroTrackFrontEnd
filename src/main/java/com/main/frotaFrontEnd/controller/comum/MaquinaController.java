package com.main.frotaFrontEnd.controller.comum;

import com.main.frotaFrontEnd.service.MaquinaApiService;
import com.main.frotaFrontEnd.service.OperacaoApiService;
import com.main.frotaFrontEnd.service.RelatorioApiService;
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
    private MaquinaApiService maquinaApiService;
    @Autowired
    private OperacaoApiService operacaoApiService;
    @Autowired
    private RelatorioApiService relatorioApiService;

    @GetMapping("/maquinas")
    public String listar(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/login";
        }
        try {
            List<Map<String, Object>> maquinas = maquinaApiService.listarMaquinas(token);
            model.addAttribute("maquinas", maquinas != null ? maquinas : List.of());
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("maquinas", List.of());
        }
        return "comum/lista-maquinas";
    }

    @GetMapping("/maquinas/editar/{id}")
    public String editar(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!isProprietario(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/dashboard";
        }
        String token = (String) session.getAttribute("token");
        try {
            Map<String, Object> maquina = maquinaApiService.buscarMaquina(id, token);
            model.addAttribute("maquina", maquina);
            List<Map<String, Object>> fazendas = maquinaApiService.listarFazendas(token);
            model.addAttribute("fazendas", fazendas);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Máquina não encontrada.");
            return "redirect:/maquinas";
        }
        return "proprietario/editar-maquina";
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
                               @RequestParam(value = "combustivel_extra", required = false) List<String> combustivelExtra,
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
            maquinaApiService.atualizarMaquina(foto, id, nome, tipo, marca, modelo, ano,
                    numeroSerie, placa, hodometroInicial, capacidadeTanque, tipoCombustivel,
                    combustivelExtra, intervaloTrocaOleo, intervaloInspecao, consumoMedio,
                    idFazenda, idTalhao, status, nivelRisco, dataAquisicao, valorAquisicao,
                    observacoes, token);
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

    @GetMapping("/maquinas/arquivadas")
    public String listarArquivadas(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!isProprietario(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/dashboard";
        }
        String token = (String) session.getAttribute("token");
        try {
            List<Map<String, Object>> maquinas = maquinaApiService.listarMaquinasArquivadas(token);
            model.addAttribute("maquinas", maquinas != null ? maquinas : List.of());
        } catch (Exception e) {
            model.addAttribute("maquinas", List.of());
        }
        return "proprietario/lista-maquinas-arquivadas";
    }

    @PostMapping("/maquinas/{id}/reativar")
    public String reativar(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isProprietario(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/dashboard";
        }
        String token = (String) session.getAttribute("token");
        try {
            maquinaApiService.reativarMaquina(id, token);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Máquina reativada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao reativar máquina.");
        }
        return "redirect:/maquinas/arquivadas";
    }

    @PostMapping("/maquinas/excluir/{id}")
    public String excluir(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isProprietario(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/dashboard";
        }
        String token = (String) session.getAttribute("token");
        try {
            maquinaApiService.excluirMaquina(id, token);
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
            Map<String, Object> maquina = maquinaApiService.buscarMaquina(id, token);
            List<Map<String, Object>> historico = operacaoApiService.obterHistoricoCompleto(id, token);
            model.addAttribute("maquina", maquina);
            model.addAttribute("historico", historico);
            return "comum/detalhes-maquina";
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
            List<Map<String, Object>> fazendas = maquinaApiService.listarFazendas(token);
            model.addAttribute("fazendas", fazendas);
        } catch (Exception e) {
            model.addAttribute("fazendas", List.of());
        }
        return "proprietario/nova-maquina";
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
                            @RequestParam(value = "combustivel_extra", required = false) List<String> combustivelExtra,
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
            maquinaApiService.cadastrarMaquina(foto, nome, tipo, marca, modelo, ano,
                    numeroSerie, placa, hodometroInicial, capacidadeTanque, tipoCombustivel,
                    combustivelExtra, intervaloTrocaOleo, intervaloInspecao, consumoMedio,
                    idFazenda, idTalhao, dataAquisicao, valorAquisicao, observacoes, token);
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

    @GetMapping("/api/maquinas/{id}/combustiveis")
    @ResponseBody
    public List<String> listarCombustiveis(@PathVariable Long id, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) return List.of();
        try {
            return maquinaApiService.listarCombustiveisMaquina(id, token);
        } catch (Exception e) {
            return List.of();
        }
    }

    @GetMapping("/api/maquinas/talhoes")
    @ResponseBody
    public List<Map<String, Object>> listarTalhoes(@RequestParam("id_fazenda") Long idFazenda, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) return List.of();
        try {
            return maquinaApiService.listarTalhoes(String.valueOf(idFazenda), token);
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
            Map<String, Object> maquina = maquinaApiService.buscarMaquina(id, token);
            model.addAttribute("maquina", maquina);
            List<Map<String, Object>> historico = operacaoApiService.listarHistoricoMaquina(id, token);
            model.addAttribute("historico", historico);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Máquina não encontrada.");
            return "redirect:/maquinas";
        }
        return "comum/trocar-status";
    }

    @PostMapping("/maquinas/{id}/status")
    public String salvarStatus(@PathVariable Long id,
                               @RequestParam("novoStatus") String novoStatus,
                               @RequestParam(value = "confirmacao", required = false) boolean confirmacao,
                               @RequestParam(value = "pesoCarregado", required = false) String pesoCarregado,
                               @RequestParam(value = "hodometroFim", required = false) String hodometroFim,
                               @RequestParam(value = "observacoes", required = false) String observacoes,
                               @RequestParam(value = "motivo", required = false) String motivo,
                               @RequestParam(value = "pesoFinal", required = false) String pesoFinal,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/login";
        }
        try {
            Map<String, Object> resumo = operacaoApiService.trocarStatusMaquina(id, novoStatus, confirmacao, pesoCarregado, hodometroFim, observacoes, motivo, pesoFinal, token);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Status atualizado com sucesso!");
            if (resumo != null && resumo.containsKey("nomeOperador")) {
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

    @GetMapping(value = "/maquinas/{id}/operacao-ativa", produces = "application/json")
    @ResponseBody
    public Map<String, Object> operacaoAtivaJson(@PathVariable Long id, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) return Map.of("error", "unauthorized");
        try {
            Map<String, Object> result = operacaoApiService.obterOperacaoAtiva(id, token);
            return result != null ? result : Map.of("error", "not_found");
        } catch (Exception e) {
            return Map.of("error", e.getMessage() != null ? e.getMessage() : "error");
        }
    }

    @GetMapping("/maquinas/{id}/historico-operacoes")
    public String historicoOperacoes(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";
        try {
            Map<String, Object> maquina = maquinaApiService.buscarMaquina(id, token);
            model.addAttribute("maquina", maquina);
            List<Map<String, Object>> historico = operacaoApiService.listarHistoricoMaquina(id, token);
            List<Map<String, Object>> operacoes = historico.stream()
                .filter(h -> "Operacao".equals(h.get("tipo")))
                .collect(java.util.stream.Collectors.toList());
            model.addAttribute("operacoes", operacoes);
            List<Map<String, Object>> mudancas = historico.stream()
                .filter(h -> "MudancaStatus".equals(h.get("tipo")))
                .collect(java.util.stream.Collectors.toList());
            model.addAttribute("mudancasStatus", mudancas);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao carregar histórico.");
            return "redirect:/maquinas";
        }
        return "comum/historico-operacoes";
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
            maquinaApiService.autorizarRisco(id, justificativa, token);
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
            Map<String, Object> maquina = maquinaApiService.buscarMaquina(id, token);
            model.addAttribute("maquina", maquina);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Máquina não encontrada.");
            return "redirect:/maquinas";
        }
        return "comum/telemetria";
    }

    @GetMapping("/api/maquinas/{id}/operacao-ativa")
    @ResponseBody
    public Map<String, Object> obterOperacaoAtivaFrontend(@PathVariable Long id, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) return Map.of("error", "Não autenticado");
        try {
            return operacaoApiService.obterOperacaoAtiva(id, token);
        } catch (Exception e) {
            return Map.of("error", e.getMessage());
        }
    }

    @GetMapping("/api/maquinas/{id}/telemetria/dados")
    @ResponseBody
    public Map<String, Object> telemetriaDados(@PathVariable Long id, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) return Map.of("error", "Não autenticado");
        try {
            return relatorioApiService.obterTelemetria(id, token);
        } catch (Exception e) {
            return Map.of("error", e.getMessage());
        }
    }

    private boolean isProprietario(HttpSession session) {
        return session.getAttribute("token") != null && "PROPRIETARIO".equals(session.getAttribute("role"));
    }
}
