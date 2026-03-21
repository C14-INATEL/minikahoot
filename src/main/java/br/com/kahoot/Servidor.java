package br.com.kahoot;

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
            out.println("Bem-vindo ao MiniKahoot!");

            cliente.close();
            server.close();

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}