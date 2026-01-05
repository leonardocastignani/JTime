package it.unicam.cs.mpgc.jtime125667.util;

import it.unicam.cs.mpgc.jtime125667.model.*;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.*;

import java.time.Duration;
import java.util.*;

public class DialogManager {
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

    public static Optional<ConcreteTask> showNewTaskDialog() {
        Dialog<ConcreteTask> dialog = new Dialog<>();
        dialog.setTitle("Nuovo Task");
        dialog.setHeaderText("Dettagli attività");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);

        TextField titleField = new TextField();
        TextArea descField = new TextArea(); descField.setPrefRowCount(2);
        TextField durationField = new TextField("60");
        DatePicker datePicker = new DatePicker();

        grid.add(new Label("Titolo:"), 0, 0); grid.add(titleField, 1, 0);
        grid.add(new Label("Descrizione:"), 0, 1); grid.add(descField, 1, 1);
        grid.add(new Label("Stima (min):"), 0, 2); grid.add(durationField, 1, 2);
        grid.add(new Label("Data:"), 0, 3); grid.add(datePicker, 1, 3);
        
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK && !titleField.getText().isEmpty()) {
                long min = 60;
                try { min = Long.parseLong(durationField.getText()); } catch (Exception e) {}
                return new ConcreteTask(titleField.getText(), descField.getText(), Duration.ofMinutes(min), datePicker.getValue());
            }
            return null;
        });

        return dialog.showAndWait();
    }
    
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