package br.com.kahoot;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GerenciadorPontosTest {

    @Test
    void deveIniciarComZeroPontos() {
        String[] jogadores = {"Rafa", "João"};
        GerenciadorPontos gp = new GerenciadorPontos(jogadores, 2);

        assertEquals(0, gp.getPontos(0));
        assertEquals(0, gp.getPontos(1));
    }

    @Test
    void deveAdicionarPontosCorretamente() {
        String[] jogadores = {"Rafa"};
        GerenciadorPontos gp = new GerenciadorPontos(jogadores, 1);

        gp.adicionarPontos(0, 5); // 100 * (15 - 5) = 1000

        assertEquals(1000, gp.getPontos(0));
    }

    @Test
    void deveDarMaisPontosParaRespostaRapida() {
        String[] jogadores = {"Rafa"};

        GerenciadorPontos gp1 = new GerenciadorPontos(jogadores, 1);
        gp1.adicionarPontos(0, 2);
        float rapido = gp1.getPontos(0);

        GerenciadorPontos gp2 = new GerenciadorPontos(jogadores, 1);
        gp2.adicionarPontos(0, 10);
        float lento = gp2.getPontos(0);

        assertTrue(rapido > lento);
    }

    @Test
    void deveLancarErroParaIdInvalido() {
        String[] jogadores = {"Rafa"};
        GerenciadorPontos gp = new GerenciadorPontos(jogadores, 1);

        assertThrows(IllegalArgumentException.class, () -> {
            gp.adicionarPontos(5, 5);
        });
    }

    @Test
    void deveAcumularPontosCorretamente() {
        String[] jogadores = {"Samuel"};
        GerenciadorPontos gp = new GerenciadorPontos(jogadores, 1);

        gp.adicionarPontos(0, 5);
        gp.adicionarPontos(0, 3);

        float esperado = (100 * (15 - 5)) + (100 * (15 - 3));
        assertEquals(esperado, gp.getPontos(0));
    }

    @Test
    void devePermitirPontuacaoNegativaQuandoTempoExcedeLimite() {
        String[] jogadores = {"Samuel"};
        GerenciadorPontos gp = new GerenciadorPontos(jogadores, 1);

        gp.adicionarPontos(0, 20);

        float esperado = 100 * (15 - 20);
        assertEquals(esperado, gp.getPontos(0));
    }

    @Test
    void deveRetornarListaDeJogadores() {
        String[] jogadores = {"Ana", "Carlos"};
        GerenciadorPontos gp = new GerenciadorPontos(jogadores, 2);

        assertArrayEquals(jogadores, gp.getJogadores());
    }

    @Test
    void deveManterPontuacoesIndependentesEntreJogadores() {
        String[] jogadores = {"Ana", "Carlos"};
        GerenciadorPontos gp = new GerenciadorPontos(jogadores, 2);

        gp.adicionarPontos(0, 5); // Ana
        gp.adicionarPontos(1, 10); // Carlos

        float pontosAna = 100 * (15 - 5);
        float pontosCarlos = 100 * (15 - 10);

        assertEquals(pontosAna, gp.getPontos(0));
        assertEquals(pontosCarlos, gp.getPontos(1));
}

}