package br.com.kahoot;

import java.util.ArrayList;
import java.util.List;

public class Perguntas {
    private List<Pergunta> perguntas;
    
    public Perguntas() {
        this.perguntas = new ArrayList<>();
    }
    
    public void adicionarPergunta(Pergunta pergunta) {
        if (pergunta != null) {
            perguntas.add(pergunta);
        }
    }
    
    public void removerPergunta(int indice) {
        if (indice >= 0 && indice < perguntas.size()) {
            perguntas.remove(indice);
        }
    }
    
    public Pergunta obterPergunta(int indice) {
        if (indice >= 0 && indice < perguntas.size()) {
            return perguntas.get(indice);
        }
        return null;
    }
    
    public int getTotalPerguntas() {
        return perguntas.size();
    }
    
    public List<Pergunta> obterTodas() {
        return new ArrayList<>(perguntas);
    }
    
    public void exibirTodas() {
        for (int i = 0; i < perguntas.size(); i++) {
            System.out.println("=== Pergunta " + (i + 1) + " ===");
            System.out.println(perguntas.get(i));
        }
    }
    
    public void limpar() {
        perguntas.clear();
    }
}
