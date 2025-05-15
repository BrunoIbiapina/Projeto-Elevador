package com.bruno.elevador.model;

import com.bruno.elevador.enums.Prioridade;

public class Pessoa {

    private String nome;
    private int andarOrigem;
    private int andarDestino;
    private Prioridade prioridade;

    public Pessoa(String nome, int andarOrigem, int andarDestino, Prioridade prioridade) {
        this.nome = nome;
        this.andarOrigem = andarOrigem;
        this.andarDestino = andarDestino;
        this.prioridade = prioridade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getAndarOrigem() {
        return andarOrigem;
    }

    public void setAndarOrigem(int andarOrigem) {
        this.andarOrigem = andarOrigem;
    }

    public int getAndarDestino() {
        return andarDestino;
    }

    public void setAndarDestino(int andarDestino) {
        this.andarDestino = andarDestino;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Prioridade prioridade) {
        this.prioridade = prioridade;
    }
}
