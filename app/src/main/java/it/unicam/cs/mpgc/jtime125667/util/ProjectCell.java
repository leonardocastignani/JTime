package it.unicam.cs.mpgc.jtime125667.util;

import it.unicam.cs.mpgc.jtime125667.model.*;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;

/**
 * Cella personalizzata per la visualizzazione di oggetti {@link ConcreteProject}
 * all'interno di una ListView.
 * 
 * <p>
 *  Questa classe estende {@link ListCell} e definisce il layout grafico per ogni
 *  riga della lista progetti.
 *  Include indicatori visivi per lo stato del progetto (completato/attivo),
 *  formattazione del testo e gestione dei colori per migliorare la leggibilità
 *  dell'interfaccia utente.
 * </p>
 */
public class ProjectCell extends ListCell<ConcreteProject> {

    /**
     * Aggiorna il contenuto della cella in base all'oggetto {@code ConcreteProject} fornito.
     * Questo metodo viene chiamato automaticamente dal framework JavaFX quando la lista deve
     * visualizzare o aggiornare una riga.
     *
     * @param item  L'oggetto {@link ConcreteProject} da visualizzare (può essere null).
     * @param empty Flag booleano che indica se la cella è vuota (non associata a dati).
     */
    @Override
    protected void updateItem(ConcreteProject item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
            setStyle("-fx-background-color: transparent;"); 
        } else {
            HBox root = new HBox(15);
            root.setAlignment(Pos.CENTER_LEFT);
            root.setPadding(new Insets(10, 10, 10, 10));

            boolean isDone = item.isCompleted();

            Circle statusDot = new Circle(6);
            statusDot.setFill(isDone ? Color.web("#bdc3c7") : Color.web("#2ecc71"));

            VBox centerContent = new VBox(5);
            centerContent.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(centerContent, Priority.ALWAYS);

            HBox titleRow = new HBox(10);
            titleRow.setAlignment(Pos.CENTER_LEFT);

            Label titleLbl = new Label(item.getName());
            titleLbl.setFont(Font.font("System", FontWeight.BOLD, 15));
            titleLbl.setTextFill(isDone ? Color.web("#95a5a6") : Color.web("#2c3e50"));
            if (isDone) titleLbl.setStyle("-fx-strikethrough: true;");

            Label statusBadge = new Label(isDone ? "COMPLETATO" : "ATTIVO");
            statusBadge.setStyle(isDone 
                ? "-fx-background-color: #ecf0f1; -fx-text-fill: #7f8c8d; -fx-padding: 2 6 2 6; -fx-background-radius: 4; -fx-font-size: 10px; -fx-font-weight: bold;"
                : "-fx-background-color: #e8f8f5; -fx-text-fill: #27ae60; -fx-padding: 2 6 2 6; -fx-background-radius: 4; -fx-font-size: 10px; -fx-font-weight: bold;"
            );

            titleRow.getChildren().addAll(titleLbl, statusBadge);

            String desc = (item.getDescription() == null) ? "" : item.getDescription().replace("\n", " ");
            Label descLbl = new Label(desc);
            descLbl.setTextFill(Color.web("#7f8c8d"));
            descLbl.setFont(Font.font("System", 12));
            descLbl.setWrapText(true);
            descLbl.setMaxWidth(Double.MAX_VALUE);

            centerContent.getChildren().addAll(titleRow, descLbl);

            root.getChildren().addAll(statusDot, centerContent);

            setText(null);
            setGraphic(root);

            if (isSelected()) {
                setStyle("-fx-background-color: #e8f0fe; -fx-border-color: #d2e3fc; -fx-border-width: 0 0 1 0;"); 
            } else {
                setStyle("-fx-background-color: transparent; -fx-border-color: #eeeeee; -fx-border-width: 0 0 1 0;");
            }
        }
    }
}