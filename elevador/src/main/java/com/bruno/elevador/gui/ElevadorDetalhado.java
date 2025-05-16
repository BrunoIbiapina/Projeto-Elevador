package com.bruno.elevador.gui;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class ElevadorDetalhado extends JPanel {

    private void gerarPassageiroAleatorio() {
        int origem = random.nextInt(totalAndares);
        int destino;
        do {
            destino = random.nextInt(totalAndares);
        } while (destino == origem);

        String nome = "Pessoa" + random.nextInt(1000);
        String prioridade = random.nextBoolean() ? "ALTA" : "NORMAL";

        passageiros.add(new Passageiro(nome, origem, destino, prioridade));
    }

    private static class Passageiro {
        String nome;
        int origem;
        int destino;
        String prioridade;

        Passageiro(String nome, int origem, int destino, String prioridade) {
            this.nome = nome;
            this.origem = origem;
            this.destino = destino;
            this.prioridade = prioridade;
        }
    }

    private int andarAtual = 0;
    private final int totalAndares = 10;
    private boolean portasAbertas = false;
    private final java.util.List<Passageiro> passageiros = new java.util.ArrayList<>();
    private final java.util.List<String> logs = new java.util.ArrayList<>();
    private final Random random = new Random();
    private final Timer animacaoPorta;
    private final java.util.Map<Integer, Integer> consumoPorAndar = new java.util.HashMap<>();
    private long tempoUltimaTroca = System.currentTimeMillis();
    private final java.util.List<Long> temposDeDeslocamento = new java.util.ArrayList<>();

    public ElevadorDetalhado() {
        Timer[] portaHolder = new Timer[1];
        portaHolder[0] = new Timer(1200, e -> {
            portasAbertas = false;
            repaint();
            portaHolder[0].stop();
        });
        this.animacaoPorta = portaHolder[0];
        // linha removida: 'porta' não existe, animacaoPorta já foi atribuído acima

        Timer mover = new Timer(2500, e -> {
            int novoAndar = new Random().nextInt(totalAndares);
            // mover antes de atualizar andarAtual
            int consumo = Math.abs(andarAtual - novoAndar);
            consumoPorAndar.put(novoAndar, consumoPorAndar.getOrDefault(novoAndar, 0) + consumo);
            if (novoAndar != andarAtual) {
                // Desembarcar passageiros
                java.util.List<Passageiro> saindo = new java.util.ArrayList<>();
                for (Passageiro p : passageiros) {
                    if (p.destino == novoAndar) {
                        String msg = "🚪 " + p.nome + " saiu no andar " + novoAndar + " (Origem: " + p.origem + ", Destino: " + p.destino + ")";
                        System.out.println(msg);
                        logs.add(msg);
                        saindo.add(p);
                    }
                }
                passageiros.removeAll(saindo);

                // Embarcar passageiros
                for (Passageiro p : passageiros) {
                    if (p.origem == novoAndar && p.destino != novoAndar) {
                        String msg = "✅ " + p.nome + " entrou no elevador no andar " + novoAndar + " indo para " + p.destino;
                        System.out.println(msg);
                        logs.add(msg);
                    }
                }
                long tempoAtual = System.currentTimeMillis();
                long duracao = tempoAtual - tempoUltimaTroca;
                temposDeDeslocamento.add(duracao);
                tempoUltimaTroca = tempoAtual;
                andarAtual = novoAndar;
                portasAbertas = true;
                // já contabilizado acima, remover duplicação
                repaint();
                animacaoPorta.restart();
            }
        });
        mover.start();

        Timer chamadas = new Timer(4000, e -> gerarPassageiroAleatorio());
        chamadas.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Desenhar prédio
        for (int i = totalAndares - 1; i >= 0; i--) {
            int y = 30 + (totalAndares - 1 - i) * 60;
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(80, y, 160, 50);
            g2.setColor(Color.BLACK);
            g2.drawRect(80, y, 160, 50);
            g2.drawString("Andar " + i, 20, y + 30);

            if (i == andarAtual) {
                g2.setColor(Color.LIGHT_GRAY);
                g2.fillRect(82, y + 2, 156, 46);

                // Portas animadas
                if (portasAbertas) {
                    g2.setColor(Color.GRAY);
                    g2.fillRect(82, y + 2, 75, 46);
                    g2.fillRect(163, y + 2, 75, 46);
                } else {
                    g2.setColor(Color.GRAY);
                    g2.fillRect(82, y + 2, 156, 46);
                }

                // Painel digital no elevador
                g2.setColor(Color.RED);
                g2.setFont(new Font("Monospaced", Font.BOLD, 12));
                g2.drawString("[🛗]", 140, y + 30);
            }
        }

        // Painel lateral digital
        g2.setColor(Color.GREEN);
        g2.drawString("-- Gráfico de Consumo --", 520, 50);
        int xBar = 520;
        int yBar = 70;
        for (int i = 0; i < totalAndares; i++) {
            int consumo = consumoPorAndar.getOrDefault(i, 0);
            g2.setColor(Color.ORANGE);
            g2.fillRect(xBar, yBar + i * 20, consumo * 10, 15);
            g2.setColor(Color.GREEN);
            g2.drawString("Andar " + i + " (" + consumo + " kWh)", xBar + consumo * 10 + 5, yBar + i * 20 + 12);
        }
        g2.setColor(Color.GREEN);
        int totalConsumo = consumoPorAndar.values().stream().mapToInt(Integer::intValue).sum();
        g2.drawString("-- Consumo por Andar --", 310, 480);
        if (!temposDeDeslocamento.isEmpty()) {
            long soma = temposDeDeslocamento.stream().mapToLong(Long::longValue).sum();
            long media = soma / temposDeDeslocamento.size();
            g2.drawString("Média deslocamento: " + media + " ms", 310, 465);
        }
        g2.drawString("Total: " + totalConsumo + " kWh", 310, 495);
        int yC = 510;
        for (int i = 0; i < totalAndares; i++) {
            int c = consumoPorAndar.getOrDefault(i, 0);
            g2.drawString("Andar " + i + ": " + c + " kWh", 310, yC);
            yC += 15;
        }
        g2.setColor(Color.BLACK);
        g2.fillRect(300, 30, 200, 500);
        g2.setColor(Color.GREEN);
        g2.setFont(new Font("Monospaced", Font.BOLD, 14));
        g2.drawString("== Painel Digital ==", 310, 50);
        g2.drawString("Andar atual: " + andarAtual, 310, 80);
        g2.drawString("Portas: " + (portasAbertas ? "Abertas" : "Fechadas"), 310, 100);

        g2.drawString("-- Passageiros --", 310, 130);
        g2.drawString("-- Últimas Ações --", 310, 630);
        int yL = 650;
        int logSize = logs.size();
        for (int i = Math.max(0, logSize - 5); i < logSize; i++) {
            g2.drawString(logs.get(i), 310, yL);
            yL += 18;
        }
        int yP = 150;
        for (Passageiro p : passageiros) {
            if (yP > 500) break;
            g2.drawString(p.nome + " " + p.origem + "→" + p.destino + ("ALTA".equals(p.prioridade) ? " 🔴" : ""), 310, yP);
            yP += 20;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Elevador Visual Detalhado");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(520, 700);
        frame.add(new ElevadorDetalhado());
        frame.setVisible(true);
    }
}
