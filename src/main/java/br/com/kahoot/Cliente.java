package br.com.kahoot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class Cliente {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 12345);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            String mensagem = in.readLine();
            System.out.println("Servidor disse: " + mensagem);

            socket.close();

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}