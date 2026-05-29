package br.com.kahoot;

import java.util.Arrays;

public class Pergunta {

    private static final int MINIMO_ALTERNATIVAS = 2;

    private String enunciado;
    private String[] alternativas;
    private int respostaCorreta;

    public Pergunta(String enunciado, String[] alternativas, int respostaCorreta) {
        validarEnunciado(enunciado);
        validarAlternativas(alternativas);
        validarRespostaCorreta(alternativas, respostaCorreta);

        this.enunciado = enunciado.trim();
        this.alternativas = Arrays.copyOf(alternativas, alternativas.length);
        this.respostaCorreta = respostaCorreta;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public String[] getAlternativas() {
        return Arrays.copyOf(alternativas, alternativas.length);
    }

    public int getRespostaCorreta() {
        return respostaCorreta;
    }

    public boolean verificarResposta(int resposta) {
        return resposta == respostaCorreta;
    }

    public String formatarParaEnvio() {
        StringBuilder texto = new StringBuilder(enunciado).append("\n");
        for (int i = 0; i < alternativas.length; i++) {
            texto.append(i + 1)
                    .append(") ")
                    .append(alternativas[i])
                    .append("\n");
        }
        return texto.toString();
    }

    @Override
    public String toString() {
        return formatarParaEnvio();
    }

    private void validarEnunciado(String enunciado) {
        if (enunciado == null || enunciado.trim().isEmpty()) {
            throw new IllegalArgumentException("Enunciado nao pode ser vazio ou nulo");
        }
    }

    private void validarAlternativas(String[] alternativas) {
        if (alternativas == null || alternativas.length == 0) {
            throw new IllegalArgumentException("Alternativas nao podem ser vazias ou nulas");
        }

        if (alternativas.length < MINIMO_ALTERNATIVAS) {
            throw new IllegalArgumentException("Pergunta deve ter pelo menos duas alternativas");
        }
    }

    private void validarRespostaCorreta(String[] alternativas, int respostaCorreta) {
        if (respostaCorreta < 0 || respostaCorreta >= alternativas.length) {
            throw new IllegalArgumentException("Resposta correta fora do intervalo das alternativas");
        }
    }
}
