package com.main.frotaFrontEnd.service;

import com.main.frotaFrontEnd.model.UserDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
public class ApiService {
    private final RestClient restClient;

    public ApiService() {
        this.restClient = RestClient.builder().baseUrl("http://localhost:8080/api").build();
    }

    public String logar(String email, String senha) {
        return restClient.post()
                .uri("/autenticar/logar")
                .body(Map.of("email", email, "senha", senha))
                .retrieve()
                .body(String.class);
    }

    public String extrairRole(String token) {
        return extrairClaimString(token, "\"perfil\":\"");
    }

    public String extrairPrimeiroAcesso(String token) {
        String key = "\"primeiro_acesso\":";
        String json = decodificarPayload(token);
        if (json == null) return "true";
        int start = json.indexOf(key);
        if (start == -1) return "true";
        return json.substring(start + key.length()).startsWith("true") ? "true" : "false";
    }

    public String extrairNome(String token) {
        String nome = extrairClaimString(token, "\"nome\":\"");
        return nome != null ? nome : null;
    }

    private String extrairClaimString(String token, String key) {
        String json = decodificarPayload(token);
        if (json == null) return null;
        int start = json.indexOf(key);
        if (start == -1) return null;
        start += key.length();
        int end = json.indexOf("\"", start);
        return end == -1 ? null : json.substring(start, end);
    }

    private String decodificarPayload(String token) {
        try {
            String[] partes = token.split("\\.");
            String payload = partes[1];
            int padding = (4 - payload.length() % 4) % 4;
            return new String(Base64.getUrlDecoder().decode(payload + "=".repeat(padding)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    public void novoColaborador(UserDTO user, String token) {
        restClient.post()
                .uri("/proprietario/registrar-colaborador")
                .header("Authorization", "Bearer " + token)
                .body(user)
                .retrieve()
                .body(String.class);
    }

    public String alterarSenha(String novaSenha, String token) {
        return restClient.post()
                .uri("/autenticar/alterar-senha")
                .header("Authorization", "Bearer " + token)
                .body(Map.of("senha", novaSenha))
                .retrieve()
                .body(String.class);
    }

    public java.util.Map<String, Boolean> verificarEmail(String email) {
        return restClient.get()
                .uri("/autenticar/verificar-email?email={email}", email)
                .retrieve()
                .body(java.util.Map.class);
    }
}
