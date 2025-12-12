package it.unicam.cs.mpgc.jtime125667.controller;

import it.unicam.cs.mpgc.jtime125667.model.*;
import it.unicam.cs.mpgc.jtime125667.persistence.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class ProjectListController {

    @FXML
    private ListView<ConcreteProject> projectListView;

    private final HibernateRepository<ConcreteProject> repository;
    private final ObservableList<ConcreteProject> projects;

    public ProjectListController() {
        this.repository = new HibernateRepository<>(ConcreteProject.class);
        this.projects = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        // Configura come visualizzare gli oggetti nella lista (mostra solo il nome)
        projectListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(ConcreteProject item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getName() == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + item.getDescription() + ")");
                }
            }
        });

        loadData();
        projectListView.setItems(projects);

        if (projects.isEmpty()) {
            createSampleData();
        }
    }

    private void createSampleData() {
        System.out.println("Database vuoto. Creo dati di esempio...");
        ConcreteProject sample = new ConcreteProject("Progetto Demo", "Creato automaticamente all'avvio");
        repository.save(sample);
        loadData(); // Ricarica la lista per mostrarlo
    }

    private void loadData() {
        projects.clear();
        projects.addAll(repository.findAll());
    }

    @FXML
    private void handleOpenAgenda() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unicam/cs/mpgc/jtime125667/view/Agenda.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) projectListView.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleNewProject() {
        // Finestra di dialogo semplice per inserire il nome
        TextInputDialog dialog = new TextInputDialog("Nuovo Progetto");
        dialog.setTitle("Crea Progetto");
        dialog.setHeaderText("Inserisci i dettagli del nuovo progetto");
        dialog.setContentText("Nome del progetto:");

        // Aspetta l'input dell'utente
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                ConcreteProject newProject = new ConcreteProject(name, "Descrizione da modificare...");
                repository.save(newProject);
                loadData(); // Aggiorna la lista
                System.out.println("Progetto salvato: " + name);
            }
        });
    }

    @FXML
    private void handleOpenProject() {
        ConcreteProject selected = projectListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                // Carica la vista di dettaglio
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unicam/cs/mpgc/jtime125667/view/ProjectDetail.fxml"));
                Parent root = loader.load();

                // Ottieni il controller e passagli il progetto selezionato
                ProjectDetailController controller = loader.getController();
                controller.setProject(selected);

                // Cambia la scena
                Stage stage = (Stage) projectListView.getScene().getWindow();
                stage.setScene(new Scene(root, 800, 600));
                
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Impossibile caricare la vista di dettaglio.");
            }
        } else {
            // Opzionale: Mostra un alert se nessun progetto è selezionato
            System.out.println("Seleziona un progetto prima di aprire.");
        }
    }
}