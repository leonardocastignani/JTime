package it.unicam.cs.mpgc.jtime125667.util;

import it.unicam.cs.mpgc.jtime125667.model.*;

import javafx.geometry.*;
import javafx.scene.control.*;

/**
 * Cella personalizzata per la visualizzazione dello stato di un {@link Task} all'interno di una {@link TableView}.
 * 
 * <p>
 *  Questa classe estende {@link TableCell} e sostituisce la rappresentazione testuale standard
 *  con un "badge" colorato (un'etichetta con sfondo arrotondato).
 *  Serve a distinguere visivamente a colpo d'occhio i task completati da quelli ancora in corso.
 * </p>
 */
public class TaskStatusCell extends TableCell<Task, String> {

    /**
     * Aggiorna il contenuto grafico della cella in base allo stato del task corrente.
     * 
     * <p>
     *  Questo metodo viene invocato dal framework JavaFX ogni volta che la cella deve essere renderizzata.
     *  Recupera l'oggetto {@link Task} associato all'intera riga per determinare lo stile da applicare.
     * </p>
     *
     * @param item  Il valore associato alla cella (spesso ignorato in favore dell'oggetto riga intera).
     * @param empty Flag che indica se la cella è vuota (non associata a dati).
     */
    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || getTableRow() == null || getTableRow().getItem() == null) {
            setText(null);
            setGraphic(null);
        } else {
            Task currentTask = getTableRow().getItem();
            boolean isDone = currentTask.isCompleted();

            Label badge = new Label(isDone ? "COMPLETATO" : "IN CORSO");

            if (isDone) {
                badge.setStyle("-fx-background-color: #d1f2eb; -fx-text-fill: #117864; -fx-background-radius: 5; -fx-padding: 4 8 4 8; -fx-font-weight: bold; -fx-font-size: 10px;");
            } else {
                badge.setStyle("-fx-background-color: #fcf3cf; -fx-text-fill: #b7950b; -fx-background-radius: 5; -fx-padding: 4 8 4 8; -fx-font-weight: bold; -fx-font-size: 10px;");
            }

            setText(null);
            setGraphic(badge);
            setAlignment(Pos.CENTER);
        }
    }
}