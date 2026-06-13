package br.com.kahoot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BancoDePerguntas {

    private final List<Pergunta> perguntas;

    public BancoDePerguntas() {
        this.perguntas = new ArrayList<>();
        carregarPerguntasIniciais();
    }

    public void adicionarPergunta(Pergunta pergunta) {
        if (pergunta == null) {
            throw new IllegalArgumentException("Pergunta nao pode ser nula");
        }
        perguntas.add(pergunta);
    }

    public Pergunta obterPergunta(int indice) {
        if (indice < 0 || indice >= perguntas.size()) {
            throw new IllegalArgumentException("Indice invalido");
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
                "Qual protocolo e usado normalmente para paginas web?",
                new String[]{"FTP", "HTTP", "SSH", "SMTP"},
                1
        ));

        adicionarPergunta(new Pergunta(
                "Qual palavra-chave cria uma heranca em Java?",
                new String[]{"implements", "extends", "import", "package"},
                1
        ));

        adicionarPergunta(new Pergunta(
                "Qual comando executa os testes de um projeto Maven?",
                new String[]{"mvn clean compile", "mvn test", "mvn package", "java -jar"},
                1
        ));

        adicionarPergunta(new Pergunta(
                "Qual classe e usada para criar uma conexao TCP no cliente em Java?",
                new String[]{"ServerSocket", "Socket", "DatagramSocket", "URL"},
                1
        ));
    }
}
