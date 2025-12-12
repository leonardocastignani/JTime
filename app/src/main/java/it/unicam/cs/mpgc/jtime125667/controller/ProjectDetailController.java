package it.unicam.cs.mpgc.jtime125667.controller;

import it.unicam.cs.mpgc.jtime125667.model.*;
import it.unicam.cs.mpgc.jtime125667.persistence.*;
import it.unicam.cs.mpgc.jtime125667.report.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.event.*;
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
        this.projectRepository = new HibernateRepository<ConcreteProject>(ConcreteProject.class);
        this.updateView();
    }

    @FXML
    public void initialize() {
        this.titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        
        this.estimatedTimeColumn.setCellValueFactory(cellData -> {
            Duration d = cellData.getValue().getEstimatedDuration();
            return new SimpleStringProperty(d != null ? d.toMinutes() + " min" : "-");
        });

        this.statusColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().isCompleted() ? "Completato" : "In Corso"));
    }

    private void updateView() {
        if (this.currentProject != null) {
            this.projectNameLabel.setText(this.currentProject.getName());
            this.projectDescriptionLabel.setText(this.currentProject.getDescription());
            this.taskTable.setItems(FXCollections.observableArrayList(this.currentProject.getTasks()));
        }
    }

    @FXML
    private void handleReport() {
        if (this.currentProject == null) return;

        TextReportVisitor visitor = new TextReportVisitor();
        this.currentProject.accept(visitor);
        String reportText = visitor.getReport();

        showReportDialog(reportText);
    }

    private void showReportDialog(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Report Progetto");
        alert.setHeaderText("Riepilogo Attività");

        TextArea textArea = new TextArea(text);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);
        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);
        alert.getDialogPane().setContent(expContent);

        ButtonType buttonSave = new ButtonType("Salva su File", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonClose = new ButtonType("Chiudi", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonSave, buttonClose);

        Button saveBtn = (Button) alert.getDialogPane().lookupButton(buttonSave);

        saveBtn.addEventFilter(ActionEvent.ACTION, event -> {
            this.saveReportToFile(text);
            event.consume();
        });

        alert.showAndWait();
    }

    private void saveReportToFile(String content) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salva Report");
        fileChooser.setInitialFileName("Report_" + this.currentProject.getName().replaceAll("\\s+", "_") + ".md");

        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("File Markdown (*.md)", "*.md")
        );
        
        Stage stage = (Stage) this.projectNameLabel.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleAddTask() {
        Dialog<Pair<String, LocalDate>> dialog = new Dialog<Pair<String, LocalDate>>();
        dialog.setTitle("Nuovo Task");
        dialog.setHeaderText("Inserisci i dettagli del task");

        ButtonType loginButtonType = new ButtonType("Crea", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

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

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<String, LocalDate>(titleField.getText(), datePicker.getValue());
            }
            return null;
        });

        Optional<Pair<String, LocalDate>> result = dialog.showAndWait();

        result.ifPresent(pair -> {
            String title = pair.getKey();
            LocalDate date = pair.getValue();
            
            if (title != null && !title.isEmpty()) {
                ConcreteTask newTask = new ConcreteTask(title, "", Duration.ofMinutes(60), date);
                this.currentProject.addTask(newTask);
                this.projectRepository.save(this.currentProject);
                this.updateView();
            }
        });
    }
    
    @FXML
    private void handleCompleteTask() {
        Task selected = this.taskTable.getSelectionModel().getSelectedItem();
        if (selected != null && !selected.isCompleted()) {
            selected.complete(selected.getEstimatedDuration());
            this.projectRepository.save(this.currentProject);
            this.taskTable.refresh();
        }
    }

    @FXML
    private void handleDeleteTask() {
        Task selected = this.taskTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            this.currentProject.removeTask(selected);
            this.projectRepository.save(this.currentProject);
            this.updateView();
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unicam/cs/mpgc/jtime125667/view/ProjectList.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) this.projectNameLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}