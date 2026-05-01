package br.com.kahoot;

import java.io.PrintWriter;
import java.net.Socket;

public class ServidorService {

    public void enviarBoasVindas(Socket socket) throws Exception {
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println("Bem-vindo ao MiniKahoot!");
    }
}
