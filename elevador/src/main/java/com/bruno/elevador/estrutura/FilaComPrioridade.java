package com.bruno.elevador.estrutura;

import com.bruno.elevador.model.Pessoa;
import com.bruno.elevador.enums.Prioridade;

public class FilaComPrioridade {

    private No inicio;
    private No fim;

    private class No {
        Pessoa pessoa;
        No proximo;

        public No(Pessoa pessoa) {
            this.pessoa = pessoa;
        }
    }

    public void enfileirar(Pessoa novaPessoa) {
        No novoNo = new No(novaPessoa);

        if (inicio == null) {
            inicio = fim = novoNo;
            return;
        }

        if (novaPessoa.getPrioridade() == Prioridade.CADEIRANTE) {
            novoNo.proximo = inicio;
            inicio = novoNo;
            return;
        }

        if (novaPessoa.getPrioridade() == Prioridade.IDOSO) {
            No atual = inicio;

            if (atual.pessoa.getPrioridade() != Prioridade.CADEIRANTE) {
                novoNo.proximo = inicio;
                inicio = novoNo;
                return;
            }

            while (atual.proximo != null &&
                    atual.proximo.pessoa.getPrioridade() == Prioridade.CADEIRANTE) {
                atual = atual.proximo;
            }

            novoNo.proximo = atual.proximo;
            atual.proximo = novoNo;
            return;
        }

        fim.proximo = novoNo;
        fim = novoNo;
    }

    public Pessoa desenfileirar() {
        if (inicio == null) {
            return null;
        }
        Pessoa p = inicio.pessoa;
        inicio = inicio.proximo;
        if (inicio == null) {
            fim = null;
        }
        return p;
    }

    public boolean estraVazia() {
        return inicio == null;
    }
}
