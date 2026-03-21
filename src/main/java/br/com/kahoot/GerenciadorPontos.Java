package br.com.kahoot;
// O servidor devia usar isso no comeco de um jogo
    public class GerenciadorPontos {
        // A posicao do cliente equivale a posicao dos pontos do mesmo
        private String jogadores[];
        private float pontos[];

        // Tem que ser inicializado no comeco da partida (porque vai ter os jogadores e etc.)
        public GerenciadorPontos(String[] jogadores, int numJogadores) {
            this.jogadores = jogadores;
            for(int i = 0; i < numJogadores; i++){
                pontos[i] = 0;
            }
        }

        public void adicionarPontos(int idCliente, float tempoPercorrido){
            float pontosGanhos;
            pontosGanhos = 100 * (15 - tempoPercorrido);
            pontos[idCliente] += pontosGanhos;
        }

        public String[] getJogadores() {
            return jogadores;
        }

        public void listarJogadoresPontos(){
            for(int i = 0; i < jogadores.length; i++){
                System.out.println("- " + jogadores[i] + " : " + pontos[i] + "\n");
            }
        }

    }