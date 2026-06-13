package br.com.kahoot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class ServidorService {

    static final String MENSAGEM_BOAS_VINDAS = "Bem-vindo ao MiniKahoot!";
    private static final String[] JOGADOR_PADRAO = {"Jogador"};

    private final BancoDePerguntas bancoDePerguntas;
    private final GerenciadorDePontos gerenciadorDePontos;
    private final RankingGeral rankingGeral;

    public ServidorService() {
        this(
                new BancoDePerguntas(),
                new GerenciadorDePontos(JOGADOR_PADRAO, JOGADOR_PADRAO.length),
                new RankingGeral()
        );
    }

    public ServidorService(BancoDePerguntas bancoDePerguntas) {
        this(
                bancoDePerguntas,
                new GerenciadorDePontos(JOGADOR_PADRAO, JOGADOR_PADRAO.length),
                new RankingGeral()
        );
    }

    public ServidorService(BancoDePerguntas bancoDePerguntas, GerenciadorDePontos gerenciadorDePontos) {
        this(bancoDePerguntas, gerenciadorDePontos, new RankingGeral());
    }

    public ServidorService(
            BancoDePerguntas bancoDePerguntas,
            GerenciadorDePontos gerenciadorDePontos,
            RankingGeral rankingGeral
    ) {
        if (bancoDePerguntas == null) {
            throw new IllegalArgumentException("Banco de perguntas nao pode ser nulo");
        }
        if (gerenciadorDePontos == null) {
            throw new IllegalArgumentException("Gerenciador de pontos nao pode ser nulo");
        }
        if (rankingGeral == null) {
            throw new IllegalArgumentException("Ranking geral nao pode ser nulo");
        }
        this.bancoDePerguntas = bancoDePerguntas;
        this.gerenciadorDePontos = gerenciadorDePontos;
        this.rankingGeral = rankingGeral;
    }

    public void atenderCliente(Socket socket) throws Exception {
        try (Socket cliente = socket;
             BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
             PrintWriter out = new PrintWriter(cliente.getOutputStream(), true)) {
            enviarBoasVindas(out);

            String nomeJogador = solicitarNomeDoJogador(out, in);

            for (Pergunta pergunta : bancoDePerguntas.obterTodas()) {
                enviarPerguntaFormatada(out, pergunta);
                out.println("RESPONDA");

                String respostaRecebida = in.readLine();
                processarResposta(out, pergunta, respostaRecebida);
            }

            float pontuacaoFinal = gerenciadorDePontos.getPontos(0);
            rankingGeral.registrarPontuacao(nomeJogador, pontuacaoFinal);

            List<Map.Entry<String, Float>> ranking = rankingGeral.obterRankingOrdenado();
            enviarRanking(out, ranking);
            exibirRankingNoServidor(ranking);

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

    private String solicitarNomeDoJogador(PrintWriter out, BufferedReader in) throws Exception {
        out.println("NOME");
        String nomeRecebido = in.readLine();

        if (nomeRecebido == null || nomeRecebido.trim().isEmpty()) {
            return JOGADOR_PADRAO[0];
        }

        return nomeRecebido.trim();
    }

    private void processarResposta(PrintWriter out, Pergunta pergunta, String respostaRecebida) {
        if (!respostaEhValida(respostaRecebida)) {
            enviarResultado(out, "ERRO", gerenciadorDePontos.getPontos(0));
            return;
        }

        int respostaCliente = Integer.parseInt(respostaRecebida.trim()) - 1;
        if (pergunta.verificarResposta(respostaCliente)) {
            gerenciadorDePontos.adicionarPontos(0, 0);
            enviarResultado(out, "ACERTO", gerenciadorDePontos.getPontos(0));
            return;
        }

        enviarResultado(out, "ERRO", gerenciadorDePontos.getPontos(0));
    }

    private boolean respostaEhValida(String respostaRecebida) {
        if (respostaRecebida == null || respostaRecebida.trim().isEmpty()) {
            return false;
        }

        try {
            Integer.parseInt(respostaRecebida.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void enviarResultado(PrintWriter out, String resultado, float pontos) {
        out.println("RESULTADO|" + resultado);
        out.println("PONTOS|" + formatarPontos(pontos));
    }

    private void enviarRanking(PrintWriter out, List<Map.Entry<String, Float>> ranking) {
        out.println("RANKING_INICIO");
        for (int i = 0; i < ranking.size(); i++) {
            Map.Entry<String, Float> entrada = ranking.get(i);
            out.println("RANKING|" + (i + 1) + "|" + entrada.getKey() + "|" + formatarPontos(entrada.getValue()));
        }
        out.println("RANKING_FIM");
    }

    private void exibirRankingNoServidor(List<Map.Entry<String, Float>> ranking) {
        System.out.println("Ranking geral:");
        for (int i = 0; i < ranking.size(); i++) {
            Map.Entry<String, Float> entrada = ranking.get(i);
            System.out.println((i + 1) + ". " + entrada.getKey() + " - " + formatarPontos(entrada.getValue()));
        }
    }

    private String formatarPontos(float pontos) {
        if (pontos == (int) pontos) {
            return String.valueOf((int) pontos);
        }
        return String.valueOf(pontos);
    }
}
