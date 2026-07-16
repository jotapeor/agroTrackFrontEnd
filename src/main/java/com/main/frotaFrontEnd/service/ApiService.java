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
}
