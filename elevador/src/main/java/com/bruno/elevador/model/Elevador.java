package com.bruno.elevador.model;

import java.util.ArrayList;
import java.util.List;

public class Elevador {

    private int capacidadeMaxima;
    private int capacidadeAtual;
    private String sentido; // subindi, descendo, parado
    private int andarAtual;
    private Painel painel;
    private List<Pessoa> pessoas;
    private List<Integer> listaDePessoas;

    public Elevador(int capacidadeMaxima, int andarInicial, Painel painel) {
        this.capacidadeMaxima = capacidadeMaxima;
        this.capacidadeAtual = 0;
        this.sentido = "parado";
        this.andarAtual = andarInicial;
        this.painel = painel;
        this.pessoas = new ArrayList<>();
        this.listaDePessoas = new ArrayList<>();
    }

    public boolean isLotado() {
        return capacidadeAtual >= capacidadeMaxima;
    }

    public void abrePorta() {
        System.out.println("Elevador abrindo as portas no andar" + andarAtual);
    }

    public void fechaPorta() {
        System.out.println("Elevador fechando as portas.");
    }

    public void irPara(int andar) {
        if (andar == andarAtual) {
            System.out.println("Elevador já está no andar " + andar);
            return;
        }
        if (andar > andarAtual) {
            sentido = "subindo";

        }else{
            sentido = "descendo";
        }

        System.out.println("Elevador indo do andar " + andarAtual + "para o andar" + andar);
        andarAtual = andar;
        painel.setValor(andar);
        sentido = "parado";
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public void setCapacidadeMaxima(int capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public int getCapacidadeAtual() {
        return capacidadeAtual;
    }

    public void setCapacidadeAtual(int capacidadeAtual) {
        this.capacidadeAtual = capacidadeAtual;
    }

    public String getSentido() {
        return sentido;
    }

    public void setSentido(String sentido) {
        this.sentido = sentido;
    }

    public int getAndarAtual() {
        return andarAtual;
    }

    public void setAndarAtual(int andarAtual) {
        this.andarAtual = andarAtual;
    }

    public Painel getPainel() {
        return painel;
    }

    public void setPainel(Painel painel) {
        this.painel = painel;
    }

    public List<Pessoa> getPessoas() {
        return pessoas;
    }

    public void setPessoas(List<Pessoa> pessoas) {
        this.pessoas = pessoas;
    }

    public List<Integer> getListaDePessoas() {
        return listaDePessoas;
    }

    public void setListaDePessoas(List<Integer> listaDePessoas) {
        this.listaDePessoas = listaDePessoas;
    }

    

}
