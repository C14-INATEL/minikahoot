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

import org.junit.jupiter.api.Test;

class ServidorServiceTest {

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
    void deveEnviarCincoPerguntasEAcumularPontuacaoQuandoTodasForemCorretas() throws Exception {
        Socket socket = mock(Socket.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("3\n2\n2\n2\n2\n".getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        when(socket.getInputStream()).thenReturn(inputStream);
        when(socket.getOutputStream()).thenReturn(outputStream);

        new ServidorService().atenderCliente(socket);

        String protocoloEsperado = String.join(System.lineSeparator(),
                "BEM_VINDO|Bem-vindo ao MiniKahoot!",
                "PERGUNTA|Qual estrutura armazena pares chave-valor em Java?",
                "ALT|1|List",
                "ALT|2|Set",
                "ALT|3|Map",
                "ALT|4|Queue",
                "FIM_PERGUNTA",
                "RESPONDA",
                "RESULTADO|ACERTO",
                "PONTOS|1500",
                "PERGUNTA|Qual protocolo e usado normalmente para paginas web?",
                "ALT|1|FTP",
                "ALT|2|HTTP",
                "ALT|3|SSH",
                "ALT|4|SMTP",
                "FIM_PERGUNTA",
                "RESPONDA",
                "RESULTADO|ACERTO",
                "PONTOS|3000",
                "PERGUNTA|Qual palavra-chave cria uma heranca em Java?",
                "ALT|1|implements",
                "ALT|2|extends",
                "ALT|3|import",
                "ALT|4|package",
                "FIM_PERGUNTA",
                "RESPONDA",
                "RESULTADO|ACERTO",
                "PONTOS|4500",
                "PERGUNTA|Qual comando executa os testes de um projeto Maven?",
                "ALT|1|mvn clean compile",
                "ALT|2|mvn test",
                "ALT|3|mvn package",
                "ALT|4|java -jar",
                "FIM_PERGUNTA",
                "RESPONDA",
                "RESULTADO|ACERTO",
                "PONTOS|6000",
                "PERGUNTA|Qual classe e usada para criar uma conexao TCP no cliente em Java?",
                "ALT|1|ServerSocket",
                "ALT|2|Socket",
                "ALT|3|DatagramSocket",
                "ALT|4|URL",
                "FIM_PERGUNTA",
                "RESPONDA",
                "RESULTADO|ACERTO",
                "PONTOS|7500",
                "FIM");

        String mensagem = outputStream.toString().trim();
        assertEquals(protocoloEsperado, mensagem);
    }

    @Test
    void deveManterPontuacaoZeroQuandoTodasAsRespostasForemIncorretas() throws Exception {
        Socket socket = mock(Socket.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("1\n1\n1\n1\n1\n".getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        when(socket.getInputStream()).thenReturn(inputStream);
        when(socket.getOutputStream()).thenReturn(outputStream);

        new ServidorService().atenderCliente(socket);

        String mensagem = outputStream.toString().trim();
        assertEquals(5, contarOcorrencias(mensagem, "RESULTADO|ERRO"));
        assertEquals(5, contarOcorrencias(mensagem, "PONTOS|0"));
    }

    @Test
    void deveTratarRespostaNaoNumericaSemInterromperAsDemaisPerguntas() throws Exception {
        Socket socket = mock(Socket.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("abc\n2\nx\n2\n2\n".getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        when(socket.getInputStream()).thenReturn(inputStream);
        when(socket.getOutputStream()).thenReturn(outputStream);

        new ServidorService().atenderCliente(socket);

        String mensagem = outputStream.toString().trim();
        assertEquals(2, contarOcorrencias(mensagem, "RESULTADO|ERRO"));
        assertEquals(3, contarOcorrencias(mensagem, "RESULTADO|ACERTO"));
        assertEquals(true, mensagem.endsWith("FIM"));
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

        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream("3\n2\n2\n2\n2\n".getBytes()));
        when(socket.getOutputStream()).thenThrow(new RuntimeException("Erro simulado"));

        ServidorService service = new ServidorService();

        assertThrows(RuntimeException.class, () -> service.atenderCliente(socket));

        verify(socket).getOutputStream();
    }

    @Test
    void deveFecharSocketAposAtendimento() throws Exception {
        Socket socket = mock(Socket.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("3\n2\n2\n2\n2\n".getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        when(socket.getInputStream()).thenReturn(inputStream);
        when(socket.getOutputStream()).thenReturn(outputStream);

        new ServidorService().atenderCliente(socket);

        verify(socket, times(1)).close();
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
