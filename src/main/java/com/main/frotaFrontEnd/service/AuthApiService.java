package com.main.frotaFrontEnd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
public class AuthApiService {
    private final RestClient restClient;

    @Autowired
    public AuthApiService(RestClient restClient) {
        this.restClient = restClient;
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

    public Long extrairUserId(String token) {
        try {
            String json = decodificarPayload(token);
            if (json == null) return null;
            int start = json.indexOf("\"id_usuario\":");
            if (start == -1) return null;
            start += "\"id_usuario\":".length();
            int end = json.indexOf(",", start);
            if (end == -1) end = json.indexOf("}", start);
            return Long.parseLong(json.substring(start, end).trim());
        } catch (Exception e) {
            return null;
        }
    }

    public String alterarSenha(String novaSenha, String token) {
        return restClient.post()
                .uri("/autenticar/alterar-senha")
                .header("Authorization", "Bearer " + token)
                .body(Map.of("senha", novaSenha))
                .retrieve()
                .body(String.class);
    }

    @SuppressWarnings("unchecked")
    public java.util.Map<String, Boolean> verificarEmail(String email) {
        return restClient.get()
                .uri("/autenticar/verificar-email?email={email}", email)
                .retrieve()
                .body(java.util.Map.class);
    }

    @SuppressWarnings("unchecked")
    public java.util.Map<String, Object> buscarMeusDados(String token) {
        return restClient.get()
                .uri("/usuario/me")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(java.util.Map.class);
    }

    @SuppressWarnings("unchecked")
    public java.util.Map<String, Object> atualizarMeusDados(String nome, String email,
                                                             org.springframework.web.multipart.MultipartFile foto,
                                                             String token) {
        var body = new org.springframework.util.LinkedMultiValueMap<String, Object>();
        if (nome != null && !nome.isEmpty()) body.add("nome", nome);
        if (email != null && !email.isEmpty()) body.add("email", email);
        if (foto != null && !foto.isEmpty()) body.add("foto", foto.getResource());

        return restClient.put()
                .uri("/usuario/me")
                .header("Authorization", "Bearer " + token)
                .body(body)
                .retrieve()
                .body(java.util.Map.class);
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
}
