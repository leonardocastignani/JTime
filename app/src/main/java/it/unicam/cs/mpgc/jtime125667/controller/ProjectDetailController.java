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

import java.io.*;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

public class ProjectDetailController {

    @FXML private Label projectNameLabel;
    @FXML private Label projectDescriptionLabel;
    @FXML private CheckBox completedCheckBox; // Riferimento alla CheckBox
    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> titleColumn;
    @FXML private TableColumn<Task, String> estimatedTimeColumn;
    @FXML private TableColumn<Task, String> statusColumn;

    private ConcreteProject currentProject;
    private HibernateRepository<ConcreteProject> projectRepository;

    public void setProject(ConcreteProject project) {
        this.currentProject = project;
        this.projectRepository = new HibernateRepository<>(ConcreteProject.class);
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
            
            // Aggiorna lo stato della CheckBox
            this.completedCheckBox.setSelected(this.currentProject.isCompleted());
            
            // Disabilita la tabella se il progetto è completato (opzionale, ma buona UX)
            this.taskTable.setDisable(this.currentProject.isCompleted());
        }
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
        this.projectRepository.save(this.currentProject);
        this.updateView();
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
        // Se il progetto è completato, impedisci l'aggiunta di task
        if (this.currentProject.isCompleted()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Riapri il progetto per aggiungere nuovi task.");
            alert.show();
            return;
        }

        // Cambiamo il tipo della Dialog per restituire direttamente un oggetto ConcreteTask
        Dialog<ConcreteTask> dialog = new Dialog<>();
        dialog.setTitle("Nuovo Task");
        dialog.setHeaderText("Inserisci i dettagli del task");

        ButtonType createButtonType = new ButtonType("Crea", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        // Layout Griglia
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        // Campi di Input
        TextField titleField = new TextField();
        titleField.setPromptText("Titolo attività");

        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Descrizione attività");
        descriptionField.setPrefRowCount(3);
        descriptionField.setWrapText(true);

        TextField durationField = new TextField("60"); // Default 60 min
        durationField.setPromptText("Minuti");

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Data pianificata");

        // Aggiunta alla griglia
        grid.add(new Label("Titolo:"), 0, 0);
        grid.add(titleField, 1, 0);
        
        grid.add(new Label("Descrizione:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        
        grid.add(new Label("Stima (min):"), 0, 2);
        grid.add(durationField, 1, 2);
        
        grid.add(new Label("Data:"), 0, 3);
        grid.add(datePicker, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Convertitore Risultato
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                String title = titleField.getText();
                String desc = descriptionField.getText();
                String durationStr = durationField.getText();
                LocalDate date = datePicker.getValue();

                if (title == null || title.trim().isEmpty()) {
                    return null; // Titolo obbligatorio
                }

                long minutes = 60;
                try {
                    minutes = Long.parseLong(durationStr);
                } catch (NumberFormatException e) {
                    // Se l'utente scrive testo invece di numeri, usiamo default
                }

                // Creiamo il task con TUTTI i dati inseriti
                return new ConcreteTask(title, desc, Duration.ofMinutes(minutes), date);
            }
            return null;
        });

        Optional<ConcreteTask> result = dialog.showAndWait();

        result.ifPresent(newTask -> {
            this.currentProject.addTask(newTask);
            this.projectRepository.save(this.currentProject);
            this.updateView();
        });
    }
    
    @FXML
    private void handleCompleteTask() {
        Task selected = taskTable.getSelectionModel().getSelectedItem();
        if (selected != null && !selected.isCompleted()) {
            TextInputDialog dialog = new TextInputDialog(String.valueOf(selected.getEstimatedDuration().toMinutes()));
            dialog.setTitle("Completa Task");
            dialog.setHeaderText("Conferma completamento: " + selected.getTitle());
            dialog.setContentText("Durata effettiva (minuti):");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(minutesStr -> {
                try {
                    long minutes = Long.parseLong(minutesStr);
                    selected.complete(Duration.ofMinutes(minutes));
                    this.projectRepository.save(this.currentProject);
                    taskTable.refresh();
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Inserisci un numero valido per i minuti.");
                    alert.show();
                }
            });
        }
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