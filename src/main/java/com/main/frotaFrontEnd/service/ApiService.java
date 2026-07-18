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

    public String novoColaborador(String nome, String email, String senha, String perfil,
                                   org.springframework.web.multipart.MultipartFile foto, String token) {
        var body = new org.springframework.util.LinkedMultiValueMap<String, Object>();
        body.add("nome", nome);
        body.add("email", email);
        body.add("senha", senha);
        body.add("perfil", perfil);
        if (foto != null && !foto.isEmpty()) body.add("foto", foto.getResource());
        return restClient.post()
                .uri("/proprietario/registrar-colaborador")
                .header("Authorization", "Bearer " + token)
                .body(body)
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

    public String cadastrarMaquina(org.springframework.web.multipart.MultipartFile foto,
                                    String nome, String tipo, String marca, String modelo, int ano,
                                    String numeroSerie, String placa, String hodometroInicial,
                                    String capacidadeTanque, String tipoCombustivel,
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
    public java.util.List<java.util.Map<String, Object>> listarColaboradores(String token) {
        return restClient.get()
                .uri("/proprietario/colaboradores")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(java.util.List.class);
    }

    @SuppressWarnings("unchecked")
    public java.util.Map<String, Object> buscarColaborador(Long id, String token) {
        return restClient.get()
                .uri("/proprietario/colaboradores/{id}", id)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(java.util.Map.class);
    }

    public String atualizarColaborador(Long id, Map<String, String> dados, String token) {
        return restClient.put()
                .uri("/proprietario/colaboradores/{id}", id)
                .header("Authorization", "Bearer " + token)
                .body(dados)
                .retrieve()
                .body(String.class);
    }

    public String vincularMaquinas(Long idColaborador, java.util.List<Long> idsMaquinas, String token) {
        return restClient.put()
                .uri("/proprietario/colaboradores/{id}/vincular-maquinas", idColaborador)
                .header("Authorization", "Bearer " + token)
                .body(idsMaquinas)
                .retrieve()
                .body(String.class);
    }

    @SuppressWarnings("unchecked")
    public java.util.List<Long> listarMaquinasVinculadas(Long idColaborador, String token) {
        return restClient.get()
                .uri("/proprietario/colaboradores/{id}/maquinas", idColaborador)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(java.util.List.class);
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

    public String excluirColaborador(Long id, String token) {
        return restClient.delete()
                .uri("/proprietario/colaboradores/{id}", id)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(String.class);
    }

    @SuppressWarnings("unchecked")
    public java.util.Map<String, Object> trocarStatusMaquina(Long idMaquina, String novoStatus, boolean confirmacao, String pesoCarregado, String hodometroFim, String observacoes, String token) {
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

        try {
            return restClient.post()
                    .uri("/operacoes/maquina/{id}/status", idMaquina)
                    .header("Authorization", "Bearer " + token)
                    .body(body)
                    .retrieve()
                    .body(java.util.Map.class);
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            try {
                String errorBody = e.getResponseBodyAsString();
                if (errorBody.contains("\"message\"")) {
                    int start = errorBody.indexOf("\"message\":\"") + 11;
                    int end = errorBody.indexOf("\"", start);
                    throw new RuntimeException(errorBody.substring(start, end));
                }
                throw new RuntimeException("Erro ao trocar status: " + errorBody);
            } catch (Exception ex) {
                throw new RuntimeException("Erro ao trocar status: " + e.getResponseBodyAsString());
            }
        }
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

    @SuppressWarnings("unchecked")
    public java.util.Map<String, Object> obterDashboard(String token) {
        return restClient.get()
                .uri("/dashboard")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(java.util.Map.class);
    }

    @SuppressWarnings("unchecked")
    public java.util.List<java.util.Map<String, Object>> listarNotificacoes(String token) {
        return restClient.get()
                .uri("/notificacoes")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(java.util.List.class);
    }

    @SuppressWarnings("unchecked")
    public void marcarNotificacaoLida(Long idNotificacao, String token) {
        restClient.post()
                .uri("/notificacoes/{id}/lida", idNotificacao)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toBodilessEntity();
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
            try {
                String errorBody = e.getResponseBodyAsString();
                if (errorBody.contains("\"message\"")) {
                    int start = errorBody.indexOf("\"message\":\"") + 11;
                    int end = errorBody.indexOf("\"", start);
                    throw new RuntimeException(errorBody.substring(start, end));
                }
                throw new RuntimeException("Erro ao registrar abastecimento: " + errorBody);
            } catch (Exception ex) {
                throw new RuntimeException("Erro ao registrar abastecimento: " + e.getResponseBodyAsString());
            }
        }
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
            try {
                String errorBody = e.getResponseBodyAsString();
                if (errorBody.contains("\"message\"")) {
                    int start = errorBody.indexOf("\"message\":\"") + 11;
                    int end = errorBody.indexOf("\"", start);
                    throw new RuntimeException(errorBody.substring(start, end));
                }
                throw new RuntimeException("Erro ao autorizar risco: " + errorBody);
            } catch (Exception ex) {
                throw new RuntimeException("Erro ao autorizar risco: " + e.getResponseBodyAsString());
            }
        }
    }
}
