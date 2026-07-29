package com.main.frotaFrontEnd.controller;

import com.main.frotaFrontEnd.service.ApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
public class ColaboradorController {

    @Autowired
    private ApiService apiService;

    @GetMapping("/colaboradores")
    public String listar(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!isProprietarioOuSocio(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/dashboard";
        }
        String token = (String) session.getAttribute("token");
        Long meuId = (Long) session.getAttribute("userId");
        try {
            List<Map<String, Object>> colaboradores = apiService.listarColaboradores(token);
            if (meuId != null && colaboradores != null) {
                colaboradores = colaboradores.stream()
                        .filter(c -> {
                            Object idObj = c.get("id_usuario");
                            if (idObj == null) return true;
                            return !meuId.equals(((Number) idObj).longValue());
                        })
                        .toList();
            }
            model.addAttribute("colaboradores", colaboradores != null ? colaboradores : List.of());
            model.addAttribute("roleLogado", session.getAttribute("role"));
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("colaboradores", List.of());
            model.addAttribute("roleLogado", session.getAttribute("role"));
        }
        return "lista-colaboradores";
    }

    @GetMapping("/colaboradores/editar/{id}")
    public String editar(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!isProprietarioOuSocio(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/dashboard";
        }
        Long meuId = (Long) session.getAttribute("userId");
        if (meuId != null && meuId.equals(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você não pode editar seu próprio perfil aqui.");
            return "redirect:/colaboradores";
        }
        String token = (String) session.getAttribute("token");
        String role = (String) session.getAttribute("role");
        try {
            Map<String, Object> colaborador = apiService.buscarColaborador(id, token);

            if ("SOCIO".equals(role) && !"OPERADOR".equals(colaborador.get("perfil"))) {
                redirectAttributes.addFlashAttribute("errorMessage", "Você só pode editar contas de operador.");
                return "redirect:/colaboradores";
            }

            model.addAttribute("colaborador", colaborador);
            model.addAttribute("roleLogado", role);

            List<Map<String, Object>> maquinas = apiService.listarMaquinas(token);
            if (maquinas != null) {
                for (Map<String, Object> m : maquinas) {
                    Object idObj = m.get("id");
                    if (idObj instanceof Number && !(idObj instanceof Long)) {
                        m.put("id", ((Number) idObj).longValue());
                    }
                }
            }
            model.addAttribute("maquinas", maquinas != null ? maquinas : List.of());

            List<?> rawIds = apiService.listarMaquinasVinculadas(id, token);
            Set<Long> idsVinculados = new HashSet<>();
            if (rawIds != null) {
                for (Object x : rawIds) {
                    if (x instanceof Number) idsVinculados.add(((Number) x).longValue());
                }
            }
            model.addAttribute("idsVinculados", idsVinculados);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Colaborador não encontrado.");
            return "redirect:/colaboradores";
        }
        return "editar-colaborador";
    }

    @PostMapping("/colaboradores/editar/{id}")
    public String salvarEdicao(@PathVariable Long id,
                               @RequestParam("nome") String nome,
                               @RequestParam("email") String email,
                               @RequestParam("perfil") String perfil,
                               @RequestParam(value = "ativo", defaultValue = "true") String ativo,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (!isProprietarioOuSocio(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/dashboard";
        }
        Long meuId = (Long) session.getAttribute("userId");
        if (meuId != null && meuId.equals(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você não pode alterar seu próprio perfil.");
            return "redirect:/colaboradores";
        }
        String token = (String) session.getAttribute("token");
        String role = (String) session.getAttribute("role");

        if ("SOCIO".equals(role)) {
            perfil = "OPERADOR";
        }

        try {
            apiService.atualizarColaborador(id, Map.of("nome", nome, "email", email, "perfil", perfil, "ativo", ativo), token);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Colaborador atualizado com sucesso!");
            return "redirect:/colaboradores";
        } catch (HttpStatusCodeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar: " + ex.getResponseBodyAsString());
            return "redirect:/colaboradores/editar/" + id;
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro inesperado: " + ex.getMessage());
            return "redirect:/colaboradores/editar/" + id;
        }
    }

    @PostMapping("/colaboradores/vincular/{id}")
    public String vincularMaquinas(@PathVariable Long id,
                                   @RequestParam(value = "maquinas", required = false) List<Long> idsMaquinas,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        if (!isProprietarioOuSocio(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/dashboard";
        }
        String token = (String) session.getAttribute("token");
        String role = (String) session.getAttribute("role");

        if ("SOCIO".equals(role)) {
            try {
                Map<String, Object> colaborador = apiService.buscarColaborador(id, token);
                if (!"OPERADOR".equals(colaborador.get("perfil"))) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Você só pode vincular máquinas a operadores.");
                    return "redirect:/colaboradores/editar/" + id;
                }
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Colaborador não encontrado.");
                return "redirect:/colaboradores/editar/" + id;
            }
        }

        try {
            if (idsMaquinas == null) idsMaquinas = List.of();
            apiService.vincularMaquinas(id, idsMaquinas, token);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Vínculos atualizados com sucesso!");
        } catch (HttpStatusCodeException ex) {
            String msg = extrairMensagemErro(ex.getResponseBodyAsString());
            redirectAttributes.addFlashAttribute("errorMessage", msg != null ? msg : "Erro ao vincular máquinas (HTTP " + ex.getStatusCode().value() + ").");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao vincular máquinas: " + e.getMessage());
        }
        return "redirect:/colaboradores/editar/" + id;
    }

    @PostMapping("/colaboradores/excluir/{id}")
    public String excluir(@PathVariable Long id,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        if (!isProprietario(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/dashboard";
        }
        String token = (String) session.getAttribute("token");
        try {
            apiService.excluirColaborador(id, token);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Colaborador excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao excluir colaborador.");
        }
        return "redirect:/colaboradores";
    }

    private String extrairMensagemErro(String body) {
        if (body == null || body.isEmpty()) return null;
        int start = body.indexOf("\"message\":\"");
        if (start == -1) return null;
        start += 11;
        int end = body.indexOf("\"", start);
        return end > start ? body.substring(start, end) : null;
    }

    private boolean isProprietario(HttpSession session) {
        return session.getAttribute("token") != null && "PROPRIETARIO".equals(session.getAttribute("role"));
    }

    private boolean isProprietarioOuSocio(HttpSession session) {
        String role = (String) session.getAttribute("role");
        return session.getAttribute("token") != null && ("PROPRIETARIO".equals(role) || "SOCIO".equals(role));
    }
}
