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

    public String interpretarChamada() {
        return switch (tipo) {
            case UNICO -> "Chamada geral enviada";
            case DOIS_BOTOES -> (valor == 1 ? "Chamada para subir" : "Chamada para descer");
            case NUMERICO -> "Chamada para andar " + valor;
        };
    }

    @Override
    public String toString() {
        return interpretarChamada();
    }
}
