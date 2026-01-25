package it.unicam.cs.mpgc.jtime125667.util;

import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;

import java.io.*;

public class ReportManager {

    private static final String REPORT_DIALOG_STYLE = "-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 13px;";

    public static void showReportDialog(Window ownerStage, String reportText, String defaultFileName) {
        Dialog<ButtonType> dialog = new Dialog<ButtonType>();
        dialog.setTitle("Report Generato");
        dialog.initOwner(ownerStage);

        dialog.getDialogPane().setStyle(REPORT_DIALOG_STYLE);
        dialog.setHeaderText(null);

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setPrefSize(600, 450);

        Label titleLabel = new Label("Anteprima Report");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label subTitleLabel = new Label("Controlla i dati prima di salvare il file.");
        subTitleLabel.setStyle("-fx-text-fill: #7f8c8d;");

        TextArea textArea = new TextArea(reportText);
        textArea.setEditable(false);
        textArea.setWrapText(false);

        textArea.setFont(Font.font("Monospaced", 13)); 
        
        textArea.setStyle("-fx-control-inner-background: #f8f9fa; -fx-text-box-border: transparent;");
        VBox.setVgrow(textArea, Priority.ALWAYS);

        root.getChildren().addAll(titleLabel, subTitleLabel, textArea);
        dialog.getDialogPane().setContent(root);

        ButtonType buttonSave = new ButtonType("Salva su File...", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonClose = new ButtonType("Chiudi", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().setAll(buttonSave, buttonClose);

        Button saveBtn = (Button) dialog.getDialogPane().lookupButton(buttonSave);

        saveBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        
        saveBtn.addEventFilter(ActionEvent.ACTION, event -> {
            boolean saved = saveReportToFile(ownerStage, reportText, defaultFileName);
            if (!saved) {
                event.consume();
            }
        });

        dialog.showAndWait();
    }

    private static boolean saveReportToFile(Window ownerStage, String content, String defaultFileName) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salva Report");

        String safeName = defaultFileName.replaceAll("[^a-zA-Z0-9\\-_]", "_");
        fileChooser.setInitialFileName(safeName + ".md");

        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("File Markdown (*.md)", "*.md")
        );
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("File Testo (*.txt)", "*.txt")
        );

        File file = fileChooser.showSaveDialog(ownerStage);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(content);
                return true;
            } catch (IOException e) {
                Alert error = new Alert(Alert.AlertType.ERROR, "Errore durante il salvataggio: " + e.getMessage());
                error.show();
            }
        }
        return false;
    }
}