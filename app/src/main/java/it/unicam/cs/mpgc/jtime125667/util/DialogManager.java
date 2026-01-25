package it.unicam.cs.mpgc.jtime125667.util;

import it.unicam.cs.mpgc.jtime125667.model.*;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.util.*;

import java.time.Duration;
import java.util.*;

public class DialogManager {

    private static final String DIALOG_STYLE = "-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 13px;";

    public static Optional<Pair<String, String>> showNewProjectDialog() {
        Dialog<Pair<String, String>> dialog = new Dialog<Pair<String, String>>();
        dialog.setTitle("Nuovo Progetto");

        dialog.setHeaderText(null); 
        dialog.getDialogPane().setStyle(DIALOG_STYLE);

        ButtonType loginButtonType = new ButtonType("Crea", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(15);
        grid.setPadding(new Insets(20, 20, 10, 20));

        Label headerLabel = new Label("Dettagli Progetto");
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        grid.add(headerLabel, 0, 0, 2, 1);

        TextField nameField = new TextField();
        nameField.setPromptText("Es. Sviluppo App Mobile");
        nameField.setPrefWidth(300);

        TextArea descField = new TextArea();
        descField.setPromptText("Descrivi brevemente lo scopo del progetto...");
        descField.setPrefRowCount(4);
        descField.setWrapText(true);

        grid.add(new Label("Nome:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Descrizione:"), 0, 2);
        grid.add(descField, 1, 2);

        javafx.application.Platform.runLater(nameField::requestFocus);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<String, String>(nameField.getText(), descField.getText());
            }
            return null;
        });

        return dialog.showAndWait();
    }

    public static Optional<ConcreteTask> showTaskDialog(ConcreteTask taskToEdit) {
        Dialog<ConcreteTask> dialog = new Dialog<ConcreteTask>();
        boolean isEdit = taskToEdit != null;
        
        dialog.setTitle(isEdit ? "Modifica Attività" : "Nuova Attività");
        dialog.setHeaderText(null);
        dialog.getDialogPane().setStyle(DIALOG_STYLE);

        ButtonType confirmButtonType = new ButtonType(isEdit ? "Salva Modifiche" : "Aggiungi", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(15);
        grid.setPadding(new Insets(20, 20, 10, 20));

        Label headerLabel = new Label(isEdit ? "Modifica Task" : "Dettagli nuova attività");
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        grid.add(headerLabel, 0, 0, 2, 1);

        TextField titleField = new TextField();
        titleField.setPromptText("Cosa devi fare?");
        
        TextArea descField = new TextArea(); 
        descField.setPromptText("Note aggiuntive...");
        descField.setPrefRowCount(3);
        descField.setWrapText(true);

        TextField durationField = new TextField("60");
        durationField.setPromptText("Minuti");
        
        DatePicker datePicker = new DatePicker();
        
        TextField tagsField = new TextField();
        tagsField.setPromptText("Es: urgente, backend, revisione");

        if (isEdit) {
            titleField.setText(taskToEdit.getTitle());
            descField.setText(taskToEdit.getDescription());
            durationField.setText(String.valueOf(taskToEdit.getEstimatedDuration().toMinutes()));
            datePicker.setValue(taskToEdit.getScheduledDate());
            if (taskToEdit.getTags() != null) {
                tagsField.setText(String.join(", ", taskToEdit.getTags()));
            }
        }

        grid.add(new Label("Titolo:"), 0, 1); grid.add(titleField, 1, 1);
        grid.add(new Label("Descrizione:"), 0, 2); grid.add(descField, 1, 2);
        
        grid.add(new Label("Stima (min):"), 0, 3); grid.add(durationField, 1, 3);
        grid.add(new Label("Data:"), 0, 4); grid.add(datePicker, 1, 4);
        
        grid.add(new Label("Tags:"), 0, 5); grid.add(tagsField, 1, 5);

        Label helpTag = new Label("Separa i tag con una virgola");
        helpTag.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");
        grid.add(helpTag, 1, 6);

        dialog.getDialogPane().setContent(grid);
        javafx.application.Platform.runLater(titleField::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType && !titleField.getText().isEmpty()) {
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
                        if(!tag.trim().isEmpty()) task.getTags().add(tag.trim());
                    }
                }
                return task;
            }
            return null;
        });

        return dialog.showAndWait();
    }

    public static Optional<Long> showCompleteTaskDialog(String taskTitle, long estimatedMin) {
        Dialog<Long> dialog = new Dialog<Long>();
        dialog.setTitle("Completa Task");
        dialog.setHeaderText(null);
        dialog.getDialogPane().setStyle(DIALOG_STYLE);
        
        ButtonType finishBtn = new ButtonType("Completa", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(finishBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(15);
        grid.setPadding(new Insets(20, 20, 20, 20));

        Label title = new Label("Hai completato l'attività?");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        
        Label subTitle = new Label(taskTitle);
        subTitle.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");

        TextField timeField = new TextField(String.valueOf(estimatedMin));
        timeField.setPromptText("Minuti effettivi");

        grid.add(title, 0, 0);
        grid.add(subTitle, 0, 1);
        grid.add(new Label("Tempo impiegato (min):"), 0, 2);
        grid.add(timeField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        javafx.application.Platform.runLater(timeField::requestFocus);

        dialog.setResultConverter(btn -> {
            if (btn == finishBtn) {
                try { return Long.parseLong(timeField.getText()); } catch (Exception e) { return null; }
            }
            return null;
        });
        
        return dialog.showAndWait();
    }
}