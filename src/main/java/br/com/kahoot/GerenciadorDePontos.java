package br.com.kahoot;

import java.util.Arrays;

public class GerenciadorDePontos {

    private static final float TEMPO_LIMITE_RESPOSTA = 15.0f;
    private static final float PONTOS_BASE = 100.0f;

    private String[] jogadores;
    private float[] pontos;

    public GerenciadorDePontos(String[] jogadores, int numJogadores) {
        validarJogadores(jogadores);
        validarNumJogadores(numJogadores);
        validarQuantidadeJogadores(jogadores, numJogadores);

        this.jogadores = Arrays.copyOf(jogadores, jogadores.length);
        this.pontos = new float[numJogadores];

        for (int i = 0; i < numJogadores; i++) {
            pontos[i] = 0;
        }
    }

    public void adicionarPontos(int idCliente, float tempoPercorrido) {
        validarIdCliente(idCliente);

        float pontosGanhos = PONTOS_BASE * Math.max(0, TEMPO_LIMITE_RESPOSTA - tempoPercorrido);
        pontos[idCliente] += pontosGanhos;
    }

    public float getPontos(int idCliente) {
        validarIdCliente(idCliente);
        return pontos[idCliente];
    }

    public String[] getJogadores() {
        return Arrays.copyOf(jogadores, jogadores.length);
    }

    private void validarJogadores(String[] jogadores) {
        if (jogadores == null) {
            throw new IllegalArgumentException("Jogadores nao podem ser nulos");
        }
    }

    private void validarNumJogadores(int numJogadores) {
        if (numJogadores <= 0) {
            throw new IllegalArgumentException("Numero de jogadores deve ser maior que zero");
        }
    }

    private void validarQuantidadeJogadores(String[] jogadores, int numJogadores) {
        if (jogadores.length != numJogadores) {
            throw new IllegalArgumentException("Quantidade de jogadores deve corresponder ao numero informado");
        }
    }

    private void validarIdCliente(int idCliente) {
        if (idCliente < 0 || idCliente >= pontos.length) {
            throw new IllegalArgumentException("ID do cliente invalido");
        }
    }
}
