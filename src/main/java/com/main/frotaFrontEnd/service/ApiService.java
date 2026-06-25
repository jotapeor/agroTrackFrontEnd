package com.main.frotaFrontEnd.service;

import com.main.frotaFrontEnd.model.UserRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class ApiService {
    private final RestClient restClient;

    public ApiService() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8080/api")
                .build();
    }

    public String logar(UserRequestDTO user) {
        return restClient.post()
                .uri("/autenticar/logar")
                .body(user)
                .retrieve()
                .body(String.class);
    }

    public String extrairRole(String token) {
        try {
            String[] partes = token.split("\\.");
            String payload = partes[1];
            int padding = (4 - payload.length() % 4) % 4;
            payload = payload + "=".repeat(padding);

            String json = new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);

            String roleKey = "\"role\":\"";
            int start = json.indexOf(roleKey);
            if (start == -1) return null;

            start += roleKey.length();
            int end = json.indexOf("\"", start);

            return json.substring(start, end);
        } catch (Exception e) {
            return null;
        }
    }

    public String extrairNome(String token) {
        try {
            String[] partes = token.split("\\.");
            String payload = partes[1];
            int padding = (4 - payload.length() % 4) % 4;
            payload = payload + "=".repeat(padding);
            String json = new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);

            for (String key : new String[]{"\"nome\":\"", "\"name\":\""}) {
                int start = json.indexOf(key);
                if (start != -1) {
                    start += key.length();
                    int end = json.indexOf("\"", start);
                    return json.substring(start, end);
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}