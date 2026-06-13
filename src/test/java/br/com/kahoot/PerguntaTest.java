package br.com.kahoot;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PerguntaTest {

    @Test
    void deveCriarPerguntaValida() {
        String[] alternativas = {"Sao Paulo", "Brasilia", "Rio de Janeiro"};

        Pergunta pergunta = new Pergunta("Qual e a capital do Brasil?", alternativas, 1);

        assertEquals("Qual e a capital do Brasil?", pergunta.getEnunciado());
        assertArrayEquals(alternativas, pergunta.getAlternativas());
        assertEquals(1, pergunta.getRespostaCorreta());
        assertTrue(pergunta.verificarResposta(1));
        assertFalse(pergunta.verificarResposta(0));
    }

    @Test
    void naoDeveCriarPerguntaComEnunciadoVazio() {
        String[] alternativas = {"Azul", "Verde"};

        assertThrows(IllegalArgumentException.class, () -> new Pergunta("   ", alternativas, 0));
        assertThrows(IllegalArgumentException.class, () -> new Pergunta(null, alternativas, 0));
    }

    @Test
    void naoDeveCriarPerguntaSemAlternativas() {
        assertThrows(IllegalArgumentException.class, () -> new Pergunta("Pergunta?", null, 0));
        assertThrows(IllegalArgumentException.class, () -> new Pergunta("Pergunta?", new String[0], 0));
        assertThrows(IllegalArgumentException.class, () -> new Pergunta("Pergunta?", new String[]{"Unica"}, 0));
    }

    @Test
    void naoDeveCriarPerguntaComAlternativaVazia() {
        assertThrows(IllegalArgumentException.class,
                () -> new Pergunta("Pergunta?", new String[]{"", "Opcao valida"}, 1));
        assertThrows(IllegalArgumentException.class,
                () -> new Pergunta("Pergunta?", new String[]{"   ", "Opcao valida"}, 1));
        assertThrows(IllegalArgumentException.class,
                () -> new Pergunta("Pergunta?", new String[]{null, "Opcao valida"}, 1));
    }

    @Test
    void naoDeveCriarPerguntaComAlternativasDuplicadas() {
        assertThrows(IllegalArgumentException.class,
                () -> new Pergunta("Pergunta?", new String[]{"Java", "Java"}, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new Pergunta("Pergunta?", new String[]{"Java", " java "}, 0));
    }

    @Test
    void naoDeveAceitarRespostaForaDoIntervalo() {
        String[] alternativas = {"Java", "Python", "C#"};

        assertThrows(IllegalArgumentException.class, () -> new Pergunta("Linguagem?", alternativas, -1));
        assertThrows(IllegalArgumentException.class, () -> new Pergunta("Linguagem?", alternativas, 3));
    }

    @Test
    void deveRetornarAlternativasImutaveis() {
        String[] alternativas = {"Linux", "Windows"};
        Pergunta pergunta = new Pergunta("Sistema operacional?", alternativas, 0);

        String[] alternativasRecebidas = pergunta.getAlternativas();
        alternativasRecebidas[0] = "MacOS";
        alternativas[1] = "BSD";

        assertArrayEquals(new String[]{"Linux", "Windows"}, pergunta.getAlternativas());
    }

    @Test
    void deveFormatarPerguntaParaEnvio() {
        Pergunta pergunta = new Pergunta(
                "Quanto e 2 + 2?",
                new String[]{"3", "4", "5"},
                1
        );

        String esperado = """
                Quanto e 2 + 2?
                1) 3
                2) 4
                3) 5
                """;

        assertEquals(esperado, pergunta.formatarParaEnvio());
        assertEquals(esperado, pergunta.toString());
    }
}
