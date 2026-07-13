package com.main.frotaFrontEnd.controller;

import com.main.frotaFrontEnd.service.ApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
public class ColaboradorController {

    @Autowired
    private ApiService apiService;

    @GetMapping("/colaboradores")
    public String listar(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!isProprietario(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/dashboard";
        }
        String token = (String) session.getAttribute("token");
        Long meuId = (Long) session.getAttribute("userId");
        try {
            List<Map<String, Object>> colaboradores = apiService.listarColaboradores(token);
            if (meuId != null && colaboradores != null) {
                colaboradores = colaboradores.stream()
                        .filter(c -> !meuId.equals(c.get("id_usuario")))
                        .toList();
            }
            model.addAttribute("colaboradores", colaboradores != null ? colaboradores : List.of());
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("colaboradores", List.of());
        }
        return "lista-colaboradores";
    }

    @GetMapping("/colaboradores/editar/{id}")
    public String editar(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!isProprietario(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/dashboard";
        }
        Long meuId = (Long) session.getAttribute("userId");
        if (meuId != null && meuId.equals(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você não pode editar seu próprio perfil aqui.");
            return "redirect:/colaboradores";
        }
        String token = (String) session.getAttribute("token");
        try {
            Map<String, Object> colaborador = apiService.buscarColaborador(id, token);
            model.addAttribute("colaborador", colaborador);

            List<Map<String, Object>> maquinas = apiService.listarMaquinas(token);
            model.addAttribute("maquinas", maquinas);

            List<Long> idsVinculados = apiService.listarMaquinasVinculadas(id, token);
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
        if (!isProprietario(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/dashboard";
        }
        Long meuId = (Long) session.getAttribute("userId");
        if (meuId != null && meuId.equals(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você não pode alterar seu próprio perfil.");
            return "redirect:/colaboradores";
        }
        String token = (String) session.getAttribute("token");
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
        if (!isProprietario(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado.");
            return "redirect:/dashboard";
        }
        String token = (String) session.getAttribute("token");
        try {
            if (idsMaquinas == null) idsMaquinas = List.of();
            apiService.vincularMaquinas(id, idsMaquinas, token);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Vínculos atualizados com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao vincular máquinas.");
        }
        return "redirect:/colaboradores/editar/" + id;
    }

    private boolean isProprietario(HttpSession session) {
        return session.getAttribute("token") != null && "PROPRIETARIO".equals(session.getAttribute("role"));
    }
}
