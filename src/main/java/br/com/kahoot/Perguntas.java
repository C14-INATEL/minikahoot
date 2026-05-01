package br.com.kahoot;

import java.util.ArrayList;
import java.util.List;

public class Perguntas {

    private List<Pergunta> perguntas;

    public Perguntas() {
        this.perguntas = new ArrayList<>();
    }

    public void adicionarPergunta(Pergunta pergunta) {
        if (pergunta == null) {
            throw new IllegalArgumentException("Pergunta não pode ser nula");
        }
        perguntas.add(pergunta);
    }

    public Pergunta obterPergunta(int indice) {
        if (indice < 0 || indice >= perguntas.size()) {
            throw new IllegalArgumentException("Índice inválido");
        }
        return perguntas.get(indice);
    }

    public int getTotalPerguntas() {
        return perguntas.size();
    }

    public List<Pergunta> obterTodas() {
        return new ArrayList<>(perguntas);
    }

    public void limpar() {
        perguntas.clear();
    }
}