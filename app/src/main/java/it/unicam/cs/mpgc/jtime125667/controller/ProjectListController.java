package it.unicam.cs.mpgc.jtime125667.controller;

import it.unicam.cs.mpgc.jtime125667.model.*;
import it.unicam.cs.mpgc.jtime125667.persistence.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

public class ProjectListController {

    @FXML
    private ListView<ConcreteProject> projectListView;

    private final HibernateRepository<ConcreteProject> repository;
    private final ObservableList<ConcreteProject> projects;

    public ProjectListController() {
        this.repository = new HibernateRepository<ConcreteProject>(ConcreteProject.class);
        this.projects = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        this.projectListView.setCellFactory(param -> new ListCell<ConcreteProject>() {
            @Override
            protected void updateItem(ConcreteProject item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getName() == null) {
                    this.setText(null);
                } else {
                    this.setText(item.getName() + " (" + item.getDescription() + ")");
                }
            }
        });

        this.loadData();
        this.projectListView.setItems(this.projects);

        if (this.projects.isEmpty()) {
            this.createSampleData();
        }
    }

    private void createSampleData() {
        System.out.println("Database vuoto. Creo dati di esempio...");
        ConcreteProject sample = new ConcreteProject("Progetto Demo", "Creato automaticamente all'avvio");
        this.repository.save(sample);
        this.loadData();
    }

    private void loadData() {
        this.projects.clear();
        this.projects.addAll(this.repository.findAll());
    }

    @FXML
    private void handleOpenAgenda() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unicam/cs/mpgc/jtime125667/view/Agenda.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) this.projectListView.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleNewProject() {
        TextInputDialog dialog = new TextInputDialog("Nuovo Progetto");
        dialog.setTitle("Crea Progetto");
        dialog.setHeaderText("Inserisci i dettagli del nuovo progetto");
        dialog.setContentText("Nome del progetto:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                ConcreteProject newProject = new ConcreteProject(name, "Descrizione da modificare...");
                this.repository.save(newProject);
                this.loadData();
                System.out.println("Progetto salvato: " + name);
            }
        });
    }

    @FXML
    private void handleOpenProject() {
        ConcreteProject selected = this.projectListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unicam/cs/mpgc/jtime125667/view/ProjectDetail.fxml"));
                Parent root = loader.load();

                ProjectDetailController controller = loader.getController();
                controller.setProject(selected);

                Stage stage = (Stage) this.projectListView.getScene().getWindow();
                stage.setScene(new Scene(root, 800, 600));
                
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Impossibile caricare la vista di dettaglio.");
            }
        } else {
            System.out.println("Seleziona un progetto prima di aprire.");
        }
    }
}