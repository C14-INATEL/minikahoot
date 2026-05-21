package br.com.kahoot;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GerenciadorPontosTest {

    @Test
    void deveIniciarComZeroPontos() {
        String[] jogadores = {"Rafa", "João"};
        GerenciadorDePontos gp = new GerenciadorDePontos(jogadores, 2);

        assertEquals(0, gp.getPontos(0));
        assertEquals(0, gp.getPontos(1));
    }

    @Test
    void deveAdicionarPontosCorretamente() {
        String[] jogadores = {"Rafa"};
        GerenciadorDePontos gp = new GerenciadorDePontos(jogadores, 1);

        gp.adicionarPontos(0, 5); // 100 * (15 - 5) = 1000

        assertEquals(1000, gp.getPontos(0));
    }

    @Test
    void deveDarMaisPontosParaRespostaRapida() {
        String[] jogadores = {"Rafa"};

        GerenciadorDePontos gp1 = new GerenciadorDePontos(jogadores, 1);
        gp1.adicionarPontos(0, 2);
        float rapido = gp1.getPontos(0);

        GerenciadorDePontos gp2 = new GerenciadorDePontos(jogadores, 1);
        gp2.adicionarPontos(0, 10);
        float lento = gp2.getPontos(0);

        assertTrue(rapido > lento);
    }

    @Test
    void deveLancarErroParaIdInvalido() {
        String[] jogadores = {"Rafa"};
        GerenciadorDePontos gp = new GerenciadorDePontos(jogadores, 1);

        assertThrows(IllegalArgumentException.class, () -> {
            gp.adicionarPontos(5, 5);
        });
    }

    @Test
    void deveAcumularPontosCorretamente() {
        String[] jogadores = {"Samuel"};
        GerenciadorDePontos gp = new GerenciadorDePontos(jogadores, 1);

        gp.adicionarPontos(0, 5);
        gp.adicionarPontos(0, 3);

        float esperado = (100 * (15 - 5)) + (100 * (15 - 3));
        assertEquals(esperado, gp.getPontos(0));
    }

    @Test
    void devePermitirPontuacaoNegativaQuandoTempoExcedeLimite() {
        String[] jogadores = {"Samuel"};
        GerenciadorDePontos gp = new GerenciadorDePontos(jogadores, 1);

        gp.adicionarPontos(0, 20);

        float esperado = 100 * (15 - 20);
        assertEquals(esperado, gp.getPontos(0));
    }

    @Test
    void deveRetornarListaDeJogadores() {
        String[] jogadores = {"Ana", "Carlos"};
        GerenciadorDePontos gp = new GerenciadorDePontos(jogadores, 2);

        assertArrayEquals(jogadores, gp.getJogadores());
    }

    @Test
    void deveManterPontuacoesIndependentesEntreJogadores() {
        String[] jogadores = {"Ana", "Carlos"};
        GerenciadorDePontos gp = new GerenciadorDePontos(jogadores, 2);

        gp.adicionarPontos(0, 5); // Ana
        gp.adicionarPontos(1, 10); // Carlos

        float pontosAna = 100 * (15 - 5);
        float pontosCarlos = 100 * (15 - 10);

        assertEquals(pontosAna, gp.getPontos(0));
        assertEquals(pontosCarlos, gp.getPontos(1));
}

}