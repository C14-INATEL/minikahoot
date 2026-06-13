package br.com.kahoot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BancoDePerguntas {

    private final List<Pergunta> perguntas;
    private final Random random;

    public BancoDePerguntas() {
        this(new Random());
    }

    BancoDePerguntas(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Random nao pode ser nulo");
        }
        this.perguntas = new ArrayList<>();
        this.random = random;
        carregarPerguntasIniciais();
    }

    public void adicionarPergunta(Pergunta pergunta) {
        if (pergunta == null) {
            throw new IllegalArgumentException("Pergunta nao pode ser nula");
        }
        perguntas.add(pergunta);
    }

    public Pergunta obterPergunta(int indice) {
        if (indice < 0 || indice >= perguntas.size()) {
            throw new IllegalArgumentException("Indice invalido");
        }
        return perguntas.get(indice);
    }

    public int getTotalPerguntas() {
        return perguntas.size();
    }

    public List<Pergunta> obterTodas() {
        return Collections.unmodifiableList(new ArrayList<>(perguntas));
    }

    public List<Pergunta> obterPerguntasAleatorias(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        if (quantidade > perguntas.size()) {
            throw new IllegalArgumentException("Quantidade solicitada maior que o total de perguntas");
        }

        List<Pergunta> perguntasSorteadas = new ArrayList<>(perguntas);
        Collections.shuffle(perguntasSorteadas, random);
        return Collections.unmodifiableList(new ArrayList<>(perguntasSorteadas.subList(0, quantidade)));
    }

    public void limpar() {
        perguntas.clear();
    }

    private void carregarPerguntasIniciais() {
        adicionarPergunta(new Pergunta(
                "Qual estrutura armazena pares chave-valor em Java?",
                new String[]{"List", "Set", "Map", "Queue"},
                2
        ));
        adicionarPergunta(new Pergunta(
                "Qual protocolo e usado normalmente para paginas web?",
                new String[]{"FTP", "HTTP", "SSH", "SMTP"},
                1
        ));
        adicionarPergunta(new Pergunta(
                "Qual palavra-chave cria uma heranca em Java?",
                new String[]{"implements", "extends", "import", "package"},
                1
        ));
        adicionarPergunta(new Pergunta(
                "Qual comando executa os testes de um projeto Maven?",
                new String[]{"mvn clean compile", "mvn test", "mvn package", "java -jar"},
                1
        ));
        adicionarPergunta(new Pergunta(
                "Qual classe e usada para criar uma conexao TCP no cliente em Java?",
                new String[]{"ServerSocket", "Socket", "DatagramSocket", "URL"},
                1
        ));
        adicionarPergunta(new Pergunta(
                "Qual interface representa uma lista ordenada em Java?",
                new String[]{"Map", "List", "Set", "Deque"},
                1
        ));
        adicionarPergunta(new Pergunta(
                "Qual palavra-chave e usada para herdar de uma classe?",
                new String[]{"extends", "implements", "throws", "static"},
                0
        ));
        adicionarPergunta(new Pergunta(
                "Qual comando gera o pacote JAR em um projeto Maven?",
                new String[]{"mvn test", "mvn clean", "mvn package", "mvn verify"},
                2
        ));
        adicionarPergunta(new Pergunta(
                "Qual porta padrao e usada por HTTP?",
                new String[]{"21", "25", "80", "443"},
                2
        ));
        adicionarPergunta(new Pergunta(
                "Qual classe abre uma porta de escuta TCP no servidor?",
                new String[]{"Socket", "ServerSocket", "Scanner", "PrintWriter"},
                1
        ));
        adicionarPergunta(new Pergunta(
                "Qual colecao nao permite elementos duplicados em Java?",
                new String[]{"List", "Queue", "Set", "ArrayList"},
                2
        ));
        adicionarPergunta(new Pergunta(
                "Qual metodo inicia uma aplicacao Java pelo terminal?",
                new String[]{"run()", "main()", "start()", "execute()"},
                1
        ));
        adicionarPergunta(new Pergunta(
                "Qual protocolo e usado para envio de e-mails?",
                new String[]{"SMTP", "SSH", "SNMP", "SFTP"},
                0
        ));
        adicionarPergunta(new Pergunta(
                "Qual comando limpa a pasta target e recompila o projeto?",
                new String[]{"mvn package", "mvn clean compile", "mvn install", "mvn site"},
                1
        ));
        adicionarPergunta(new Pergunta(
                "Qual classe e usada para ler texto do console em Java?",
                new String[]{"Scanner", "Socket", "ServerSocket", "Thread"},
                0
        ));
        adicionarPergunta(new Pergunta(
                "Qual keyword define uma constante em Java quando combinada com static?",
                new String[]{"volatile", "native", "final", "strictfp"},
                2
        ));
        adicionarPergunta(new Pergunta(
                "Qual colecao segue o modelo FIFO?",
                new String[]{"Stack", "Queue", "Set", "Map"},
                1
        ));
        adicionarPergunta(new Pergunta(
                "Qual anotacao marca um metodo de teste no JUnit 5?",
                new String[]{"@BeforeEach", "@Test", "@Mock", "@Override"},
                1
        ));
        adicionarPergunta(new Pergunta(
                "Qual arquivo define dependencias e plugins em um projeto Maven?",
                new String[]{"Dockerfile", "pom.xml", "Jenkinsfile", "settings.json"},
                1
        ));
        adicionarPergunta(new Pergunta(
                "Qual classe envia texto para um stream com autoflush opcional?",
                new String[]{"BufferedReader", "Scanner", "PrintWriter", "Socket"},
                2
        ));
        adicionarPergunta(new Pergunta(
                "Qual protocolo seguro normalmente usa a porta 443?",
                new String[]{"HTTP", "HTTPS", "FTP", "Telnet"},
                1
        ));
        adicionarPergunta(new Pergunta(
                "Qual comando executa os testes do Maven sem empacotar o projeto?",
                new String[]{"mvn test", "mvn package", "mvn deploy", "mvn exec:java"},
                0
        ));
        adicionarPergunta(new Pergunta(
                "Qual estrutura armazena pares chave-valor sem ordem garantida no Java mais basico?",
                new String[]{"HashMap", "ArrayList", "HashSet", "LinkedList"},
                0
        ));
        adicionarPergunta(new Pergunta(
                "Qual bloco trata excecoes em Java?",
                new String[]{"switch", "for", "try-catch", "if-else"},
                2
        ));
        adicionarPergunta(new Pergunta(
                "Qual comando mostra a versao do Maven no terminal?",
                new String[]{"mvn -version", "java -version", "git --version", "docker version"},
                0
        ));
        adicionarPergunta(new Pergunta(
                "Qual colecao permite acesso por indice?",
                new String[]{"List", "Set", "Map", "Queue"},
                0
        ));
        adicionarPergunta(new Pergunta(
                "Qual recurso do Jenkins publica relatorios de testes JUnit?",
                new String[]{"archiveArtifacts", "junit", "mail", "timestamps"},
                1
        ));
        adicionarPergunta(new Pergunta(
                "Qual comando sobe servicos definidos em docker-compose em segundo plano?",
                new String[]{"docker build", "docker compose up -d", "docker run", "docker ps"},
                1
        ));
        adicionarPergunta(new Pergunta(
                "Qual classe representa uma conexao TCP aceita pelo servidor?",
                new String[]{"Socket", "ServerSocket", "URL", "File"},
                0
        ));
        adicionarPergunta(new Pergunta(
                "Qual tipo de teste verifica comportamentos isolados de classes pequenas?",
                new String[]{"Teste de carga", "Teste unitario", "Teste exploratorio", "Teste manual"},
                1
        ));
    }
}
