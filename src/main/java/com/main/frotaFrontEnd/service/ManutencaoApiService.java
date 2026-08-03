package com.main.frotaFrontEnd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ManutencaoApiService {
    private final RestClient restClient;

    @Autowired
    public ManutencaoApiService(RestClient restClient) {
        this.restClient = restClient;
    }

    @SuppressWarnings("unchecked")
    public java.util.List<java.util.Map<String, Object>> listarOrdens(String token) {
        return restClient.get()
                .uri("/manutencao/ordens")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(java.util.List.class);
    }

    @SuppressWarnings("unchecked")
    public java.util.Map<String, Object> abrirOrdem(Long idMaquina, String urgencia, String descricao, String token) {
        var body = new java.util.HashMap<String, Object>();
        body.put("urgencia", urgencia);
        body.put("descricao", descricao);
        return restClient.post()
                .uri("/manutencao/maquina/{id}/ordens", idMaquina)
                .header("Authorization", "Bearer " + token)
                .body(body)
                .retrieve()
                .body(java.util.Map.class);
    }

    @SuppressWarnings("unchecked")
    public java.util.Map<String, Object> aprovarOrdem(Long idOrdem, boolean aprovada, String token) {
        return restClient.post()
                .uri("/manutencao/ordens/{id}/aprovar", idOrdem)
                .header("Authorization", "Bearer " + token)
                .body(java.util.Map.of("aprovada", aprovada))
                .retrieve()
                .body(java.util.Map.class);
    }

    @SuppressWarnings("unchecked")
    public java.util.Map<String, Object> encerrarOrdem(Long idOrdem, String observacao, String token) {
        return restClient.post()
                .uri("/manutencao/ordens/{id}/encerrar", idOrdem)
                .header("Authorization", "Bearer " + token)
                .body(java.util.Map.of("observacao", observacao))
                .retrieve()
                .body(java.util.Map.class);
    }

    public String removerOrdem(Long idOrdem, String token) {
        return restClient.delete()
                .uri("/manutencao/ordens/{id}", idOrdem)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(String.class);
    }
}
