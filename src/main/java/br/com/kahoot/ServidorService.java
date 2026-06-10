package br.com.kahoot;

import java.io.PrintWriter;
import java.net.Socket;

public class ServidorService {

    static final String MENSAGEM_BOAS_VINDAS = "Bem-vindo ao MiniKahoot!";

    private final BancoDePerguntas bancoDePerguntas;

    public ServidorService() {
        this(new BancoDePerguntas());
    }

    public ServidorService(BancoDePerguntas bancoDePerguntas) {
        if (bancoDePerguntas == null) {
            throw new IllegalArgumentException("Banco de perguntas nao pode ser nulo");
        }
        this.bancoDePerguntas = bancoDePerguntas;
    }

    public void atenderCliente(Socket socket) throws Exception {
        try (Socket cliente = socket;
             PrintWriter out = new PrintWriter(cliente.getOutputStream(), true)) {
            enviarBoasVindas(out);
            enviarPerguntaFormatada(out, bancoDePerguntas.obterPergunta(0));
            out.println("FIM");
        }
    }

    public void enviarBoasVindas(Socket socket) throws Exception {
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        enviarBoasVindas(out);
    }

    void enviarBoasVindas(PrintWriter out) {
        out.println("BEM_VINDO|" + MENSAGEM_BOAS_VINDAS);
    }

    void enviarPerguntaFormatada(PrintWriter out, Pergunta pergunta) {
        out.println("PERGUNTA|" + pergunta.getEnunciado());

        String[] alternativas = pergunta.getAlternativas();
        for (int i = 0; i < alternativas.length; i++) {
            out.println("ALT|" + (i + 1) + "|" + alternativas[i]);
        }

        out.println("FIM_PERGUNTA");
    }
}
