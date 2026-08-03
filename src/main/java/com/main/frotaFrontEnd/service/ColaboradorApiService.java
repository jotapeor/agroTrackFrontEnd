package com.main.frotaFrontEnd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ColaboradorApiService {
    private final RestClient restClient;

    @Autowired
    public ColaboradorApiService(RestClient restClient) {
        this.restClient = restClient;
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

    public String atualizarColaborador(Long id, String nome, String email,
            String perfil, String ativo,
            org.springframework.web.multipart.MultipartFile foto, String token) {
        var body = new org.springframework.util.LinkedMultiValueMap<String, Object>();
        body.add("nome", nome);
        body.add("email", email);
        body.add("perfil", perfil);
        body.add("ativo", ativo);
        if (foto != null && !foto.isEmpty()) body.add("foto", foto.getResource());
        return restClient.put()
                .uri("/proprietario/colaboradores/{id}", id)
                .header("Authorization", "Bearer " + token)
                .contentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(String.class);
    }

    public String excluirColaborador(Long id, String token) {
        return restClient.delete()
                .uri("/proprietario/colaboradores/{id}", id)
                .header("Authorization", "Bearer " + token)
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
    public java.util.Map<String, Object> verificarEmailDisponivel(String email, Long idAtual, String token) {
        String uri = "/usuarios/email-disponivel?email=" + email;
        if (idAtual != null) uri += "&idAtual=" + idAtual;
        return restClient.get()
                .uri(uri)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(java.util.Map.class);
    }
}
