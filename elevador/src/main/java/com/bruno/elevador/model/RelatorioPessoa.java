package com.bruno.elevador.model;

import com.bruno.elevador.enums.Prioridade;

public class RelatorioPessoa {
    private String nome;
    private Prioridade prioridade;
    private int tempoEspera;
    private int tempoViagem;
    private int consumoEnergia;

    public RelatorioPessoa(String nome, Prioridade prioridade, int tempoEspera, int tempoViagem, int consumoEnergia) {
        this.nome = nome;
        this.prioridade = prioridade;
        this.tempoEspera = tempoEspera;
        this.tempoViagem = tempoViagem;
        this.consumoEnergia = consumoEnergia;
    }

    public String getNome() { return nome; }
    public Prioridade getPrioridade() { return prioridade; }
    public int getTempoEspera() { return tempoEspera; }
    public int getTempoViagem() { return tempoViagem; }
    public int getConsumoEnergia() { return consumoEnergia; }
}
