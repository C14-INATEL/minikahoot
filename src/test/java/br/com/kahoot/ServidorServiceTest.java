package br.com.kahoot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ServidorServiceTest {

    @TempDir
    Path pastaTemporaria;

    @Test
    void deveEnviarMensagemDeBoasVindasAoSocket() throws Exception {
        Socket socket = mock(Socket.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        when(socket.getOutputStream()).thenReturn(outputStream);

        new ServidorService().enviarBoasVindas(socket);

        String mensagem = outputStream.toString().trim();
        assertEquals("BEM_VINDO|" + ServidorService.MENSAGEM_BOAS_VINDAS, mensagem);
    }

    @Test
    void deveSolicitarNomeEnviarCincoPerguntasEApresentarRankingFinal() throws Exception {
        Socket socket = mock(Socket.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("Samuel\n1\n1\n1\n1\n1\n".getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        when(socket.getInputStream()).thenReturn(inputStream);
        when(socket.getOutputStream()).thenReturn(outputStream);

        ServidorService service = criarServiceComRankingTemporario();
        service.atenderCliente(socket);

        String mensagem = outputStream.toString().trim();
        assertEquals(1, contarOcorrencias(mensagem, "BEM_VINDO|Bem-vindo ao MiniKahoot!"));
        assertEquals(1, contarOcorrencias(mensagem, "NOME"));
        assertEquals(5, contarOcorrencias(mensagem, "PERGUNTA|"));
        assertEquals(5, contarOcorrencias(mensagem, "RESPONDA"));
        assertEquals(5, contarOcorrencias(mensagem, "RESULTADO|ACERTO"));
        assertEquals(true, mensagem.contains("RANKING_INICIO"));
        assertEquals(true, mensagem.contains("RANKING|1|Samuel|7500"));
        assertEquals(true, mensagem.endsWith("FIM"));
    }

    @Test
    void deveManterPontuacaoZeroQuandoTodasAsRespostasForemIncorretas() throws Exception {
        Socket socket = mock(Socket.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("Samuel\n2\n2\n2\n2\n2\n".getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        when(socket.getInputStream()).thenReturn(inputStream);
        when(socket.getOutputStream()).thenReturn(outputStream);

        ServidorService service = criarServiceComRankingTemporario();
        service.atenderCliente(socket);

        String mensagem = outputStream.toString().trim();
        assertEquals(5, contarOcorrencias(mensagem, "RESULTADO|ERRO"));
        assertEquals(5, contarOcorrencias(mensagem, "PONTOS|0"));
        assertEquals(true, mensagem.contains("RANKING|1|Samuel|0"));
    }

    @Test
    void deveTratarRespostaNaoNumericaSemInterromperAsDemaisPerguntas() throws Exception {
        Socket socket = mock(Socket.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("Samuel\nabc\n1\nx\n1\n1\n".getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        when(socket.getInputStream()).thenReturn(inputStream);
        when(socket.getOutputStream()).thenReturn(outputStream);

        ServidorService service = criarServiceComRankingTemporario();
        service.atenderCliente(socket);

        String mensagem = outputStream.toString().trim();
        assertEquals(2, contarOcorrencias(mensagem, "RESULTADO|ERRO"));
        assertEquals(3, contarOcorrencias(mensagem, "RESULTADO|ACERTO"));
        assertEquals(true, mensagem.contains("RANKING_INICIO"));
        assertEquals(true, mensagem.endsWith("FIM"));
    }

    @Test
    void deveUsarNomePadraoQuandoClienteNaoInformarNome() throws Exception {
        Socket socket = mock(Socket.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("\n1\n1\n1\n1\n1\n".getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        when(socket.getInputStream()).thenReturn(inputStream);
        when(socket.getOutputStream()).thenReturn(outputStream);

        ServidorService service = criarServiceComRankingTemporario();
        service.atenderCliente(socket);

        String mensagem = outputStream.toString().trim();
        assertEquals(true, mensagem.contains("RANKING|1|Jogador|7500"));
    }

    @Test
    void deveChamarOutputStreamApenasUmaVezAoEnviarBoasVindas() throws Exception {
        Socket socket = mock(Socket.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        when(socket.getOutputStream()).thenReturn(outputStream);

        new ServidorService().enviarBoasVindas(socket);

        verify(socket, times(1)).getOutputStream();
    }

    @Test
    void deveTratarErroDeConexaoSemMascararExcecao() throws Exception {
        Socket socket = mock(Socket.class);

        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream("Samuel\n1\n1\n1\n1\n1\n".getBytes()));
        when(socket.getOutputStream()).thenThrow(new RuntimeException("Erro simulado"));

        ServidorService service = criarServiceComRankingTemporario();

        assertThrows(RuntimeException.class, () -> service.atenderCliente(socket));

        verify(socket).getOutputStream();
    }

    @Test
    void deveFecharSocketAposAtendimento() throws Exception {
        Socket socket = mock(Socket.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("Samuel\n1\n1\n1\n1\n1\n".getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        when(socket.getInputStream()).thenReturn(inputStream);
        when(socket.getOutputStream()).thenReturn(outputStream);

        ServidorService service = criarServiceComRankingTemporario();
        service.atenderCliente(socket);

        verify(socket, times(1)).close();
    }

    private ServidorService criarServiceComRankingTemporario() {
        BancoDePerguntas banco = new BancoDePerguntas();
        banco.limpar();

        for (int i = 1; i <= 5; i++) {
            banco.adicionarPergunta(new Pergunta(
                    "Pergunta de teste " + i + "?",
                    new String[]{"Correta", "Errada A", "Errada B", "Errada C"},
                    0
            ));
        }

        return new ServidorService(
                banco,
                new GerenciadorDePontos(new String[]{"Jogador"}, 1),
                new RankingGeral(pastaTemporaria.resolve("ranking.txt"))
        );
    }

    private int contarOcorrencias(String texto, String trecho) {
        int total = 0;
        int indice = 0;

        while ((indice = texto.indexOf(trecho, indice)) != -1) {
            total++;
            indice += trecho.length();
        }

        return total;
    }
}
