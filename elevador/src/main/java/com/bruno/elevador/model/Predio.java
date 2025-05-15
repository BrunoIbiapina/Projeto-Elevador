package com.bruno.elevador.model;

import java.util.List;
import java.util.Queue;

public class Predio {

    private boolean horaPico; // Indica se está em horário de pico
    private CentrodeControle centroControle; // Responsável por comandar o elevador
    private List<Andar> andares; // Lista de todos os andares do prédio
    private List<Elevador> elevadores; // Lista de elevadores disponíveis
    private Queue<Pessoa> listaChegada; // Pessoas que chegaram no térreo

    public Predio(boolean horaPico, CentrodeControle centroControle, List<Andar> andares, Queue<Pessoa> listaChegada, List<Elevador> elevadores) {
        this.horaPico = horaPico;
        this.centroControle = centroControle;
        this.andares = andares;
        this.listaChegada = listaChegada;
        this.elevadores = elevadores;
    }

    // GETTERS E SETTERS
    public boolean isHoraPico() {
        return horaPico;
    }

    public void setHoraPico(boolean horaPico) {
        this.horaPico = horaPico;
    }

    public CentrodeControle getCentroControle() {
        return centroControle;
    }

    public void setCentroControle(CentrodeControle centroControle) {
        this.centroControle = centroControle;
    }

    public List<Andar> getAndares() {
        return andares;
    }

    public void setAndares(List<Andar> andares) {
        this.andares = andares;
    }

    public Queue<Pessoa> getListaChegada() {
        return listaChegada;
    }

    public void setListaChegada(Queue<Pessoa> listaChegada) {
        this.listaChegada = listaChegada;
    }

    public List<Elevador> getElevadores() {
        return elevadores;
    }

    public void setElevadores(List<Elevador> elevadores) {
        this.elevadores = elevadores;
    }

    // ✅ NOVO: Adiciona uma pessoa na fila de chegada
    public void adicionarPessoa(Pessoa pessoa) {
        listaChegada.add(pessoa);
        System.out.println("Pessoa " + pessoa.getNome() + " chegou no térreo para ir ao andar " + pessoa.getAndarDestino());
    }

    // ✅ NOVO: Simula um ciclo de operação do prédio
    public void simularCiclo() {
        if (elevadores.isEmpty()) {
            System.out.println("Nenhum elevador disponível.");
            return;
        }

        Elevador elevador = elevadores.get(0); // por enquanto usamos só o primeiro

        // Envia as pessoas da fila para o elevador
        while (!listaChegada.isEmpty()) {
            Pessoa p = listaChegada.poll();
            elevador.adicionarPessoa(p);
        }

        // Executa o ciclo do elevador
        elevador.executarCiclo();
    }
}
