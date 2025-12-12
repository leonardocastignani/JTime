package it.unicam.cs.mpgc.jtime125667.controller;

import it.unicam.cs.mpgc.jtime125667.model.*;
import it.unicam.cs.mpgc.jtime125667.persistence.*;
import javafx.beans.property.*;
import javafx.collections.*;
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