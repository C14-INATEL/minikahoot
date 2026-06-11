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
                System.out.println("Servidor disse: " + mensagem);

                if ("RESPONDA".equals(mensagem)) {
                    System.out.print("Digite sua resposta: ");
                    out.println(scanner.nextLine());
                    continue;
                }

                if ("FIM".equals(mensagem)) {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
