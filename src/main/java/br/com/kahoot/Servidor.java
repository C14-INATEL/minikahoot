package br.com.kahoot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(12345);
            System.out.println("Servidor iniciado...");

            Socket cliente = server.accept();
            System.out.println("Cliente conectado!");

            PrintWriter out = new PrintWriter(cliente.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));

            out.println("Bem-vindo ao MiniKahoot!");

            // Criar perguntas
            Perguntas perguntas = new Perguntas();
            perguntas.adicionarPergunta(new Pergunta("Qual é a capital do Brasil?", new String[]{"São Paulo", "Rio de Janeiro", "Brasília", "Belo Horizonte"}, 2));
            perguntas.adicionarPergunta(new Pergunta("Quanto é 2 + 2?", new String[]{"3", "4", "5", "6"}, 1));
            perguntas.adicionarPergunta(new Pergunta("Qual linguagem usamos?", new String[]{"Python", "Java", "C++", "JavaScript"}, 1));

            GerenciadorPontos gp = new GerenciadorPontos(new String[]{"Jogador1"}, 1);

            for (int i = 0; i < perguntas.getTotalPerguntas(); i++) {
                Pergunta p = perguntas.obterPergunta(i);
                out.println("PERGUNTA:" + p.getEnunciado());
                for (int j = 0; j < p.getAlternativas().length; j++) {
                    out.println("ALT:" + (j + 1) + ":" + p.getAlternativas()[j]);
                }
                out.println("FIM_PERGUNTA");

                // Ler resposta
                String respostaStr = in.readLine();
                if (respostaStr != null) {
                    try {
                        int resposta = Integer.parseInt(respostaStr) - 1; // 1-based to 0-based
                        if (p.verificarResposta(resposta)) {
                            gp.adicionarPontos(0, 5.0f); // Tempo fixo para exemplo
                            out.println("CORRETO:" + gp.getPontos(0));
                        } else {
                            out.println("ERRADO:" + gp.getPontos(0));
                        }
                    } catch (NumberFormatException e) {
                        out.println("RESPOSTA_INVALIDA");
                    }
                }
            }

            out.println("FIM_JOGO:" + gp.getPontos(0));

            cliente.close();
            server.close();

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}