package com.main.frotaFrontEnd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class RelatorioApiService {
    private final RestClient restClient;

    @Autowired
    public RelatorioApiService(RestClient restClient) {
        this.restClient = restClient;
    }

    @SuppressWarnings("unchecked")
    public java.util.Map<String, Object> obterDashboard(String token) {
        return restClient.get()
                .uri("/dashboard")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(java.util.Map.class);
    }

    @SuppressWarnings("unchecked")
    public java.util.Map<String, Object> obterTelemetria(Long idMaquina, String token) {
        return restClient.get()
                .uri("/telemetria/maquina/{idMaquina}", idMaquina)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(java.util.Map.class);
    }

    @SuppressWarnings("unchecked")
    public java.util.List<java.util.Map<String, Object>> relatorioConsumo(String inicio, String fim, String token) {
        String url = "/relatorios/consumo-por-maquina";
        if (inicio != null && !inicio.isEmpty() && fim != null && !fim.isEmpty()) {
            url += "?dataInicio=" + inicio + "&dataFim=" + fim;
        }
        return restClient.get()
                .uri(url)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(java.util.List.class);
    }

    @SuppressWarnings("unchecked")
    public java.util.Map<String, Object> relatorioRisco(String inicio, String fim, String token) {
        String url = "/relatorios/risco-distribuicao";
        if (inicio != null && !inicio.isEmpty() && fim != null && !fim.isEmpty()) {
            url += "?dataInicio=" + inicio + "&dataFim=" + fim;
        }
        return restClient.get()
                .uri(url)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(java.util.Map.class);
    }

    @SuppressWarnings("unchecked")
    public java.util.Map<String, Long> relatorioOrdensPorStatus(String inicio, String fim, String token) {
        String url = "/relatorios/ordens-por-status";
        if (inicio != null && !inicio.isEmpty() && fim != null && !fim.isEmpty()) {
            url += "?dataInicio=" + inicio + "&dataFim=" + fim;
        }
        return restClient.get()
                .uri(url)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(java.util.Map.class);
    }

    @SuppressWarnings("unchecked")
    public java.util.List<java.util.Map<String, Object>> relatorioHorasKm(String inicio, String fim, Long idMaquina, Long idOperador, String token) {
        StringBuilder url = new StringBuilder("/relatorios/horas-km?");
        if (inicio != null && !inicio.isEmpty()) url.append("dataInicio=").append(inicio).append("&");
        if (fim != null && !fim.isEmpty()) url.append("dataFim=").append(fim).append("&");
        if (idMaquina != null) url.append("idMaquina=").append(idMaquina).append("&");
        if (idOperador != null) url.append("idOperador=").append(idOperador).append("&");

        return restClient.get()
                .uri(url.toString())
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(java.util.List.class);
    }

    @SuppressWarnings("unchecked")
    public java.util.List<java.util.Map<String, Object>> relatorioAlertasTimeline(String inicio, String fim, String token) {
        String url = "/relatorios/alertas-timeline";
        if (inicio != null && !inicio.isEmpty() && fim != null && !fim.isEmpty()) {
            url += "?dataInicio=" + inicio + "&dataFim=" + fim;
        }
        return restClient.get()
                .uri(url)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(java.util.List.class);
    }
}
