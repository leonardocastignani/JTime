package it.unicam.cs.mpgc.jtime125667.controller;

import it.unicam.cs.mpgc.jtime125667.model.*;
import it.unicam.cs.mpgc.jtime125667.persistence.*;
import it.unicam.cs.mpgc.jtime125667.report.*;
import it.unicam.cs.mpgc.jtime125667.util.*;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;

import java.time.Duration;

public class ProjectDetailController {

    @FXML private Label projectNameLabel;
    @FXML private Label projectDescriptionLabel;
    @FXML private CheckBox completedCheckBox;
    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> titleColumn;
    @FXML private TableColumn<Task, String> estimatedTimeColumn;
    @FXML private TableColumn<Task, String> statusColumn;

    private ConcreteProject currentProject;
    private final HibernateRepository<ConcreteProject> repository = new HibernateRepository<ConcreteProject>(ConcreteProject.class);

    public void setProject(ConcreteProject project) {
        this.currentProject = project;
        updateView();
    }

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitle()));
        estimatedTimeColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstimatedDuration().toMinutes() + " min"));
        statusColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isCompleted() ? "Completato" : "In Corso"));
    }

    private void updateView() {
        if (currentProject != null) {
            projectNameLabel.setText(currentProject.getName());
            projectDescriptionLabel.setText(currentProject.getDescription());
            completedCheckBox.setSelected(currentProject.isCompleted());
            taskTable.setItems(FXCollections.observableArrayList(currentProject.getTasks()));
            taskTable.setDisable(currentProject.isCompleted());
        }
    }

    @FXML
    private void handleAddTask() {
        if (currentProject.isCompleted()) return;

        DialogManager.showNewTaskDialog().ifPresent(task -> {
            currentProject.addTask(task);
            repository.save(currentProject);
            updateView();
        });
    }

    @FXML
    private void handleCompleteTask() {
        Task selected = taskTable.getSelectionModel().getSelectedItem();
        if (selected != null && !selected.isCompleted()) {
            DialogManager.showCompleteTaskDialog(selected.getTitle(), selected.getEstimatedDuration().toMinutes())
                .ifPresent(minutes -> {
                    selected.complete(Duration.ofMinutes(minutes));
                    repository.save(currentProject);
                    taskTable.refresh();
                });
        }
    }

    @FXML
    private void handleReport() {
        if (currentProject == null) return;
        TextReportVisitor visitor = new TextReportVisitor();
        currentProject.accept(visitor);
        ReportManager.showReportDialog(projectNameLabel.getScene().getWindow(), visitor.getReport(), "Report_" + currentProject.getName());
    }

    @FXML
    private void handleToggleComplete() {
        if (this.currentProject == null) return;

        if (this.currentProject.isCompleted()) {
            // Se il progetto era completato e l'utente toglie la spunta -> Riapri
            this.currentProject.setCompleted(false);
        } else {
            // Se l'utente prova a completare il progetto -> Verifica vincoli
            boolean allTasksDone = this.currentProject.getTasks().stream().allMatch(Task::isCompleted);
            
            if (!allTasksDone) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Impossibile chiudere");
                alert.setHeaderText("Attività pendenti");
                alert.setContentText("Non puoi chiudere il progetto finché tutte le attività non sono completate.");
                alert.showAndWait();
                
                // Ripristina la checkbox allo stato non selezionato
                this.completedCheckBox.setSelected(false);
                return;
            }
            // Se tutti i task sono completati -> Chiudi
            this.currentProject.setCompleted(true);
        }
        
        // Salva e aggiorna la vista
        this.repository.save(this.currentProject);
        this.updateView();
    }

    @FXML
    private void handleDeleteTask() {
        // Se il progetto è completato, impedisci l'eliminazione
        if (this.currentProject.isCompleted()) {
            return; 
        }
        
        Task selected = this.taskTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            this.currentProject.removeTask(selected);
            this.repository.save(this.currentProject);
            this.updateView();
        }
    }

    @FXML
    private void handleBack() {
        SceneManager.changeScene(projectNameLabel, "/it/unicam/cs/mpgc/jtime125667/view/ProjectList.fxml");
    }
}