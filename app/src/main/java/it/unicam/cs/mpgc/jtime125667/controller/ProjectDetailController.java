package it.unicam.cs.mpgc.jtime125667.controller;

import it.unicam.cs.mpgc.jtime125667.model.*;
import it.unicam.cs.mpgc.jtime125667.persistence.*;
import it.unicam.cs.mpgc.jtime125667.report.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

public class ProjectDetailController {

    @FXML private Label projectNameLabel;
    @FXML private Label projectDescriptionLabel;
    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> titleColumn;
    @FXML private TableColumn<Task, String> estimatedTimeColumn;
    @FXML private TableColumn<Task, String> statusColumn;

    private ConcreteProject currentProject;
    private HibernateRepository<ConcreteProject> projectRepository;

    public void setProject(ConcreteProject project) {
        this.currentProject = project;
        this.projectRepository = new HibernateRepository<>(ConcreteProject.class);
        updateView();
    }

    @FXML
    public void initialize() {
        // Configurazione Colonne Tabella
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        
        estimatedTimeColumn.setCellValueFactory(cellData -> {
            Duration d = cellData.getValue().getEstimatedDuration();
            return new SimpleStringProperty(d != null ? d.toMinutes() + " min" : "-");
        });

        statusColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().isCompleted() ? "Completato" : "In Corso"));
    }

    private void updateView() {
        if (currentProject != null) {
            projectNameLabel.setText(currentProject.getName());
            projectDescriptionLabel.setText(currentProject.getDescription());
            // Aggiorna la tabella convertendo la lista in ObservableList
            taskTable.setItems(FXCollections.observableArrayList(currentProject.getTasks()));
        }
    }

    @FXML
    private void handleReport() {
        if (currentProject == null) return;

        // 1. Genera il report
        TextReportVisitor visitor = new TextReportVisitor();
        currentProject.accept(visitor);
        String reportText = visitor.getReport();

        // 2. Mostra la finestra con il pulsante di salvataggio integrato
        showReportDialog(reportText);
    }

    private void showReportDialog(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Report Progetto");
        alert.setHeaderText("Riepilogo Attività");

        // Configurazione TextArea (invariata)
        TextArea textArea = new TextArea(text);
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

        // --- SOLUZIONE 1: Pulsanti Vicini ---
        // Usiamo OK_DONE per "Salva" così JavaFX lo raggruppa a destra insieme a CANCEL_CLOSE
        ButtonType buttonSave = new ButtonType("Salva su File", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonClose = new ButtonType("Chiudi", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonSave, buttonClose);

        // --- SOLUZIONE 2: Impedire la chiusura automatica ---
        // Recuperiamo il nodo Button reale dall'Alert
        Button saveBtn = (Button) alert.getDialogPane().lookupButton(buttonSave);
        
        // Aggiungiamo un filtro all'evento. Questo viene eseguito PRIMA che l'Alert gestisca il click.
        saveBtn.addEventFilter(ActionEvent.ACTION, event -> {
            // 1. Eseguiamo il salvataggio
            saveReportToFile(text);
            
            // 2. "Consumiamo" l'evento. Questo dice a JavaFX: "Ho gestito io il click, fermati qui".
            // Di conseguenza, l'Alert non riceve il comando di chiudersi.
            event.consume();
        });

        // Mostriamo la finestra
        alert.showAndWait();
    }

    private void saveReportToFile(String content) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salva Report");
        fileChooser.setInitialFileName("Report_" + currentProject.getName().replaceAll("\\s+", "_") + ".txt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File di Testo", "*.txt"));
        
        Stage stage = (Stage) projectNameLabel.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(content);
                // Feedback opzionale
                // System.out.println("File salvato in: " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleAddTask() {
        // Creiamo una Dialog personalizzata
        Dialog<Pair<String, LocalDate>> dialog = new Dialog<>();
        dialog.setTitle("Nuovo Task");
        dialog.setHeaderText("Inserisci i dettagli del task");

        // Setta i bottoni (OK e Cancel)
        ButtonType loginButtonType = new ButtonType("Crea", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        // Crea i campi di input
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField titleField = new TextField();
        titleField.setPromptText("Titolo");
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Data pianificata");

        grid.add(new Label("Titolo:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Data:"), 0, 1);
        grid.add(datePicker, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Converte il risultato quando si preme OK
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(titleField.getText(), datePicker.getValue());
            }
            return null;
        });

        Optional<Pair<String, LocalDate>> result = dialog.showAndWait();

        result.ifPresent(pair -> {
            String title = pair.getKey();
            LocalDate date = pair.getValue();
            
            if (title != null && !title.isEmpty()) {
                ConcreteTask newTask = new ConcreteTask(title, "", Duration.ofMinutes(60), date);
                currentProject.addTask(newTask);
                projectRepository.save(currentProject);
                updateView();
            }
        });
    }
    
    @FXML
    private void handleCompleteTask() {
        Task selected = taskTable.getSelectionModel().getSelectedItem();
        if (selected != null && !selected.isCompleted()) {
            selected.complete(selected.getEstimatedDuration()); // Simuliamo durata effettiva = stimata
            projectRepository.save(currentProject);
            taskTable.refresh(); // Rinfresca la tabella per mostrare "Completato"
        }
    }

    @FXML
    private void handleDeleteTask() {
        Task selected = taskTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            currentProject.removeTask(selected);
            projectRepository.save(currentProject);
            updateView();
        }
    }

    @FXML
    private void handleBack() {
        try {
            // Torna alla lista progetti
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unicam/cs/mpgc/jtime125667/view/ProjectList.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) projectNameLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}