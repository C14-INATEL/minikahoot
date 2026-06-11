package br.com.kahoot;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GerenciadorDePontosTest {

    @Test
    void deveIniciarComZeroPontos() {
        String[] jogadores = {"Rafa", "Joao"};
        GerenciadorDePontos gp = new GerenciadorDePontos(jogadores, 2);

        assertEquals(0, gp.getPontos(0));
        assertEquals(0, gp.getPontos(1));
    }

    @Test
    void deveAdicionarPontosCorretamente() {
        String[] jogadores = {"Rafa"};
        GerenciadorDePontos gp = new GerenciadorDePontos(jogadores, 1);

        gp.adicionarPontos(0, 5);

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
    void deveLancarErroParaIdInvalidoAoAdicionarPontos() {
        String[] jogadores = {"Rafa"};
        GerenciadorDePontos gp = new GerenciadorDePontos(jogadores, 1);

        assertThrows(IllegalArgumentException.class, () -> gp.adicionarPontos(5, 5));
    }

    @Test
    void deveLancarErroAoBuscarPontosComIdInvalido() {
        String[] jogadores = {"Rafa"};
        GerenciadorDePontos gp = new GerenciadorDePontos(jogadores, 1);

        assertThrows(IllegalArgumentException.class, () -> gp.getPontos(-1));
        assertThrows(IllegalArgumentException.class, () -> gp.getPontos(2));
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
    void naoDevePermitirPontuacaoNegativaQuandoTempoExcedeLimite() {
        String[] jogadores = {"Samuel"};
        GerenciadorDePontos gp = new GerenciadorDePontos(jogadores, 1);

        gp.adicionarPontos(0, 20);

        assertEquals(0, gp.getPontos(0));
    }

    @Test
    void deveRetornarCopiaDefensivaDosJogadores() {
        String[] jogadores = {"Ana", "Carlos"};
        GerenciadorDePontos gp = new GerenciadorDePontos(jogadores, 2);

        String[] jogadoresRecebidos = gp.getJogadores();
        jogadoresRecebidos[0] = "Alterado";
        jogadores[1] = "Mudou";

        assertArrayEquals(new String[]{"Ana", "Carlos"}, gp.getJogadores());
    }

    @Test
    void deveManterPontuacoesIndependentesEntreJogadores() {
        String[] jogadores = {"Ana", "Carlos"};
        GerenciadorDePontos gp = new GerenciadorDePontos(jogadores, 2);

        gp.adicionarPontos(0, 5);
        gp.adicionarPontos(1, 10);

        float pontosAna = 100 * (15 - 5);
        float pontosCarlos = 100 * (15 - 10);

        assertEquals(pontosAna, gp.getPontos(0));
        assertEquals(pontosCarlos, gp.getPontos(1));
    }

    @Test
    void naoDeveCriarGerenciadorComJogadoresNulos() {
        assertThrows(IllegalArgumentException.class, () -> new GerenciadorDePontos(null, 1));
    }

    @Test
    void naoDeveCriarGerenciadorComNumeroDeJogadoresInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new GerenciadorDePontos(new String[]{"Ana"}, 0));
        assertThrows(IllegalArgumentException.class, () -> new GerenciadorDePontos(new String[]{"Ana"}, -1));
    }

    @Test
    void naoDeveCriarGerenciadorComQuantidadeDiferenteDoArrayDeJogadores() {
        assertThrows(IllegalArgumentException.class,
                () -> new GerenciadorDePontos(new String[]{"Ana", "Carlos"}, 1));
    }
}
