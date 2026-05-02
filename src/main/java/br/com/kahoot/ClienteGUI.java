package br.com.kahoot;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteGUI extends Application {

    private TextField ipField;
    private TextField portField;
    private Button connectButton;
    private Label statusLabel;
    private Label questionLabel;
    private ToggleGroup answerGroup;
    private RadioButton[] answerButtons;
    private Button submitButton;
    private Label scoreLabel;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private int playerId = 0; // Simular ID do jogador
    private float score = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("MiniKahoot Cliente");

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        // Campos de conexão
        ipField = new TextField("localhost");
        portField = new TextField("12345");
        connectButton = new Button("Conectar");
        statusLabel = new Label("Desconectado");

        connectButton.setOnAction(e -> connectToServer());

        // Área da pergunta
        questionLabel = new Label("Pergunta aparecerá aqui");
        answerGroup = new ToggleGroup();
        answerButtons = new RadioButton[4];
        for (int i = 0; i < 4; i++) {
            answerButtons[i] = new RadioButton("Alternativa " + (i + 1));
            answerButtons[i].setToggleGroup(answerGroup);
        }

        submitButton = new Button("Responder");
        submitButton.setOnAction(e -> submitAnswer());
        submitButton.setDisable(true);

        scoreLabel = new Label("Pontos: 0");

        root.getChildren().addAll(
            new Label("IP:"), ipField,
            new Label("Porta:"), portField,
            connectButton, statusLabel,
            new Separator(),
            questionLabel,
            answerButtons[0], answerButtons[1], answerButtons[2], answerButtons[3],
            submitButton,
            scoreLabel
        );

        Scene scene = new Scene(root, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    void connectToServer() {
        try {
            socket = new Socket(ipField.getText(), Integer.parseInt(portField.getText()));
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            statusLabel.setText("Conectado");
            connectButton.setDisable(true);
            submitButton.setDisable(false);

            // Receber mensagem inicial
            String message = in.readLine();
            Platform.runLater(() -> statusLabel.setText("Servidor: " + message));

            // Receber primeira pergunta
            loadQuestionFromServer();

        } catch (Exception e) {
            statusLabel.setText("Erro: " + e.getMessage());
        }
    }

    void loadQuestionFromServer() {
        try {
            String line;
            String question = "";
            String[] alternatives = new String[4];
            int altIndex = 0;

            while ((line = in.readLine()) != null) {
                if (line.startsWith("PERGUNTA:")) {
                    question = line.substring(9);
                } else if (line.startsWith("ALT:")) {
                    String[] parts = line.split(":", 3);
                    int idx = Integer.parseInt(parts[1]) - 1;
                    alternatives[idx] = parts[2];
                } else if (line.equals("FIM_PERGUNTA")) {
                    break;
                } else if (line.startsWith("FIM_JOGO:")) {
                    final String finalLine = line;
                    Platform.runLater(() -> {
                        questionLabel.setText("Jogo finalizado!");
                        submitButton.setDisable(true);
                        statusLabel.setText("Pontuação final: " + finalLine.substring(9));
                    });
                    return;
                }
            }

            final String displayQuestion = question;
            final String[] displayAlternatives = alternatives;

            Platform.runLater(() -> {
                questionLabel.setText(displayQuestion);
                for (int i = 0; i < 4; i++) {
                    answerButtons[i].setText(displayAlternatives[i] != null ? displayAlternatives[i] : "");
                }
                answerGroup.selectToggle(null);
            });

        } catch (Exception e) {
            Platform.runLater(() -> statusLabel.setText("Erro ao carregar pergunta: " + e.getMessage()));
        }
    }

    void submitAnswer() {
        RadioButton selected = (RadioButton) answerGroup.getSelectedToggle();
        if (selected != null) {
            int answerIndex = -1;
            for (int i = 0; i < answerButtons.length; i++) {
                if (answerButtons[i] == selected) {
                    answerIndex = i;
                    break;
                }
            }
            // Enviar resposta (1-based)
            out.println(answerIndex + 1);

            try {
                String response = in.readLine();
                if (response != null) {
                    if (response.startsWith("CORRETO:")) {
                        score = Float.parseFloat(response.substring(8));
                        Platform.runLater(() -> statusLabel.setText("Correto!"));
                    } else if (response.startsWith("ERRADO:")) {
                        score = Float.parseFloat(response.substring(7));
                        Platform.runLater(() -> statusLabel.setText("Errado!"));
                    } else {
                        Platform.runLater(() -> statusLabel.setText("Resposta inválida"));
                    }
                    scoreLabel.setText("Pontos: " + score);
                    // Carregar próxima pergunta
                    loadQuestionFromServer();
                }
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Erro: " + e.getMessage()));
            }
        }
    }

    @Override
    public void stop() {
        try {
            if (socket != null) socket.close();
        } catch (Exception e) {
            // Ignorar
        }
    }
}