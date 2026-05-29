package br.com.kahoot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BancoDePerguntas {

    private List<Pergunta> perguntas;

    public BancoDePerguntas() {
        this.perguntas = new ArrayList<>();
        carregarPerguntasIniciais();
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
        return Collections.unmodifiableList(new ArrayList<>(perguntas));
    }

    public void limpar() {
        perguntas.clear();
    }

    private void carregarPerguntasIniciais() {
        adicionarPergunta(new Pergunta(
                "Qual estrutura armazena pares chave-valor em Java?",
                new String[]{"List", "Set", "Map", "Queue"},
                2
        ));

        adicionarPergunta(new Pergunta(
                "Qual protocolo é usado normalmente para páginas web?",
                new String[]{"FTP", "HTTP", "SSH", "SMTP"},
                1
        ));

        adicionarPergunta(new Pergunta(
                "Qual palavra-chave cria uma herança em Java?",
                new String[]{"implements", "extends", "import", "package"},
                1
        ));
    }
}
