package com.bruno.elevador.gui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ElevadorInterfaceSwing extends JPanel {

    private int andarAtual = 0;
    private final int totalAndares = 10;
    private final List<Passageiro> passageiros = new ArrayList<>();
    private final List<String> logEventos = new ArrayList<>();
    private final Map<Integer, Integer> consumoPorAndar = new HashMap<>();
    private int consumoTotal = 0;
    private int totalChamadas = 0;
    private final Random random = new Random();

    public ElevadorInterfaceSwing() {
        Timer chamadasAuto = new Timer(3000, e -> adicionarChamadaAleatoria());
        chamadasAuto.start();
        Timer timer = new Timer(2000, e -> moverPara(new Random().nextInt(totalAndares)));
        timer.start();
    }

    private void moverPara(int novoAndar) {
        if (novoAndar != andarAtual) {
            int consumo = Math.abs(novoAndar - andarAtual);
            consumoTotal += consumo;
            consumoPorAndar.put(andarAtual, consumoPorAndar.getOrDefault(andarAtual, 0) + consumo);
            logEventos.add("🛗 Movendo do andar " + andarAtual + " para o andar " + novoAndar + " (" + consumo + " kWh)");
        }

        // Embarcar/desembarcar passageiros
        List<Passageiro> embarcados = new ArrayList<>();
        for (Passageiro p : passageiros) {
            if (p.origem == novoAndar && p.destino == -1) {
                p.destino = random.nextInt(totalAndares);
                while (p.destino == novoAndar) p.destino = random.nextInt(totalAndares);
                logEventos.add("✅ " + p.nome + " entrou no elevador no andar " + novoAndar + " indo para " + p.destino);
                embarcados.add(p);
            }
        }

        for (Passageiro p : embarcados) passageiros.remove(p);
        passageiros.addAll(embarcados); // agora com destino definido

        List<Passageiro> saindo = new ArrayList<>();
        for (Passageiro p : passageiros) {
            if (p.destino == novoAndar) {
                logEventos.add("🚪 " + p.nome + " saiu no andar " + novoAndar);
                saindo.add(p);
            }
        }
        passageiros.removeAll(saindo);

        andarAtual = novoAndar;
        repaint();
    }

    public void adicionarChamadaManual() {
        adicionarChamada(andarAtual);
    }

    private void adicionarChamadaAleatoria() {
        int origem = random.nextInt(totalAndares);
        adicionarChamada(origem);
    }

    private void adicionarChamada(int origem) {
        String nome = "Pessoa" + random.nextInt(1000);
        String prioridade = random.nextBoolean() ? "ALTA" : "NORMAL";
        passageiros.add(new Passageiro(nome, origem, prioridade));
        logEventos.add("📞 " + nome + " chamou no andar " + origem + " (prioridade: " + prioridade + ")");
        totalChamadas++;
        repaint();
    }

    public void exportarRelatorio() {
        try (FileWriter writer = new FileWriter("relatorio_swing.csv")) {
            writer.write("Nome,Andar,Prioridade\n");
            for (Passageiro p : passageiros) {
                writer.write(p.nome + "," + p.origem + "," + p.prioridade + "\n");
            }
            writer.write("Consumo Total," + consumoTotal + "");
            double media = totalChamadas > 0 ? (double) consumoTotal / totalChamadas : 0;
            writer.write("Média por chamada," + String.format("%.2f", media) + "");
            writer.write("Log de Eventos:");
            for (String log : logEventos) {
                writer.write(log + "");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setFont(new Font("Arial", Font.PLAIN, 14));

        for (int i = totalAndares - 1; i >= 0; i--) {
            int y = 30 + (totalAndares - 1 - i) * 50;
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(50, y, 150, 40);
            g2.setColor(Color.BLACK);
            g2.drawRect(50, y, 150, 40);
            g2.drawString("Andar " + i, 60, y + 25);
            if (i == andarAtual) {
                g2.setColor(Color.BLUE);
                g2.fillRect(210, y + 5, 30, 30);
                g2.setColor(Color.WHITE);
                g2.drawString("🛗", 217, y + 25);
            }
        }

        int panelX = 270;
        int y = 30;
        g2.setColor(Color.BLACK);
        g2.drawString("👥 No elevador:", panelX, y);
        y += 20;
        for (Passageiro p : passageiros) {
            g2.setColor(p.prioridade.equals("ALTA") ? Color.RED : Color.BLACK);
            g2.drawString("- " + p.nome + " (" + p.prioridade + ")", panelX, y);
            y += 20;
        }

        y += 10;
        g2.setColor(Color.BLACK);
        g2.drawString("⚡ Consumo Total: " + consumoTotal + " kWh", panelX, y);

        y += 20;
        double media = totalChamadas > 0 ? (double) consumoTotal / totalChamadas : 0;
        g2.drawString("⏱ Média consumo/chamada: " + String.format("%.2f", media) + " kWh", panelX, y);

        y += 30;
        g2.drawString("📜 Log de eventos:", panelX, y);
        y += 15;
        int maxLogs = 10;
        for (int i = Math.max(0, logEventos.size() - maxLogs); i < logEventos.size(); i++) {
            g2.drawString(logEventos.get(i), panelX, y);
            y += 15;
        }

        y += 15;
        g2.drawString("📊 Consumo por andar:", panelX, y);
        y += 15;
        for (int i = 0; i < totalAndares; i++) {
            int consumo = consumoPorAndar.getOrDefault(i, 0);
            g2.drawString("Andar " + i + ":", panelX, y);
            g2.setColor(Color.GREEN);
            g2.fillRect(panelX + 70, y - 10, consumo * 10, 10);
            g2.setColor(Color.BLACK);
            g2.drawString(consumo + " kWh", panelX + 75 + consumo * 10, y);
            y += 15;
        }
    }

    static class Passageiro {
        String nome;
        int origem;
        String prioridade;
        int destino;

        public Passageiro(String nome, int origem, String prioridade) {
            this.destino = -1;
            this.nome = nome;
            this.origem = origem;
            this.prioridade = prioridade;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Elevador - Interface Gráfica");
        ElevadorInterfaceSwing painel = new ElevadorInterfaceSwing();

        JButton chamar = new JButton("Chamar Elevador");
        chamar.addActionListener((ActionEvent e) -> painel.adicionarChamadaManual());

        JButton exportar = new JButton("Exportar Relatório");
        exportar.addActionListener(e -> painel.exportarRelatorio());

        JPanel botoes = new JPanel();
        botoes.add(chamar);
        botoes.add(exportar);

        frame.setLayout(new BorderLayout());
        frame.add(painel, BorderLayout.CENTER);
        frame.add(botoes, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 700);
        frame.setVisible(true);
    }
}
