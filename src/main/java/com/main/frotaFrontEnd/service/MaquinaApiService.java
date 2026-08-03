package com.main.frotaFrontEnd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class MaquinaApiService {
    private final RestClient restClient;

    @Autowired
    public MaquinaApiService(RestClient restClient) {
        this.restClient = restClient;
    }

    public String cadastrarMaquina(org.springframework.web.multipart.MultipartFile foto,
                                    String nome, String tipo, String marca, String modelo, int ano,
                                    String numeroSerie, String placa, String hodometroInicial,
                                    String capacidadeTanque, String tipoCombustivel,
                                    java.util.List<String> combustivelExtra,
                                    String intervaloTrocaOleo, String intervaloInspecao,
                                    String consumoMedio, String idFazenda, String idTalhao,
                                    String dataAquisicao, String valorAquisicao,
                                    String observacoes, String token) {

        var body = new org.springframework.util.LinkedMultiValueMap<String, Object>();
        body.add("nome", nome);
        body.add("tipo", tipo);
        body.add("modelo", modelo);
        body.add("ano", ano);
        body.add("hodometro_inicial", hodometroInicial);
        if (marca != null && !marca.isEmpty()) body.add("marca", marca);
        if (numeroSerie != null && !numeroSerie.isEmpty()) body.add("numero_serie", numeroSerie);
        if (placa != null && !placa.isEmpty()) body.add("placa", placa);
        if (capacidadeTanque != null && !capacidadeTanque.isEmpty()) body.add("capacidade_tanque", capacidadeTanque);
        if (tipoCombustivel != null && !tipoCombustivel.isEmpty()) body.add("tipo_combustivel", tipoCombustivel);
        if (combustivelExtra != null) { for (String c : combustivelExtra) { if (c != null && !c.isEmpty()) body.add("combustivel_extra", c); } }
        if (intervaloTrocaOleo != null && !intervaloTrocaOleo.isEmpty()) body.add("intervalo_troca_oleo_horas", intervaloTrocaOleo);
        if (intervaloInspecao != null && !intervaloInspecao.isEmpty()) body.add("intervalo_inspecao_horas", intervaloInspecao);
        if (consumoMedio != null && !consumoMedio.isEmpty()) body.add("consumo_medio", consumoMedio);
        if (idFazenda != null && !idFazenda.isEmpty()) body.add("id_fazenda", idFazenda);
        if (idTalhao != null && !idTalhao.isEmpty()) body.add("id_talhao", idTalhao);
        if (dataAquisicao != null && !dataAquisicao.isEmpty()) body.add("data_aquisicao", dataAquisicao);
        if (valorAquisicao != null && !valorAquisicao.isEmpty()) body.add("valor_aquisicao", valorAquisicao);
        if (observacoes != null && !observacoes.isEmpty()) body.add("observacoes", observacoes);
        if (foto != null && !foto.isEmpty()) body.add("foto", foto.getResource());

        return restClient.post()
                .uri("/proprietario/maquinas")
                .header("Authorization", "Bearer " + token)
                .body(body)
                .retrieve()
                .body(String.class);
    }

    @SuppressWarnings("unchecked")
    public java.util.List<java.util.Map<String, Object>> listarFazendas(String token) {
        return restClient.get()
                .uri("/proprietario/maquinas/fazendas")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(java.util.List.class);
    }

    @SuppressWarnings("unchecked")
    public java.util.List<java.util.Map<String, Object>> listarTalhoes(String idFazenda, String token) {
        var uri = "/proprietario/maquinas/talhoes";
        if (idFazenda != null && !idFazenda.isEmpty())
            uri += "?id_fazenda=" + idFazenda;
        return restClient.get()
                .uri(uri)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(java.util.List.class);
    }

    @SuppressWarnings("unchecked")
    public java.util.List<java.util.Map<String, Object>> listarMaquinas(String token) {
        return restClient.get()
                .uri("/proprietario/maquinas")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(java.util.List.class);
    }

    @SuppressWarnings("unchecked")
    public java.util.Map<String, Object> buscarMaquina(Long id, String token) {
        return restClient.get()
                .uri("/proprietario/maquinas/{id}", id)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(java.util.Map.class);
    }

    public String atualizarMaquina(org.springframework.web.multipart.MultipartFile foto,
                                    Long id, String nome, String tipo, String marca, String modelo, int ano,
                                    String numeroSerie, String placa, String hodometroInicial,
                                    String capacidadeTanque, String tipoCombustivel,
                                    java.util.List<String> combustivelExtra,
                                    String intervaloTrocaOleo, String intervaloInspecao,
                                    String consumoMedio, String idFazenda, String idTalhao,
                                    String status, String nivelRisco,
                                    String dataAquisicao, String valorAquisicao,
                                    String observacoes, String token) {

        var body = new org.springframework.util.LinkedMultiValueMap<String, Object>();
        body.add("nome", nome);
        body.add("tipo", tipo);
        body.add("modelo", modelo);
        body.add("ano", ano);
        body.add("hodometro_inicial", hodometroInicial);
        if (marca != null && !marca.isEmpty()) body.add("marca", marca);
        if (numeroSerie != null && !numeroSerie.isEmpty()) body.add("numero_serie", numeroSerie);
        if (placa != null && !placa.isEmpty()) body.add("placa", placa);
        if (capacidadeTanque != null && !capacidadeTanque.isEmpty()) body.add("capacidade_tanque", capacidadeTanque);
        if (tipoCombustivel != null && !tipoCombustivel.isEmpty()) body.add("tipo_combustivel", tipoCombustivel);
        if (combustivelExtra != null) { for (String c : combustivelExtra) { if (c != null && !c.isEmpty()) body.add("combustivel_extra", c); } }
        if (intervaloTrocaOleo != null && !intervaloTrocaOleo.isEmpty()) body.add("intervalo_troca_oleo_horas", intervaloTrocaOleo);
        if (intervaloInspecao != null && !intervaloInspecao.isEmpty()) body.add("intervalo_inspecao_horas", intervaloInspecao);
        if (consumoMedio != null && !consumoMedio.isEmpty()) body.add("consumo_medio", consumoMedio);
        if (idFazenda != null && !idFazenda.isEmpty()) body.add("id_fazenda", idFazenda);
        if (idTalhao != null && !idTalhao.isEmpty()) body.add("id_talhao", idTalhao);
        if (status != null && !status.isEmpty()) body.add("status", status);
        if (nivelRisco != null && !nivelRisco.isEmpty()) body.add("nivel_risco", nivelRisco);
        if (dataAquisicao != null && !dataAquisicao.isEmpty()) body.add("data_aquisicao", dataAquisicao);
        if (valorAquisicao != null && !valorAquisicao.isEmpty()) body.add("valor_aquisicao", valorAquisicao);
        if (observacoes != null && !observacoes.isEmpty()) body.add("observacoes", observacoes);
        if (foto != null && !foto.isEmpty()) body.add("foto", foto.getResource());

        return restClient.post()
                .uri("/proprietario/maquinas/{id}", id)
                .header("Authorization", "Bearer " + token)
                .body(body)
                .retrieve()
                .body(String.class);
    }

    public String excluirMaquina(Long id, String token) {
        return restClient.delete()
                .uri("/proprietario/maquinas/{id}", id)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(String.class);
    }

    @SuppressWarnings("unchecked")
    public java.util.List<java.util.Map<String, Object>> listarMaquinasArquivadas(String token) {
        return restClient.get()
                .uri("/proprietario/maquinas/arquivadas")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(java.util.List.class);
    }

    public String reativarMaquina(Long id, String token) {
        return restClient.post()
                .uri("/proprietario/maquinas/{id}/reativar", id)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(String.class);
    }

    @SuppressWarnings("unchecked")
    public java.util.List<String> listarCombustiveisMaquina(Long idMaquina, String token) {
        return restClient.get()
                .uri("/proprietario/maquinas/{id}/combustiveis", idMaquina)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(java.util.List.class);
    }

    @SuppressWarnings("unchecked")
    public java.util.Map<String, Object> autorizarRisco(Long idMaquina, String justificativa, String token) {
        var body = new java.util.HashMap<String, Object>();
        body.put("justificativa", justificativa);

        try {
            return restClient.post()
                    .uri("/proprietario/maquinas/{id}/autorizar-risco", idMaquina)
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
            throw new RuntimeException("Erro ao autorizar risco: " + errorBody);
        }
    }
}
