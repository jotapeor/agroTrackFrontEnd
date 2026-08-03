package com.main.frotaFrontEnd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class OperacaoApiService {
    private final RestClient restClient;

    @Autowired
    public OperacaoApiService(RestClient restClient) {
        this.restClient = restClient;
    }

    @SuppressWarnings("unchecked")
    public java.util.Map<String, Object> trocarStatusMaquina(Long idMaquina, String novoStatus, boolean confirmacao, String pesoCarregado, String hodometroFim, String observacoes, String motivo, String pesoFinal, String token) {
        var body = new java.util.HashMap<String, Object>();
        body.put("novoStatus", novoStatus);
        body.put("confirmacao", confirmacao);
        if (pesoCarregado != null && !pesoCarregado.isEmpty()) {
            body.put("pesoCarregado", new java.math.BigDecimal(pesoCarregado));
        }
        if (hodometroFim != null && !hodometroFim.isEmpty()) {
            body.put("hodometroFim", new java.math.BigDecimal(hodometroFim));
        }
        if (observacoes != null && !observacoes.isEmpty()) {
            body.put("observacoes", observacoes);
        }
        if (motivo != null && !motivo.isEmpty()) {
            body.put("motivo", motivo);
        }
        if (pesoFinal != null && !pesoFinal.isEmpty()) {
            body.put("pesoFinal", new java.math.BigDecimal(pesoFinal));
        }

        try {
            return restClient.post()
                    .uri("/operacoes/maquina/{id}/status", idMaquina)
                    .header("Authorization", "Bearer " + token)
                    .body(body)
                    .retrieve()
                    .body(java.util.Map.class);
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            String errorBody = e.getResponseBodyAsString();
            if (errorBody != null && errorBody.contains("\"message\":\"")) {
                int start = errorBody.indexOf("\"message\":\"") + 11;
                int end = errorBody.indexOf("\"", start);
                if (end > start) throw new RuntimeException(errorBody.substring(start, end));
            }
            throw new RuntimeException("Erro ao trocar status: " + errorBody);
        }
    }

    @SuppressWarnings("unchecked")
    public java.util.Map<String, Object> obterOperacaoAtiva(Long idMaquina, String token) {
        return restClient.get()
                .uri("/operacoes/maquina/{id}/operacao-ativa", idMaquina)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(java.util.Map.class);
    }

    @SuppressWarnings("unchecked")
    public java.util.List<java.util.Map<String, Object>> listarHistoricoMaquina(Long idMaquina, String token) {
        return restClient.get()
                .uri("/operacoes/maquina/{id}/historico", idMaquina)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(java.util.List.class);
    }

    @SuppressWarnings("unchecked")
    public java.util.List<java.util.Map<String, Object>> obterHistoricoCompleto(Long idMaquina, String token) {
        return restClient.get()
                .uri("/proprietario/maquinas/{id}/historico-completo", idMaquina)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(java.util.List.class);
    }

    @SuppressWarnings("unchecked")
    public java.util.List<java.util.Map<String, Object>> listarTelemetriaEmOperacao(String token) {
        return restClient.get()
                .uri("/telemetria/em-operacao")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(java.util.List.class);
    }
}
