package br.com.kahoot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class Cliente {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()))) {

            String mensagem;
            while ((mensagem = in.readLine()) != null) {
                System.out.println("Servidor disse: " + mensagem);

                if ("FIM".equals(mensagem)) {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
