package br.com.kahoot;

import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    private static final int PORTA = 12345;

    public static void main(String[] args) {
        ServidorService service = new ServidorService();

        try (ServerSocket server = new ServerSocket(PORTA)) {
            System.out.println("Servidor iniciado...");

            Socket cliente = server.accept();
            System.out.println("Cliente conectado!");

            service.atenderCliente(cliente);
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
