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
    @FXML private TableColumn<Task, String> tagsColumn;

    private ConcreteProject currentProject;
    private Repository<ConcreteProject, String> repository;

    public ProjectDetailController() {
        // Fallback se non iniettato
        this.repository = new HibernateRepository<>(ConcreteProject.class);
    }
    
    // Dependency Injection Setter
    public void setRepository(Repository<ConcreteProject, String> repository) {
        this.repository = repository;
    }

    public void setProject(ConcreteProject project) {
        this.currentProject = project;
        updateView();
    }

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitle()));
        estimatedTimeColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstimatedDuration().toMinutes() + " min"));
        statusColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isCompleted() ? "Completato" : "In Corso"));

        if (tagsColumn != null) {
            tagsColumn.setCellValueFactory(c -> new SimpleStringProperty(String.join(", ", c.getValue().getTags())));
        }
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

    private void saveProject() {
        try {
            repository.save(currentProject);
        } catch (RuntimeException e) {
            new Alert(Alert.AlertType.ERROR, "Errore nel salvataggio: " + e.getMessage()).show();
        }
    }

    @FXML
    private void handleAddTask() {
        if (currentProject.isCompleted()) return;

        DialogManager.showTaskDialog(null).ifPresent(task -> {
            currentProject.addTask(task);
            saveProject();
            updateView();
        });
    }

    // NUOVO: Metodo per gestire la modifica
    // N.B.: Devi aggiungere un pulsante nel file FXML e collegarlo a questo metodo!
    @FXML
    private void handleEditTask() {
        if (currentProject.isCompleted()) return;

        Task selected = taskTable.getSelectionModel().getSelectedItem();
        if (selected != null && !selected.isCompleted()) {
            // Cast a ConcreteTask necessario perché il Dialog gestisce l'implementazione concreta
            if (selected instanceof ConcreteTask) {
                 DialogManager.showTaskDialog((ConcreteTask) selected).ifPresent(updatedTask -> {
                     saveProject();
                     taskTable.refresh();
                 });
            }
        } else {
             new Alert(Alert.AlertType.WARNING, "Seleziona un task non completato per modificarlo.").show();
        }
    }

    @FXML
    private void handleCompleteTask() {
        Task selected = taskTable.getSelectionModel().getSelectedItem();
        if (selected != null && !selected.isCompleted()) {
            DialogManager.showCompleteTaskDialog(selected.getTitle(), selected.getEstimatedDuration().toMinutes())
                .ifPresent(minutes -> {
                    selected.complete(Duration.ofMinutes(minutes));
                    saveProject();
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
            this.currentProject.setCompleted(false);
        } else {
            if (!this.currentProject.canBeClosed()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Impossibile chiudere");
                alert.setHeaderText("Attività pendenti");
                alert.setContentText("Il progetto ha attività non completate. Completale prima di chiudere il progetto.");
                alert.showAndWait();
                this.completedCheckBox.setSelected(false);
                return;
            }
            this.currentProject.setCompleted(true);
        }
        saveProject();
        this.updateView();
    }

    @FXML
    private void handleDeleteTask() {
        if (this.currentProject.isCompleted()) return; 
        
        Task selected = this.taskTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            this.currentProject.removeTask(selected);
            saveProject();
            this.updateView();
        }
    }

    @FXML
    private void handleBack() {
        // Qui dovremmo passare il repository indietro se necessario, ma per semplicità ricarichiamo la lista
        SceneManager.changeScene(projectNameLabel, "/it/unicam/cs/mpgc/jtime125667/view/ProjectList.fxml");
    }
}