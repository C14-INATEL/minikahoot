package br.com.kahoot;

public class Pergunta {

    private String enunciado;
    private String[] alternativas;
    private int respostaCorreta;

    public Pergunta(String enunciado, String[] alternativas, int respostaCorreta) {
        this.enunciado = enunciado;
        this.alternativas = alternativas;
        this.respostaCorreta = respostaCorreta;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public String[] getAlternativas() {
        return alternativas;
    }

    public int getRespostaCorreta() {
        return respostaCorreta;
    }

    public boolean verificarResposta(int resposta) {
        return resposta == respostaCorreta;
    }

    @Override
    public String toString() {
        String texto = enunciado + "\n";
        for (int i = 0; i < alternativas.length; i++) {
            texto += (i + 1) + ") " + alternativas[i] + "\n";
        }
        return texto;
    }
}