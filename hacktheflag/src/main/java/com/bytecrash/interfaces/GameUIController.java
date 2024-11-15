package com.bytecrash.interfaces;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import com.bytecrash.terminal.CommandHandler;
import com.bytecrash.filesystem.FileSystem;

public class GameUIController {

    @FXML private TextArea terminal;
    @FXML private TextField commandInput;
    @FXML private VBox dynamicControlPanel;
    @FXML private Label timerLabel;
    @FXML private TextArea logs;

    private CommandHandler commandHandler;

    @FXML
    private void initialize() {
        FileSystem fileSystem = new FileSystem();
        commandHandler = new CommandHandler(fileSystem);
        appendToTerminal("[martins@hacktheflag ~] $ ");
    }

    @FXML
    public void processCommand() {
        String command = commandInput.getText();
        commandInput.clear();

        // Adiciona o prefixo e o comando digitado
        appendToTerminal("[martins@hacktheflag ~] $ " + command);

        // Executa o comando e captura a saída
        String output = commandHandler.executeCommand(command);
        
        // Exibe a saída no terminal, se houver
        if (output != null && !output.isEmpty()) {
            appendToTerminal(output);
        }
    }

    @FXML
    private void showExploitsPanel() {
        dynamicControlPanel.getChildren().clear();
        dynamicControlPanel.getChildren().add(new Label("Exploits disponíveis"));
    }

    @FXML
    private void showDefensePanel() {
        dynamicControlPanel.getChildren().clear();
        dynamicControlPanel.getChildren().add(new Label("Ferramentas de defesa"));
    }

    private void appendToTerminal(String text) {
        terminal.appendText(text + "\n");
        terminal.setScrollTop(Double.MAX_VALUE); // Scroll automático para a última linha
    }
}
