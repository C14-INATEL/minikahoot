package br.com.kahoot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

class PerguntasTest {

    @Test
    void deveCarregarBancoInicialOrganizado() {
        Perguntas perguntas = new Perguntas();

        assertEquals(3, perguntas.getTotalPerguntas());
        assertNotNull(perguntas.obterPergunta(0));
        assertNotNull(perguntas.obterPergunta(1));
        assertNotNull(perguntas.obterPergunta(2));
    }

    @Test
    void deveAdicionarPerguntaValida() {
        Perguntas perguntas = new Perguntas();
        int totalInicial = perguntas.getTotalPerguntas();

        perguntas.adicionarPergunta(new Pergunta(
                "Qual comando compila um projeto Maven?",
                new String[]{"mvn test", "mvn clean compile", "java -jar", "docker build"},
                1
        ));

        assertEquals(totalInicial + 1, perguntas.getTotalPerguntas());
    }

    @Test
    void naoDeveAdicionarPerguntaNula() {
        Perguntas perguntas = new Perguntas();

        assertThrows(IllegalArgumentException.class, () -> perguntas.adicionarPergunta(null));
    }

    @Test
    void naoDeveObterPerguntaComIndiceInvalido() {
        Perguntas perguntas = new Perguntas();

        assertThrows(IllegalArgumentException.class, () -> perguntas.obterPergunta(-1));
        assertThrows(IllegalArgumentException.class, () -> perguntas.obterPergunta(perguntas.getTotalPerguntas()));
    }

    @Test
    void deveRetornarListaDePerguntasImutavel() {
        Perguntas perguntas = new Perguntas();
        List<Pergunta> lista = perguntas.obterTodas();

        assertThrows(UnsupportedOperationException.class, () -> lista.add(
                new Pergunta("Nova pergunta?", new String[]{"Sim", "Nao"}, 0)
        ));
    }

    @Test
    void deveLimparBancoDePerguntas() {
        Perguntas perguntas = new Perguntas();

        perguntas.limpar();

        assertEquals(0, perguntas.getTotalPerguntas());
    }
}
