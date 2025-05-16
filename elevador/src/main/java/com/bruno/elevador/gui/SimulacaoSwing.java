package com.bruno.elevador.gui;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class SimulacaoSwing extends JPanel {

    private int andarAtual = 0;
    private final int totalAndares = 10;
    private final Timer timer;

    public SimulacaoSwing() {
        timer = new Timer(1200, e -> {
            andarAtual = new Random().nextInt(totalAndares); // substitua por lógica real
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setFont(new Font("Arial", Font.PLAIN, 14));

        for (int i = totalAndares - 1; i >= 0; i--) {
            int y = 30 + (totalAndares - 1 - i) * 50;

            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(50, y, 200, 40);

            g2.setColor(Color.BLACK);
            g2.drawRect(50, y, 200, 40);
            g2.drawString("Andar " + i, 60, y + 25);

            if (i == andarAtual) {
                g2.setColor(Color.BLUE);
                g2.fillRect(260, y + 5, 30, 30);
                g2.setColor(Color.WHITE);
                g2.drawString("🛗", 267, y + 25);
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Simulação de Elevador");
        SimulacaoSwing painel = new SimulacaoSwing();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350, 600);
        frame.add(painel);
        frame.setVisible(true);
    }
}
