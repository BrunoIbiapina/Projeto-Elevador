// Atualizado para exibir o consumo total de energia no terminal
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

    private static Painel painel;
    private static Elevador elevador;
    private static final int TOTAL_ANDARES = 10;
    private static final int CAPACIDADE_MAXIMA = 5;
    private static int ciclo = 1;
    private static final Random random = new Random();
    private static int contadorPessoas = 1;
    private static int consumoTotalEnergia = 0;

    public static void simular() {
        painel = new Painel(null, 0);
        elevador = new Elevador(CAPACIDADE_MAXIMA, 0, painel);

        while (true) {
            simularChamadasAutomaticas();
            limparTerminal();
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            desenharPredioFixo(elevador.getAndarAtual(), passageiros);

            System.out.println("📟 Painel do Elevador:");
            System.out.println("   Andar Atual: " + elevador.getAndarAtual());
            System.out.print("   Passageiros: ");
            System.out.println(passageiros.isEmpty() ? "[nenhum]" : passageiros);
            System.out.print("   Destinos chamados: ");
            System.out.println(destinoPorPessoa.isEmpty() ? "[nenhum]" : destinoPorPessoa);
            System.out.print("   Chamadas pendentes: ");
            System.out.println(destinosPendentes.isEmpty() ? "[nenhum]" : destinosPendentes);
            System.out.println("⚡ Consumo total de energia: " + consumoTotalEnergia + " kWh");
            if (!relatorioFinal.isEmpty()) {
                double media = (double) consumoTotalEnergia / relatorioFinal.size();
                System.out.printf("⚖️  Média de consumo por pessoa: %.2f kWh", media);
                long alta = relatorioFinal.stream().filter(r -> r.getPrioridade() == Prioridade.ALTA).count();
                long normal = relatorioFinal.size() - alta;
                double mediaAlta = relatorioFinal.stream().filter(r -> r.getPrioridade() == Prioridade.ALTA).mapToInt(RelatorioPessoa::getConsumoEnergia).average().orElse(0);
                double mediaNormal = relatorioFinal.stream().filter(r -> r.getPrioridade() == Prioridade.NORMAL).mapToInt(RelatorioPessoa::getConsumoEnergia).average().orElse(0);
                System.out.printf("👥 Pessoas com prioridade ALTA: %d | Média: %.2f kWh", alta, mediaAlta);
                System.out.printf("👥 Pessoas com prioridade NORMAL: %d | Média: %.2f kWh", normal, mediaNormal);
            }

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
                    consumoTotalEnergia += moverElevador(elevador, proximo);
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

                            int tempoEspera = Math.max(0, ciclo - momentoChamada.getOrDefault(nome, ciclo));
                            int tempoViagem = Math.abs(destino - proximo);
                            int consumo = tempoViagem;

                            System.out.printf("🛗 %s entrou no elevador no andar %d. Indo para o andar %d. Prioridade: %s. Tempo de espera: %ds\n",
                                nome, proximo, destino, prioridadePorPessoa.get(nome).toUpperCase(), tempoEspera);

                            relatorioFinal.add(new RelatorioPessoa(
                                nome,
                                prioridadePorPessoa.get(nome).equals("alta") ? Prioridade.ALTA : Prioridade.NORMAL,
                                tempoEspera,
                                tempoViagem,
                                consumo
                            ));

                        } else {
                            System.out.println("❌ Capacidade máxima atingida! " + nome + " não pôde entrar.");
                            chamadasExternas.put(proximo, nome + ";" + destino);
                            destinosPendentes.add(proximo);
                        }
                    } else if (destinoPorPessoa.containsKey(proximo)) {
                        List<String> saindo = destinoPorPessoa.remove(proximo);
                        passageiros.removeAll(saindo);
                        for (String pessoa : saindo) {
                            System.out.println("🚪 " + pessoa + " saiu no andar " + proximo + ".");
                        }
                        desembarquesPorAndar.put(proximo, desembarquesPorAndar.getOrDefault(proximo, 0) + saindo.size());
                    }
                }
            }

            ciclo++;

            if (!relatorioFinal.isEmpty()) {
                RelatorioCSV.exportar(relatorioFinal, "relatorio_elevador.csv");
            }

            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void simularChamadasAutomaticas() {
        int chamadas = random.nextInt(3) + 1;
        for (int i = 0; i < chamadas; i++) {
            int origem = random.nextInt(TOTAL_ANDARES);
            int destino;
            do {
                destino = random.nextInt(TOTAL_ANDARES);
            } while (destino == origem);

            String nome = "Pessoa" + contadorPessoas++;
            String prioridade = random.nextBoolean() ? "alta" : "normal";

            adicionarChamada(nome, origem, destino, prioridade);
        }
    }

    private static void adicionarChamada(String nome, int origem, int destino, String prioridade) {
        prioridadePorPessoa.put(nome, prioridade);
        momentoChamada.put(nome, ciclo + 1);
        chamadasExternas.put(origem, nome + ";" + destino);
        if (!destinosPendentes.contains(origem)) destinosPendentes.add(origem);
    }

    private static int moverElevador(Elevador elevador, int destino) {
        int origem = elevador.getAndarAtual();
        int atual = elevador.getAndarAtual();
        int consumo = 0;
        while (atual != destino && destino >= 0 && destino < TOTAL_ANDARES) {
            atual += (destino > atual) ? 1 : -1;
            elevador.setAndarAtual(atual);
            desenharPredioFixo(atual, passageiros);
            System.out.println("📟 Painel: Andar Atual → " + atual);
            consumo++;
            try {
                Thread.sleep(1200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        consumoPorAndar.put(origem, consumoPorAndar.getOrDefault(origem, 0) + consumo);
        System.out.println("⚡ Deslocamento do andar " + origem + " até o andar " + destino + " consumiu " + consumo + " kWh");
        return consumo;
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
        desenharGraficoConsumoEnergia();
        System.out.println("\n📊 Desembarques por andar:");
        for (int i = 0; i < TOTAL_ANDARES; i++) {
            int count = mapa.getOrDefault(i, 0);
            String barras = "█".repeat(count);
            System.out.printf("Andar %2d: %s (%d)\n", i, barras, count);
        }
    }

    private static final Map<Integer, Integer> consumoPorAndar = new HashMap<>();

    private static void desenharGraficoConsumoEnergia() {
        System.out.println("⚡ Consumo de energia por deslocamento em cada andar:");
        for (int i = 0; i < TOTAL_ANDARES; i++) {
            int consumo = consumoPorAndar.getOrDefault(i, 0);
            String barras = "█".repeat(consumo);
            System.out.printf("Andar %2d: %s (%d kWh)", i, barras, consumo);
        }
    }

    private static void limparTerminal() {
        System.out.println("\n\n\n\n\n\n\n\n\n\n");
    }
}
