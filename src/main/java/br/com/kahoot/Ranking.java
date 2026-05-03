package br.com.kahoot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Ranking {

    private final File file;
    private final List<Entry> entries = new ArrayList<>();

    public Ranking(String filePath) {
        this.file = new File(filePath);
    }

    public void carregar() throws Exception {
        entries.clear();
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 2);
                if (parts.length == 2) {
                    String name = parts[0];
                    float score;
                    try {
                        score = Float.parseFloat(parts[1]);
                    } catch (NumberFormatException e) {
                        continue;
                    }
                    entries.add(new Entry(name, score));
                }
            }
        }

        sortEntries();
    }

    public void load() throws Exception {
        carregar();
    }

    public void adicionarScore(String nome, float pontos) {
        entries.add(new Entry(nome, pontos));
        sortEntries();
    }

    public List<Entry> obterTop(int limit) {
        int end = Math.min(limit, entries.size());
        return new ArrayList<>(entries.subList(0, end));
    }

    public void save() throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Entry entry : entries) {
                writer.write(entry.getNome().replace("|", " ") + "|" + entry.getPontos());
                writer.newLine();
            }
        }
    }

    private void sortEntries() {
        entries.sort(Comparator.comparing(Entry::getPontos).reversed());
    }

    public static class Entry {
        private final String nome;
        private final float pontos;

        public Entry(String nome, float pontos) {
            this.nome = nome;
            this.pontos = pontos;
        }

        public String getNome() {
            return nome;
        }

        public float getPontos() {
            return pontos;
        }
    }
}
