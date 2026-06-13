package br.com.kahoot;

import javafx.application.Platform;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClienteGUITest {

    private BufferedReader mockIn;
    private PrintWriter mockOut;
    private StringWriter stringWriter;

    private ClienteGUI clienteGUI;

    @BeforeAll
    static void initJavaFX() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(() -> latch.countDown());
        latch.await();
    }

    @BeforeEach
    void setUp() throws Exception {
        stringWriter = new StringWriter();
        mockOut = new PrintWriter(stringWriter);

        clienteGUI = new ClienteGUI();

        // Initialize GUI components
        Field answerButtonsField = ClienteGUI.class.getDeclaredField("answerButtons");
        answerButtonsField.setAccessible(true);
        RadioButton[] buttons = new RadioButton[4];
        for (int i = 0; i < 4; i++) {
            buttons[i] = new RadioButton();
        }
        answerButtonsField.set(clienteGUI, buttons);

        Field answerGroupField = ClienteGUI.class.getDeclaredField("answerGroup");
        answerGroupField.setAccessible(true);
        ToggleGroup group = new ToggleGroup();
        answerGroupField.set(clienteGUI, group);
        for (RadioButton b : buttons) {
            b.setToggleGroup(group);
        }

        // Set streams
        Field outField = ClienteGUI.class.getDeclaredField("out");
        outField.setAccessible(true);
        outField.set(clienteGUI, mockOut);
    }

    @Test
    void deveCarregarPerguntaDoServidor() throws Exception {
        String input = "PERGUNTA:Qual é a capital do Brasil?\n" +
                       "ALT:1:Brasília\n" +
                       "ALT:2:Rio de Janeiro\n" +
                       "ALT:3:São Paulo\n" +
                       "ALT:4:Belo Horizonte\n" +
                       "FIM_PERGUNTA\n";
        mockIn = new BufferedReader(new StringReader(input));

        Field inField = ClienteGUI.class.getDeclaredField("in");
        inField.setAccessible(true);
        inField.set(clienteGUI, mockIn);

        clienteGUI.loadQuestionFromServer();
        // Test passes if no exception
    }

    @Test
    void deveEnviarRespostaCorreta() throws Exception {
        String input = "CORRETO:100.0\n";
        mockIn = new BufferedReader(new StringReader(input));

        Field inField = ClienteGUI.class.getDeclaredField("in");
        inField.setAccessible(true);
        inField.set(clienteGUI, mockIn);

        // Select first answer
        Field answerButtonsField = ClienteGUI.class.getDeclaredField("answerButtons");
        answerButtonsField.setAccessible(true);
        RadioButton[] buttons = (RadioButton[]) answerButtonsField.get(clienteGUI);

        Field answerGroupField = ClienteGUI.class.getDeclaredField("answerGroup");
        answerGroupField.setAccessible(true);
        ToggleGroup group = (ToggleGroup) answerGroupField.get(clienteGUI);
        group.selectToggle(buttons[0]);

        clienteGUI.submitAnswer();

        mockOut.flush();
        assertEquals("1" + System.lineSeparator(), stringWriter.toString());
    }
}