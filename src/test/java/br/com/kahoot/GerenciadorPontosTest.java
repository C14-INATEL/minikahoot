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
}