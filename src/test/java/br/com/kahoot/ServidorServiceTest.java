package br.com.kahoot;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ServidorServiceTest {

    @Test
    void deveEnviarMensagemDeBoasVindasAoSocket() throws Exception {
        Socket socket = mock(Socket.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        when(socket.getOutputStream()).thenReturn(outputStream);

        new ServidorService().enviarBoasVindas(socket);

        String mensagem = outputStream.toString().trim();
        assertEquals("Bem-vindo ao MiniKahoot!", mensagem);
    }

    @Test
    void naoDeveEnviarMensagemVazia() throws Exception {
        Socket socket = mock(Socket.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        when(socket.getOutputStream()).thenReturn(outputStream);

        new ServidorService().enviarBoasVindas(socket);

        String mensagem = outputStream.toString().trim();

        assertFalse(mensagem.isEmpty());
    }

    @Test
    void deveChamarOutputStreamApenasUmaVez() throws Exception {
        Socket socket = mock(Socket.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        when(socket.getOutputStream()).thenReturn(outputStream);

        new ServidorService().enviarBoasVindas(socket);

        verify(socket, times(1)).getOutputStream();
    }

    @Test
    void deveLancarExcecaoQuandoOutputStreamFalhar() throws Exception {
        Socket socket = mock(Socket.class);

        when(socket.getOutputStream()).thenThrow(new RuntimeException("Erro simulado"));

        ServidorService service = new ServidorService();

        assertThrows(RuntimeException.class, () -> {
            service.enviarBoasVindas(socket);
        });

        verify(socket).getOutputStream();
    }

    @Test
    void deveAtenderMultiplosClientes() throws Exception {

        ServidorService serviceMock = mock(ServidorService.class);

        Socket socket1 = mock(Socket.class);
        Socket socket2 = mock(Socket.class);

        // Simulando duas conexões
        serviceMock.enviarBoasVindas(socket1);
        serviceMock.enviarBoasVindas(socket2);

        verify(serviceMock, times(1)).enviarBoasVindas(socket1);
        verify(serviceMock, times(1)).enviarBoasVindas(socket2);
    }
}
