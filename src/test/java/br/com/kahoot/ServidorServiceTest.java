package br.com.kahoot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    void deveEnviarPerguntaFormatada() throws Exception {
        Socket socket = mock(Socket.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

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
                "FIM");

        String mensagem = outputStream.toString().trim();
        assertEquals(protocoloEsperado, mensagem);
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

        when(socket.getOutputStream()).thenThrow(new RuntimeException("Erro simulado"));

        ServidorService service = new ServidorService();

        assertThrows(RuntimeException.class, () -> service.atenderCliente(socket));

        verify(socket).getOutputStream();
    }

    @Test
    void deveFecharSocketAposAtendimento() throws Exception {
        Socket socket = mock(Socket.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        when(socket.getOutputStream()).thenReturn(outputStream);

        new ServidorService().atenderCliente(socket);

        verify(socket, times(1)).close();
    }
}
