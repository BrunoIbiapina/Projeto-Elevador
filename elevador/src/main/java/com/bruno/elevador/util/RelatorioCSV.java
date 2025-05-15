package com.bruno.elevador.util;

import com.bruno.elevador.model.RelatorioPessoa;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class RelatorioCSV {

    public static void exportar(List<RelatorioPessoa> dados, String nomeArquivo) {
        try (FileWriter writer = new FileWriter(nomeArquivo)) {
            writer.append("Nome,Prioridade,TempoEspera,TempoViagem,ConsumoEnergia\n");
            for (RelatorioPessoa p : dados) {
                writer.append(p.getNome()).append(",")
                        .append(p.getPrioridade().name()).append(",")
                        .append(String.valueOf(p.getTempoEspera())).append(",")
                        .append(String.valueOf(p.getTempoViagem())).append(",")
                        .append(String.valueOf(p.getConsumoEnergia())).append("\n");
            }
            System.out.println("📁 Relatório gerado: " + nomeArquivo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
