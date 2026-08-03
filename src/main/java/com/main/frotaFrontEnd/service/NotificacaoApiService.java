package com.main.frotaFrontEnd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class NotificacaoApiService {
    private final RestClient restClient;

    @Autowired
    public NotificacaoApiService(RestClient restClient) {
        this.restClient = restClient;
    }

    @SuppressWarnings("unchecked")
    public java.util.List<java.util.Map<String, Object>> listarNotificacoes(String token) {
        return restClient.get()
                .uri("/notificacoes")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(java.util.List.class);
    }

    public void marcarNotificacaoLida(Long idNotificacao, String token) {
        restClient.post()
                .uri("/notificacoes/{id}/lida", idNotificacao)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toBodilessEntity();
    }

    public void removerNotificacao(Long idNotificacao, String token) {
        restClient.delete()
                .uri("/notificacoes/{id}", idNotificacao)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toBodilessEntity();
    }
}
