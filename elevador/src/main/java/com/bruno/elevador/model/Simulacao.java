// Atualizado para lógica com relatório completo e prioridade
package com.bruno.elevador.model;

import com.bruno.elevador.enums.Prioridade;
import com.bruno.elevador.util.RelatorioCSV;

import java.util.*;

public class Simulacao {

    private static List<Integer> destinosPendentes = new ArrayList<>();
    private static List<String> passageiros = new ArrayList<>();
    private static Map<Integer, List<String>> destinoPorPessoa = new HashMap<>();
    private static Map<Integer, Integer> desembarquesPorAndar = new HashMap<>();
    private static Map<Integer, String> chamadasExternas = new HashMap<>();
    private static Map<String, String> prioridadePorPessoa = new HashMap<>();
    private static Map<String, Integer> momentoChamada = new HashMap<>();
    private static List<RelatorioPessoa> relatorioFinal = new ArrayList<>();

    private static final Scanner scanner = new Scanner(System.in);
    private static Painel painel;
    private static Elevador elevador;
    private static final int TOTAL_ANDARES = 10;
    private static final int CAPACIDADE_MAXIMA = 5;

    public static void simular() {
        painel = new Painel(null, 0);
        elevador = new Elevador(CAPACIDADE_MAXIMA, 0, painel);

        new Thread(() -> {
            while (true) {
                try {
                    System.out.print("Chamada (nome,origem,destino,prioridade) ou ENTER para ignorar: ");
                    String linha = scanner.nextLine().trim();
                    if (!linha.isEmpty()) {
                        String[] partes = linha.split(",");
                        if (partes.length == 4) {
                            String nome = partes[0].trim();
                            int origem = Integer.parseInt(partes[1].trim());
                            int destino = Integer.parseInt(partes[2].trim());
                            String prioridade = partes[3].trim().toLowerCase();
                            prioridadePorPessoa.put(nome, prioridade);
                            momentoChamada.put(nome, ciclo);

                            synchronized (Simulacao.class) {
                                if (origem == destino) return;
                                if (passageiros.size() < CAPACIDADE_MAXIMA && origem == painel.getValor()) {
                                    passageiros.add(nome);
                                    destinoPorPessoa.putIfAbsent(destino, new ArrayList<>());
                                    destinoPorPessoa.get(destino).add(nome);
                                    if (!destinosPendentes.contains(destino)) destinosPendentes.add(destino);
                                    System.out.println("✅ " + nome + " entrou diretamente no elevador");
                                } else {
                                    chamadasExternas.put(origem, nome + ";" + destino);
                                    if (!destinosPendentes.contains(origem)) destinosPendentes.add(origem);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("❌ Entrada inválida. Formato: nome,origem,destino,prioridade");
                }
            }
        }).start();

        while (true) {
            limparTerminal();
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("📟 Painel do Elevador:");
            System.out.println("   Andar Atual: " + elevador.getAndarAtual());
            System.out.println("   Passageiros: " + passageiros);
            System.out.println("   Destinos chamados: " + destinoPorPessoa.keySet());
            System.out.println("   Chamadas pendentes: " + destinosPendentes);

            desenharPredioFixo(elevador.getAndarAtual(), passageiros);
            desenharGraficoDesembarques(desembarquesPorAndar);

            while (!destinosPendentes.isEmpty()) {
                Integer proximo = null;
                int andarAtual = elevador.getAndarAtual();
                int menorDist = TOTAL_ANDARES + 1;

                for (Integer chamado : destinosPendentes) {
                    int dist = Math.abs(chamado - andarAtual);
                    if (dist < menorDist) {
                        proximo = chamado;
                        menorDist = dist;
                    }
                }

                if (proximo != null) {
                    moverElevador(elevador, proximo);
                    destinosPendentes.remove(proximo);

                    if (chamadasExternas.containsKey(proximo)) {
                        String[] dados = chamadasExternas.remove(proximo).split(";");
                        String nome = dados[0];
                        int destino = Integer.parseInt(dados[1]);
                        if (passageiros.size() < CAPACIDADE_MAXIMA) {
                            passageiros.add(nome);
                            destinoPorPessoa.putIfAbsent(destino, new ArrayList<>());
                            destinoPorPessoa.get(destino).add(nome);
                            if (!destinosPendentes.contains(destino)) destinosPendentes.add(destino);

                            int tempoEspera = ciclo - momentoChamada.getOrDefault(nome, ciclo);
                            int tempoViagem = Math.abs(destino - proximo);
                            int consumo = tempoViagem;

                            relatorioFinal.add(new RelatorioPessoa(
                                nome,
                                prioridadePorPessoa.get(nome).equals("alta") ? Prioridade.ALTA : Prioridade.NORMAL,
                                tempoEspera,
                                tempoViagem,
                                consumo
                            ));

                            System.out.println("🛗 Pegou " + nome + " no andar " + proximo);
                        } else {
                            System.out.println("❌ Capacidade máxima atingida! " + nome + " não pôde entrar.");
                            chamadasExternas.put(proximo, nome + ";" + destino);
                            destinosPendentes.add(proximo);
                        }
                    } else if (destinoPorPessoa.containsKey(proximo)) {
                        List<String> saindo = destinoPorPessoa.remove(proximo);
                        passageiros.removeAll(saindo);
                        System.out.println("🚪 Saíram no andar " + proximo + ": " + String.join(", ", saindo));
                        desembarquesPorAndar.put(proximo, desembarquesPorAndar.getOrDefault(proximo, 0) + saindo.size());
                    }
                }
            }

            ciclo++;

            if (!relatorioFinal.isEmpty()) {
                RelatorioCSV.exportar(relatorioFinal, "relatorio_elevador.csv");
            }

            try {
                Thread.sleep(1500); // Aguarda 1.5 segundos antes de novo ciclo
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static int ciclo = 1;

    private static void moverElevador(Elevador elevador, int destino) {
        int atual = elevador.getAndarAtual();
        while (atual != destino && destino >= 0 && destino < TOTAL_ANDARES) {
            atual += (destino > atual) ? 1 : -1;
            elevador.setAndarAtual(atual);
            desenharPredioFixo(atual, passageiros);
            System.out.println("📟 Painel: Andar Atual → " + atual);
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void desenharPredioFixo(int elevadorNoAndar, List<String> passageiros) {
        System.out.println();
        for (int i = TOTAL_ANDARES - 1; i >= 0; i--) {
            String elevadorIcone = (i == elevadorNoAndar) ? "🛗" : "  ";
            String extra = (i == elevadorNoAndar && !passageiros.isEmpty()) ? "  👥 " + passageiros.size() + " pessoa(s)" : "";
            System.out.printf("Andar %2d ──│ %s │%s\n", i, elevadorIcone, extra);
        }
        System.out.println("              └─────┘");
    }

    private static void desenharGraficoDesembarques(Map<Integer, Integer> mapa) {
        System.out.println("\n📊 Desembarques por andar:");
        for (int i = 0; i < TOTAL_ANDARES; i++) {
            int count = mapa.getOrDefault(i, 0);
            String barras = "█".repeat(count);
            System.out.printf("Andar %2d: %s (%d)\n", i, barras, count);
        }
    }

    private static void limparTerminal() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}