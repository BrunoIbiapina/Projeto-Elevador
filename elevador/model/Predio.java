package com.bruno.elevador.model;

import java.util.List;
import java.util.Queue;

public class Predio {

    private boolean horaPico;
    private CentroControle centroControle;
    private List<Andar> andares;
    private List<Elevador> elevadores;
    private Queue<Pessoa> listaChegada;

    public Predio(boolean horaPico, CentroControle centroControle, List<Andar> andares, Queue<Pessoa> listaChegada, List<Elevador> elevadores) {
        this.horaPico = horaPico;
        this.centroControle = centroControle;
        this.andares = andares;
        this.listaChegada = listaChegada;
        this.elevadores = elevadores;

    }

    public boolean isHoraPico() {
        return horaPico;
    }

    public void setHoraPico(boolean horaPico) {
        this.horaPico = horaPico;
    }

    public CentroControle getCentroControle() {
        return centroControle;
    }

    public void setCentroControle(CentroControle centroControle) {
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

    

}
