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

    private final Repository<ConcreteProject, String> repository;

    public ProjectDetailController() {
        this.repository = new HibernateRepository<>(ConcreteProject.class);
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
        
        this.repository.save(this.currentProject);
        this.updateView();
    }

    @FXML
    private void handleDeleteTask() {
        if (this.currentProject.isCompleted()) return; 
        
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