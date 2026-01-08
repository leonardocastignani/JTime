package it.unicam.cs.mpgc.jtime125667.util;

import javafx.event.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

import java.io.*;

/**
 * Classe di utilità per la gestione della visualizzazione e del salvataggio dei report.
 * <p>
 *  Questa classe fornisce metodi statici per mostrare un'anteprima del report generato
 *  in una finestra di dialogo modale e per permettere all'utente di salvarlo su file (es. Markdown).
 * </p>
 */
public class ReportManager {

    /**
     * Mostra una finestra di dialogo con l'anteprima del report e un pulsante per il salvataggio.
     * <p>
     *  Utilizza un {@link Alert} personalizzato inserendo una {@link TextArea} al suo interno
     *  per visualizzare il contenuto del report. Gestisce inoltre l'evento di salvataggio
     *  per aprire il FileChooser.
     * </p>
     *
     * @param ownerStage      La finestra "proprietaria" del dialog (per renderlo modale rispetto ad essa).
     * @param reportText      Il contenuto testuale del report da visualizzare.
     * @param defaultFileName Il nome file suggerito per l'eventuale salvataggio.
     */
    public static void showReportDialog(Window ownerStage, String reportText, String defaultFileName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Report");
        alert.setHeaderText("Anteprima Report");
        alert.initOwner(ownerStage);

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

        ButtonType buttonSave = new ButtonType("Salva su File", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonClose = new ButtonType("Chiudi", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonSave, buttonClose);

        Button saveBtn = (Button) alert.getDialogPane().lookupButton(buttonSave);
        saveBtn.addEventFilter(ActionEvent.ACTION, event -> {
            saveReportToFile(ownerStage, reportText, defaultFileName);
            event.consume();
        });

        alert.showAndWait();
    }

    /**
     * Apre un FileChooser per permettere all'utente di salvare il report su disco.
     *
     * @param ownerStage      La finestra padre per il FileChooser.
     * @param content         Il contenuto da scrivere nel file.
     * @param defaultFileName Il nome di default suggerito per il file.
     */
    private static void saveReportToFile(Window ownerStage, String content, String defaultFileName) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salva Report");

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