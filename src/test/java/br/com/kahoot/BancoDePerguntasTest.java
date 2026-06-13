package br.com.kahoot;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Test;

class BancoDePerguntasTest {

    @Test
    void deveCarregarTrintaPerguntasIniciais() {
        BancoDePerguntas perguntas = new BancoDePerguntas();

        assertEquals(30, perguntas.getTotalPerguntas());
        for (int i = 0; i < perguntas.getTotalPerguntas(); i++) {
            assertNotNull(perguntas.obterPergunta(i));
        }
    }

    @Test
    void deveObterPerguntaPorIndiceValido() {
        BancoDePerguntas perguntas = new BancoDePerguntas();

        Pergunta pergunta = perguntas.obterPergunta(1);

        assertNotNull(pergunta);
        assertEquals(1, pergunta.getRespostaCorreta());
        assertArrayEquals(new String[]{"FTP", "HTTP", "SSH", "SMTP"}, pergunta.getAlternativas());
    }

    @Test
    void deveAdicionarPerguntaValida() {
        BancoDePerguntas perguntas = new BancoDePerguntas();
        int totalInicial = perguntas.getTotalPerguntas();

        perguntas.adicionarPergunta(new Pergunta(
                "Qual comando compila um projeto Maven?",
                new String[]{"mvn test", "mvn clean compile", "java -jar", "docker build"},
                1
        ));

        assertEquals(totalInicial + 1, perguntas.getTotalPerguntas());
    }

    @Test
    void deveRetornarCincoPerguntasAleatoriasSemRepeticao() {
        BancoDePerguntas perguntas = new BancoDePerguntas(new Random(0));

        List<Pergunta> perguntasAleatorias = perguntas.obterPerguntasAleatorias(5);
        Set<Pergunta> perguntasUnicas = new HashSet<>(perguntasAleatorias);

        assertEquals(5, perguntasAleatorias.size());
        assertEquals(5, perguntasUnicas.size());
        assertTrue(perguntas.obterTodas().containsAll(perguntasAleatorias));
    }

    @Test
    void naoDevePermitirQuantidadeInvalidaAoSortearPerguntas() {
        BancoDePerguntas perguntas = new BancoDePerguntas();

        assertThrows(IllegalArgumentException.class, () -> perguntas.obterPerguntasAleatorias(0));
        assertThrows(IllegalArgumentException.class, () -> perguntas.obterPerguntasAleatorias(31));
    }

    @Test
    void naoDeveAdicionarPerguntaNula() {
        BancoDePerguntas perguntas = new BancoDePerguntas();

        assertThrows(IllegalArgumentException.class, () -> perguntas.adicionarPergunta(null));
    }

    @Test
    void naoDeveObterPerguntaComIndiceInvalido() {
        BancoDePerguntas perguntas = new BancoDePerguntas();

        assertThrows(IllegalArgumentException.class, () -> perguntas.obterPergunta(-1));
        assertThrows(IllegalArgumentException.class, () -> perguntas.obterPergunta(perguntas.getTotalPerguntas()));
    }

    @Test
    void deveRetornarListaDePerguntasImutavel() {
        BancoDePerguntas perguntas = new BancoDePerguntas();
        List<Pergunta> lista = perguntas.obterTodas();

        assertThrows(UnsupportedOperationException.class, () -> lista.add(
                new Pergunta("Nova pergunta?", new String[]{"Sim", "Nao"}, 0)
        ));
    }

    @Test
    void deveLimparBancoDePerguntas() {
        BancoDePerguntas perguntas = new BancoDePerguntas();

        perguntas.limpar();

        assertEquals(0, perguntas.getTotalPerguntas());
    }
}
