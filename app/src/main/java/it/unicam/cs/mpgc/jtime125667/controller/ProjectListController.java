package it.unicam.cs.mpgc.jtime125667.controller;

import it.unicam.cs.mpgc.jtime125667.model.*;
import it.unicam.cs.mpgc.jtime125667.persistence.*;
import it.unicam.cs.mpgc.jtime125667.util.*;

import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;

import java.util.stream.*;
import java.util.*;

public class ProjectListController {

    @FXML private ListView<ConcreteProject> projectListView;
    @FXML private CheckBox filterActiveCheckBox; 

    private final Repository<ConcreteProject, String> repository;
    private final ObservableList<ConcreteProject> projects = FXCollections.observableArrayList();

    public ProjectListController() {
        this.repository = new HibernateRepository<>(ConcreteProject.class);
    }

    @FXML
    public void initialize() {
        projectListView.setCellFactory(param -> new ListCell<>() {
            @Override protected void updateItem(ConcreteProject item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String status = item.isCompleted() ? "[CHIUSO] " : "[ATTIVO] ";
                    setText(status + item.getName() + " (" + item.getDescription() + ")");
                }
            }
        });

        if (filterActiveCheckBox != null) {
            filterActiveCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> loadData());
        }

        loadData();
    }

    private void loadData() {
        List<ConcreteProject> allProjects = repository.findAll();

        if (filterActiveCheckBox != null && filterActiveCheckBox.isSelected()) {
            List<ConcreteProject> activeOnly = allProjects.stream()
                .filter(p -> !p.isCompleted())
                .collect(Collectors.toList());
            projects.setAll(activeOnly);
        } else {
            projects.setAll(allProjects);
        }
        
        projectListView.setItems(projects);
    }

    @FXML
    private void handleNewProject() {
        DialogManager.showNewProjectDialog().ifPresent(pair -> {
            if (!pair.getKey().trim().isEmpty()) {
                repository.save(new ConcreteProject(pair.getKey(), pair.getValue()));
                loadData();
            }
        });
    }

    @FXML
    private void handleOpenProject() {
        ConcreteProject selected = projectListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            SceneManager.changeScene(projectListView, "/it/unicam/cs/mpgc/jtime125667/view/ProjectDetail.fxml", 
                (ProjectDetailController controller) -> {
                    controller.setRepository(this.repository); // INIEZIONE DEL REPOSITORY
                    controller.setProject(selected);
                }
            );
        }
    }

    @FXML
    private void handleOpenAgenda() {
        SceneManager.changeScene(projectListView, "/it/unicam/cs/mpgc/jtime125667/view/Agenda.fxml");
    }
    
    @FXML
    private void handleDeleteProject() {
        ConcreteProject selected = projectListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
             repository.delete(selected);
             loadData();
        }
    }
}