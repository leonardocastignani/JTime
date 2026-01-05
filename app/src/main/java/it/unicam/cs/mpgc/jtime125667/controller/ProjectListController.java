package it.unicam.cs.mpgc.jtime125667.controller;

import it.unicam.cs.mpgc.jtime125667.model.*;
import it.unicam.cs.mpgc.jtime125667.persistence.*;
import it.unicam.cs.mpgc.jtime125667.util.*;

import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;

public class ProjectListController {

    @FXML private ListView<ConcreteProject> projectListView;

    private final HibernateRepository<ConcreteProject> repository = new HibernateRepository<ConcreteProject>(ConcreteProject.class);
    private final ObservableList<ConcreteProject> projects = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        projectListView.setCellFactory(param -> new ListCell<>() {
            @Override protected void updateItem(ConcreteProject item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getName() + " (" + item.getDescription() + ")");
            }
        });
        loadData();
    }

    private void loadData() {
        projects.setAll(repository.findAll());
        projectListView.setItems(projects);
    }

    @FXML
    private void handleNewProject() {
        // Una sola riga per chiamare la dialog!
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
            // Una sola riga per cambiare scena e passare i dati!
            SceneManager.changeScene(projectListView, "/it/unicam/cs/mpgc/jtime125667/view/ProjectDetail.fxml", 
                (ProjectDetailController controller) -> controller.setProject(selected)
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
             repository.delete(selected); // Assumendo che tu abbia aggiunto delete() al repository
             projects.remove(selected);
        }
    }
}