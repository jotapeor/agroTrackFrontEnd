package com.main.frotaFrontEnd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AbastecimentoApiService {
    private final RestClient restClient;

    @Autowired
    public AbastecimentoApiService(RestClient restClient) {
        this.restClient = restClient;
    }

    @SuppressWarnings("unchecked")
    public java.util.Map<String, Object> registrarAbastecimento(Long idMaquina, String dataAbastecimento, String litros, String tipoCombustivel, String hodometroAtual, String token) {
        var body = new java.util.HashMap<String, Object>();
        if (dataAbastecimento != null && !dataAbastecimento.isEmpty()) {
            body.put("dataAbastecimento", dataAbastecimento);
        }
        body.put("litros", new java.math.BigDecimal(litros));
        body.put("tipoCombustivel", tipoCombustivel);
        body.put("hodometroAtual", new java.math.BigDecimal(hodometroAtual));

        try {
            return restClient.post()
                    .uri("/abastecimentos/maquina/{id}", idMaquina)
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
            throw new RuntimeException("Erro ao registrar abastecimento: " + errorBody);
        }
    }
}
