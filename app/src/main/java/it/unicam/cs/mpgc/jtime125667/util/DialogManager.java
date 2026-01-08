package it.unicam.cs.mpgc.jtime125667.util;

import it.unicam.cs.mpgc.jtime125667.model.*;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.*;

import java.time.Duration;
import java.util.*;

/**
 * Classe di utilità per la creazione e gestione delle finestre di dialogo (Dialogs).
 * <p>
 *  Questa classe centralizza la logica di interfaccia utente per l'input dei dati,
 *  fornendo metodi statici per mostrare popup modali.
 *  Gestisce dialoghi per:
 *  <ul>
 *      <li>Creazione di nuovi progetti.</li>
 *      <li>Creazione e modifica di Task (riutilizzando lo stesso form).</li>
 *      <li>Conferma di completamento attività con inserimento durata effettiva.</li>
 *  </ul>
 * </p>
 */
public class DialogManager {

    /**
     * Mostra una finestra di dialogo per la creazione di un nuovo progetto.
     * Chiede all'utente di inserire il nome e la descrizione.
     *
     * @return Un {@link Optional} contenente una coppia (Nome, Descrizione) se l'utente conferma,
     * oppure un Optional vuoto se annulla.
     */
    public static Optional<Pair<String, String>> showNewProjectDialog() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Nuovo Progetto");
        dialog.setHeaderText("Inserisci i dettagli");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        
        TextField nameField = new TextField();
        nameField.setPromptText("Nome");
        TextArea descField = new TextArea();
        descField.setPromptText("Descrizione");
        descField.setPrefRowCount(3);

        grid.add(new Label("Nome:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Descrizione:"), 0, 1);
        grid.add(descField, 1, 1);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) return new Pair<>(nameField.getText(), descField.getText());
            return null;
        });

        return dialog.showAndWait();
    }

    /**
     * Mostra una finestra di dialogo per creare o modificare un Task.
     * <p>
     *  Questo metodo è "intelligente": se viene passato un task esistente (`taskToEdit` non null),
     *  il dialog si apre in modalità "Modifica" pre-popolando i campi. Altrimenti, si apre
     *  in modalità "Nuovo Task".
     * </p>
     *
     * @param taskToEdit Il task da modificare, oppure {@code null} per crearne uno nuovo.
     * @return Un {@link Optional} contenente il task creato/aggiornato, o vuoto se annullato.
     */
    public static Optional<ConcreteTask> showTaskDialog(ConcreteTask taskToEdit) {
        Dialog<ConcreteTask> dialog = new Dialog<>();
        boolean isEdit = taskToEdit != null;
        
        dialog.setTitle(isEdit ? "Modifica Task" : "Nuovo Task");
        dialog.setHeaderText(isEdit ? "Modifica dettagli attività" : "Dettagli nuova attività");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);

        TextField titleField = new TextField();
        TextArea descField = new TextArea(); descField.setPrefRowCount(2);
        TextField durationField = new TextField("60");
        DatePicker datePicker = new DatePicker();
        TextField tagsField = new TextField();
        tagsField.setPromptText("Es: urgente, java, frontend");

        if (isEdit) {
            titleField.setText(taskToEdit.getTitle());
            descField.setText(taskToEdit.getDescription());
            durationField.setText(String.valueOf(taskToEdit.getEstimatedDuration().toMinutes()));
            datePicker.setValue(taskToEdit.getScheduledDate());
            tagsField.setText(String.join(", ", taskToEdit.getTags()));
        }

        grid.add(new Label("Titolo:"), 0, 0); grid.add(titleField, 1, 0);
        grid.add(new Label("Descrizione:"), 0, 1); grid.add(descField, 1, 1);
        grid.add(new Label("Stima (min):"), 0, 2); grid.add(durationField, 1, 2);
        grid.add(new Label("Data:"), 0, 3); grid.add(datePicker, 1, 3);
        grid.add(new Label("Tags:"), 0, 4); grid.add(tagsField, 1, 4);
        
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK && !titleField.getText().isEmpty()) {
                long min = 60;
                try { min = Long.parseLong(durationField.getText()); } catch (Exception e) {}

                ConcreteTask task = isEdit ? taskToEdit : new ConcreteTask(titleField.getText(), descField.getText(), Duration.ofMinutes(min), datePicker.getValue());
                
                if (isEdit) {
                    task.setTitle(titleField.getText());
                    task.setDescription(descField.getText());
                    task.setEstimatedDuration(Duration.ofMinutes(min));
                    task.setScheduledDate(datePicker.getValue());
                    task.getTags().clear();
                }

                String tagsInput = tagsField.getText();
                if (!tagsInput.isEmpty()) {
                    for (String tag : tagsInput.split(",")) {
                        task.getTags().add(tag.trim());
                    }
                }
                return task;
            }
            return null;
        });

        return dialog.showAndWait();
    }

    /**
     * Mostra un semplice dialog di input per confermare il completamento di un task.
     * Chiede all'utente di specificare quanti minuti ha effettivamente impiegato.
     *
     * @param taskTitle Il titolo del task che si sta completando.
     * @param estimatedMin La durata che era stata stimata (usata come valore di default).
     * @return Un {@link Optional} con i minuti effettivi inseriti dall'utente.
     */
    public static Optional<Long> showCompleteTaskDialog(String taskTitle, long estimatedMin) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(estimatedMin));
        dialog.setTitle("Completa Task");
        dialog.setHeaderText("Conferma: " + taskTitle);
        dialog.setContentText("Durata effettiva (min):");
        
        return dialog.showAndWait().map(s -> {
            try { return Long.parseLong(s); } catch (Exception e) { return null; }
        });
    }
}