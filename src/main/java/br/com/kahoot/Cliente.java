package br.com.kahoot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            String mensagem;
            while ((mensagem = in.readLine()) != null) {
                if ("NOME".equals(mensagem)) {
                    System.out.print("Digite seu nome: ");
                    out.println(scanner.nextLine());
                    continue;
                }

                if ("RESPONDA".equals(mensagem)) {
                    System.out.print("Digite sua resposta (ex: 2): ");
                    out.println(scanner.nextLine());
                    continue;
                }

                if ("FIM".equals(mensagem)) {
                    break;
                }

                exibirMensagem(mensagem);
            }
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void exibirMensagem(String mensagem) {
        String[] partes = mensagem.split("\\|");

        if (partes.length == 0) {
            System.out.println(mensagem);
            return;
        }

        switch (partes[0]) {
            case "BEM_VINDO":
            case "PERGUNTA":
            case "RESULTADO":
            case "PONTOS":
                if (partes.length > 1) {
                    System.out.println(partes[1]);
                }
                break;
            case "ALT":
                if (partes.length > 2) {
                    System.out.println(partes[1] + ") " + partes[2]);
                }
                break;
            case "RANKING_INICIO":
                System.out.println("Ranking geral:");
                break;
            case "RANKING":
                if (partes.length > 3) {
                    System.out.println(partes[1] + ". " + partes[2] + " - " + partes[3] + " pontos");
                }
                break;
            case "FIM_PERGUNTA":
            case "RANKING_FIM":
                break;
            default:
                System.out.println(mensagem);
                break;
        }
    }
}
