package it.unicam.cs.mpgc.jtime125667.util;

import javafx.event.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

import java.io.*;

public class ReportManager {
    
    public static void showReportDialog(Window ownerStage, String reportText, String defaultFileName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Report");
        alert.setHeaderText("Anteprima Report");
        alert.initOwner(ownerStage); // Importante per non perdere la finestra

        // Configurazione TextArea
        TextArea textArea = new TextArea(reportText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);
        alert.getDialogPane().setContent(expContent);

        // Pulsanti Custom
        ButtonType buttonSave = new ButtonType("Salva su File", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonClose = new ButtonType("Chiudi", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonSave, buttonClose);

        // Gestione Evento Salva (senza chiudere la finestra)
        Button saveBtn = (Button) alert.getDialogPane().lookupButton(buttonSave);
        saveBtn.addEventFilter(ActionEvent.ACTION, event -> {
            saveReportToFile(ownerStage, reportText, defaultFileName);
            event.consume(); // Impedisce la chiusura dell'alert
        });

        alert.showAndWait();
    }

    private static void saveReportToFile(Window ownerStage, String content, String defaultFileName) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salva Report");
        
        // Pulisce il nome file da caratteri strani e aggiunge estensione
        String safeName = defaultFileName.replaceAll("[^a-zA-Z0-9\\-_]", "_");
        fileChooser.setInitialFileName(safeName + ".md");

        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("File Markdown (*.md)", "*.md")
        );

        File file = fileChooser.showSaveDialog(ownerStage);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(content);
            } catch (IOException e) {
                Alert error = new Alert(Alert.AlertType.ERROR, "Errore durante il salvataggio: " + e.getMessage());
                error.show();
            }
        }
    }
}