package com.bruno.elevador.model;

import com.bruno.elevador.enums.TipodeComportamento;

public class CentrodeControle {

    private TipodeComportamento tipodeComportamento;

    public CentrodeControle(TipodeComportamento tipoDeComportamento) {
        this.tipodeComportamento = tipoDeComportamento;
    }

    public TipodeComportamento getTipodeComportamento() {
        return tipodeComportamento;
    }

    public void setTipodeComportamento(TipodeComportamento tipodeComportamento) {
        this.tipodeComportamento = tipodeComportamento;
    }

    public Elevador elevadorMaisProximo(int andarChamado, LIst<Elevador> elevadores) {
        Elevador maisProximo = null;
        int menorDistancia = Integer.MAX_VALUE;

        for (Elevador elevador : elevadores) {
            int distancia = Math.abs(elevador.getAndares() - andarChamado);

            if (distancia < menorDistancia && !elevador.isLotado()) {
                menorDistancia = distancia;
                maisProximo = elevador;
            }
        }
        return maisProximo;

    }

}
