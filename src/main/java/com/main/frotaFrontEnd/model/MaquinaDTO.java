package com.main.frotaFrontEnd.model;

import java.math.BigDecimal;

public class MaquinaDTO {
    private Long id_maquina;
    private Long id_fazenda;
    private String nomeFazenda;
    private Long id_talhao;
    private String nomeTalhao;
    private String nome;
    private String tipo;
    private String marca;
    private String modelo;
    private int ano;
    private String numero_serie;
    private String placa;
    private BigDecimal hodometro_inicial;
    private BigDecimal capacidade_tanque;
    private String tipo_combustivel;
    private Integer intervalo_troca_oleo_horas;
    private Integer intervalo_inspecao_horas;
    private BigDecimal consumo_medio;
    private String status;
    private String nivel_risco;
    private String data_aquisicao;
    private BigDecimal valor_aquisicao;
    private String foto_path;
    private String observacoes;

    public MaquinaDTO() {
    }

    public Long getId_maquina() {
        return id_maquina;
    }

    public void setId_maquina(Long id_maquina) {
        this.id_maquina = id_maquina;
    }

    public Long getId_fazenda() {
        return id_fazenda;
    }

    public void setId_fazenda(Long id_fazenda) {
        this.id_fazenda = id_fazenda;
    }

    public String getNomeFazenda() {
        return nomeFazenda;
    }

    public void setNomeFazenda(String nomeFazenda) {
        this.nomeFazenda = nomeFazenda;
    }

    public Long getId_talhao() {
        return id_talhao;
    }

    public void setId_talhao(Long id_talhao) {
        this.id_talhao = id_talhao;
    }

    public String getNomeTalhao() {
        return nomeTalhao;
    }

    public void setNomeTalhao(String nomeTalhao) {
        this.nomeTalhao = nomeTalhao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public String getNumero_serie() {
        return numero_serie;
    }

    public void setNumero_serie(String numero_serie) {
        this.numero_serie = numero_serie;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public BigDecimal getHodometro_inicial() {
        return hodometro_inicial;
    }

    public void setHodometro_inicial(BigDecimal hodometro_inicial) {
        this.hodometro_inicial = hodometro_inicial;
    }

    public BigDecimal getCapacidade_tanque() {
        return capacidade_tanque;
    }

    public void setCapacidade_tanque(BigDecimal capacidade_tanque) {
        this.capacidade_tanque = capacidade_tanque;
    }

    public String getTipo_combustivel() {
        return tipo_combustivel;
    }

    public void setTipo_combustivel(String tipo_combustivel) {
        this.tipo_combustivel = tipo_combustivel;
    }

    public Integer getIntervalo_troca_oleo_horas() {
        return intervalo_troca_oleo_horas;
    }

    public void setIntervalo_troca_oleo_horas(Integer intervalo_troca_oleo_horas) {
        this.intervalo_troca_oleo_horas = intervalo_troca_oleo_horas;
    }

    public Integer getIntervalo_inspecao_horas() {
        return intervalo_inspecao_horas;
    }

    public void setIntervalo_inspecao_horas(Integer intervalo_inspecao_horas) {
        this.intervalo_inspecao_horas = intervalo_inspecao_horas;
    }

    public BigDecimal getConsumo_medio() {
        return consumo_medio;
    }

    public void setConsumo_medio(BigDecimal consumo_medio) {
        this.consumo_medio = consumo_medio;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNivel_risco() {
        return nivel_risco;
    }

    public void setNivel_risco(String nivel_risco) {
        this.nivel_risco = nivel_risco;
    }

    public String getData_aquisicao() {
        return data_aquisicao;
    }

    public void setData_aquisicao(String data_aquisicao) {
        this.data_aquisicao = data_aquisicao;
    }

    public BigDecimal getValor_aquisicao() {
        return valor_aquisicao;
    }

    public void setValor_aquisicao(BigDecimal valor_aquisicao) {
        this.valor_aquisicao = valor_aquisicao;
    }

    public String getFoto_path() {
        return foto_path;
    }

    public void setFoto_path(String foto_path) {
        this.foto_path = foto_path;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
