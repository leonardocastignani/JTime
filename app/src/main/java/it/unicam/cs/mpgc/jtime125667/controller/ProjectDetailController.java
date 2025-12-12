package it.unicam.cs.mpgc.jtime125667.controller;

import it.unicam.cs.mpgc.jtime125667.model.*;
import it.unicam.cs.mpgc.jtime125667.persistence.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;

import java.io.*;
import java.time.*;
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
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nuovo Task");
        dialog.setHeaderText("Aggiungi una nuova attività");
        dialog.setContentText("Titolo del task:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(title -> {
            // Per semplicità, ora mettiamo una durata fissa di 60 min. 
            // In futuro faremo una dialog più complessa.
            ConcreteTask newTask = new ConcreteTask(title, "", Duration.ofMinutes(60));
            
            currentProject.addTask(newTask);
            projectRepository.save(currentProject); // Salva progetto e task (Cascade)
            updateView();
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