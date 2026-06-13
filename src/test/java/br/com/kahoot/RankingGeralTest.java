package br.com.kahoot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class RankingGeralTest {

    @TempDir
    Path pastaTemporaria;

    @Test
    void devePersistirMelhorPontuacaoDoJogador() throws Exception {
        RankingGeral ranking = new RankingGeral(pastaTemporaria.resolve("ranking.txt"));

        ranking.registrarPontuacao("Ana", 1500);
        ranking.registrarPontuacao("Ana", 1000);
        ranking.registrarPontuacao("Ana", 3000);

        List<Map.Entry<String, Float>> rankingOrdenado = ranking.obterRankingOrdenado();

        assertEquals(1, rankingOrdenado.size());
        assertEquals("Ana", rankingOrdenado.get(0).getKey());
        assertEquals(3000f, rankingOrdenado.get(0).getValue());
    }

    @Test
    void deveRetornarRankingOrdenadoPorMaiorPontuacao() throws Exception {
        RankingGeral ranking = new RankingGeral(pastaTemporaria.resolve("ranking.txt"));

        ranking.registrarPontuacao("Carlos", 2000);
        ranking.registrarPontuacao("Ana", 3500);
        ranking.registrarPontuacao("Bruno", 2500);

        List<Map.Entry<String, Float>> rankingOrdenado = ranking.obterRankingOrdenado();

        assertEquals("Ana", rankingOrdenado.get(0).getKey());
        assertEquals("Bruno", rankingOrdenado.get(1).getKey());
        assertEquals("Carlos", rankingOrdenado.get(2).getKey());
    }
}
