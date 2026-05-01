package br.com.kahoot;

public class GerenciadorPontos {

    private String[] jogadores;
    private float[] pontos;

    public GerenciadorPontos(String[] jogadores, int numJogadores) {
        this.jogadores = jogadores;
        this.pontos = new float[numJogadores];

        for (int i = 0; i < numJogadores; i++) {
            pontos[i] = 0;
        }
    }

    public void adicionarPontos(int idCliente, float tempoPercorrido) {
        if (idCliente < 0 || idCliente >= pontos.length) {
            throw new IllegalArgumentException("ID do cliente inválido");
        }

        float pontosGanhos = 100 * (15 - tempoPercorrido);
        pontos[idCliente] += pontosGanhos;
    }

    public float getPontos(int idCliente) {
        return pontos[idCliente];
    }

    public String[] getJogadores() {
        return jogadores;
    }
}