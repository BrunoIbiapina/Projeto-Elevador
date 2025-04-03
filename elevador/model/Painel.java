package com.bruno.elevador.model;

import com.bruno.elevador.enums.TipoPainel;

public class Painel {

    private TipoPainel tipo;
    private int valor;

    public Painel(TipoPainel tipo, int valor) {
        this.tipo = tipo;
        this.valor = valor;
    }

    public TipoPainel getTipo() {
        return tipo;
    }

    public void setTipo(TipoPainel tipo) {
        this.tipo = tipo;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

}
