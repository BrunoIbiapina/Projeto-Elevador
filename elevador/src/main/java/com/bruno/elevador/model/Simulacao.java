package com.bruno.elevador.model;

import com.bruno.elevador.enums.Prioridade;
import com.bruno.elevador.enums.TipoPainel;
import com.bruno.elevador.enums.TipodeComportamento;

import java.util.*;

public class Simulacao {

    private static final Random random = new Random();
    private static final String[] nomes = {"Ana", "Bruno", "João", "Clara", "Carlos", "Paula", "Lucas", "Duda"};

    public static void simular() {
        Painel painel = new Painel(TipoPainel.DIGITAL, 0);
        Elevador elevador = new Elevador(4, 0, painel);
        List<Elevador> elevadores = List.of(elevador);
        List<Andar> andares = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            andares.add(new Andar(i));
        }
        Queue<Pessoa> filaChegada = new LinkedList<>();
        CentrodeControle centro = new CentrodeControle(TipodeComportamento.NORMAL);
        Predio predio = new Predio(false, centro, andares, filaChegada, elevadores);

        int ciclo = 1;

        while (true) {
            limparTerminal();
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("🕒 CICLO " + ciclo);

            Pessoa novaPessoa = gerarPessoa();
            predio.adicionarPessoa(novaPessoa);

            // Captura antes e depois do ciclo
            int andarAntes = elevador.getAndarAtual();

            predio.simularCiclo();

            int andarDepois = elevador.getAndarAtual();

            desenharPredio(andarDepois, novaPessoa, andarAntes, andarDepois);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ciclo++;
        }
    }

    private static Pessoa gerarPessoa() {
        String nome = nomes[random.nextInt(nomes.length)];
        int origem = random.nextInt(5);
        int destino;
        do {
            destino = random.nextInt(5);
        } while (destino == origem);

        Prioridade prioridade = Prioridade.values()[random.nextInt(Prioridade.values().length)];
        return new Pessoa(nome, origem, destino, prioridade);
    }

    private static void desenharPredio(int andarAtual, Pessoa pessoa, int de, int para) {
        System.out.println();
        for (int i = 4; i >= 0; i--) {
            String elevador = (i == andarAtual) ? "🛗" : "  ";
            String info = "";

            if (i == pessoa.getAndarOrigem()) {
                info = "👤 " + pessoa.getNome() + " entrou (➜ " + pessoa.getAndarDestino() + ")";
            } else if (i == pessoa.getAndarDestino() && de != para) {
                info = "🚪 " + pessoa.getNome() + " saiu";
            }

            System.out.printf("Andar %d ──│ %s │  %s\n", i, elevador, info);
        }
        System.out.println("          └─────┘");
    }

    private static void limparTerminal() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            System.out.println("Erro ao limpar terminal.");
        }
    }
}
