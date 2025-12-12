package it.unicam.cs.mpgc.jtime125667.controller;

import it.unicam.cs.mpgc.jtime125667.model.*;
import it.unicam.cs.mpgc.jtime125667.persistence.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.*;

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
        Dialog<Pair<String, String>> dialog = new Dialog<Pair<String, String>>();
        dialog.setTitle("Nuovo Progetto");
        dialog.setHeaderText("Inserisci i dettagli del nuovo progetto");

        ButtonType createButtonType = new ButtonType("Crea", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Nome del progetto");
        
        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Descrizione del progetto");
        descriptionField.setPrefRowCount(3);
        descriptionField.setWrapText(true);

        grid.add(new Label("Nome:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Descrizione:"), 0, 1);
        grid.add(descriptionField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                return new Pair<String, String>(nameField.getText(), descriptionField.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(pair -> {
            String name = pair.getKey();
            String description = pair.getValue();
            
            if (name != null && !name.trim().isEmpty()) {
                if (description == null) description = "";
                
                ConcreteProject newProject = new ConcreteProject(name, description);
                this.repository.save(newProject);
                this.loadData();
                System.out.println("Progetto salvato: " + name);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Il nome del progetto è obbligatorio.");
                alert.show();
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

    @FXML
    private void handleDeleteProject() {
        ConcreteProject selected = this.projectListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Elimina Progetto");
            alert.setHeaderText("Sei sicuro di voler eliminare: " + selected.getName() + "?");
            alert.setContentText("Questa azione eliminerà anche tutte le attività associate.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                this.repository.delete(selected);
                this.projects.remove(selected);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Seleziona un progetto da eliminare.");
            alert.show();
        }
    }
}