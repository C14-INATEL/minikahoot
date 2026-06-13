package br.com.kahoot;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RankingGeral {

    private static final String SEPARADOR = ";";
    private final Path caminhoArquivo;

    public RankingGeral() {
        this(Paths.get("ranking_geral.txt"));
    }

    public RankingGeral(Path caminhoArquivo) {
        if (caminhoArquivo == null) {
            throw new IllegalArgumentException("Caminho do ranking nao pode ser nulo");
        }
        this.caminhoArquivo = caminhoArquivo;
    }

    public void registrarPontuacao(String jogador, float pontos) throws IOException {
        validarJogador(jogador);

        Map<String, Float> ranking = carregarPontuacoes();
        ranking.merge(jogador.trim(), pontos, Math::max);
        salvarPontuacoes(ranking);
    }

    public List<Map.Entry<String, Float>> obterRankingOrdenado() throws IOException {
        List<Map.Entry<String, Float>> ranking = new ArrayList<>(carregarPontuacoes().entrySet());
        ranking.sort(Comparator
                .comparing(Map.Entry<String, Float>::getValue).reversed()
                .thenComparing(Map.Entry::getKey));
        return ranking;
    }

    private Map<String, Float> carregarPontuacoes() throws IOException {
        Map<String, Float> ranking = new LinkedHashMap<>();
        if (!Files.exists(caminhoArquivo)) {
            return ranking;
        }

        List<String> linhas = Files.readAllLines(caminhoArquivo, StandardCharsets.UTF_8);
        for (String linha : linhas) {
            if (linha == null || linha.trim().isEmpty()) {
                continue;
            }

            String[] partes = linha.split(SEPARADOR, 2);
            if (partes.length != 2) {
                continue;
            }

            ranking.put(partes[0], Float.parseFloat(partes[1]));
        }

        return ranking;
    }

    private void salvarPontuacoes(Map<String, Float> ranking) throws IOException {
        List<String> linhas = new ArrayList<>();
        for (Map.Entry<String, Float> entrada : ranking.entrySet()) {
            linhas.add(entrada.getKey() + SEPARADOR + entrada.getValue());
        }

        Path diretorio = caminhoArquivo.getParent();
        if (diretorio != null) {
            Files.createDirectories(diretorio);
        }

        Files.write(
                caminhoArquivo,
                linhas,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        );
    }

    private void validarJogador(String jogador) {
        if (jogador == null || jogador.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do jogador nao pode ser vazio");
        }
    }
}
