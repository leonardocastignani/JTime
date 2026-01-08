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

/**
 * Controller per la gestione della vista "Dettaglio Progetto".
 * <p>
 *  Questa classe gestisce l'interfaccia utente che mostra le informazioni specifiche di un progetto
 *  (nome, descrizione, stato) e la lista delle attività (Task) associate.
 *  Permette di aggiungere, modificare, eliminare e completare task, oltre a cambiare lo stato
 *  del progetto stesso (completato/in corso).
 * </p>
 */
public class ProjectDetailController {

    @FXML private Label projectNameLabel;
    @FXML private Label projectDescriptionLabel;
    @FXML private CheckBox completedCheckBox;
    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> titleColumn;
    @FXML private TableColumn<Task, String> estimatedTimeColumn;
    @FXML private TableColumn<Task, String> statusColumn;
    @FXML private TableColumn<Task, String> tagsColumn;

    /**
     * Il progetto attualmente visualizzato nella vista.
     */
    private ConcreteProject currentProject;

    /**
     * Repository per la persistenza delle modifiche al progetto.
     */
    private Repository<ConcreteProject, String> repository;

    /**
     * Costruttore predefinito.
     * Inizializza il repository con l'implementazione Hibernate di default.
     * Questo è utile se il controller viene istanziato direttamente da FXML senza un setRepository esplicito.
     */
    public ProjectDetailController() {
        this.repository = new HibernateRepository<>(ConcreteProject.class);
    }

    /**
     * Metodo per la Dependency Injection del Repository.
     * Permette di passare un repository specifico (es. condiviso o mock per i test).
     *
     * @param repository Il repository da utilizzare per il salvataggio dei dati.
     */
    public void setRepository(Repository<ConcreteProject, String> repository) {
        this.repository = repository;
    }

    /**
     * Imposta il progetto da visualizzare e aggiorna la vista.
     *
     * @param project Il progetto di cui visualizzare i dettagli.
     */
    public void setProject(ConcreteProject project) {
        this.currentProject = project;
        this.updateView();
    }

    /**
     * Metodo di inizializzazione chiamato automaticamente da JavaFX.
     * Configura le colonne della TableView per mostrare i dati corretti dei Task.
     */
    @FXML
    public void initialize() {
        this.titleColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitle()));
        this.estimatedTimeColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstimatedDuration().toMinutes() + " min"));
        this.statusColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isCompleted() ? "Completato" : "In Corso"));

        if (this.tagsColumn != null) {
            this.tagsColumn.setCellValueFactory(c -> new SimpleStringProperty(String.join(", ", c.getValue().getTags())));
        }
    }

    /**
     * Aggiorna gli elementi grafici dell'interfaccia con i dati del progetto corrente.
     * Se il progetto è completato, disabilita la tabella per prevenire modifiche.
     */
    private void updateView() {
        if (this.currentProject != null) {
            this.projectNameLabel.setText(this.currentProject.getName());
            this.projectDescriptionLabel.setText(this.currentProject.getDescription());
            this.completedCheckBox.setSelected(this.currentProject.isCompleted());
            this.taskTable.setItems(FXCollections.observableArrayList(this.currentProject.getTasks()));
            this.taskTable.setDisable(this.currentProject.isCompleted());
        }
    }

    /**
     * Metodo helper per salvare lo stato corrente del progetto nel database.
     * Gestisce eventuali errori mostrando un Alert all'utente.
     */
    private void saveProject() {
        try {
            this.repository.save(this.currentProject);
        } catch (RuntimeException e) {
            new Alert(Alert.AlertType.ERROR, "Errore nel salvataggio: " + e.getMessage()).show();
        }
    }

    /**
     * Gestisce l'azione di aggiunta di un nuovo Task.
     * Apre una finestra di dialogo e, se confermato, aggiunge il task al progetto.
     */
    @FXML
    private void handleAddTask() {
        if (this.currentProject.isCompleted()) return;

        DialogManager.showTaskDialog(null).ifPresent(task -> {
            this.currentProject.addTask(task);
            this.saveProject();
            this.updateView();
        });
    }

    /**
     * Gestisce l'azione di modifica di un Task esistente.
     * Recupera il task selezionato dalla tabella e apre il dialog pre-popolato.
     */
    @FXML
    private void handleEditTask() {
        if (this.currentProject.isCompleted()) return;

        Task selected = this.taskTable.getSelectionModel().getSelectedItem();
        if (selected != null && !selected.isCompleted()) {
            if (selected instanceof ConcreteTask) {
                 DialogManager.showTaskDialog((ConcreteTask) selected).ifPresent(updatedTask -> {
                     this.saveProject();
                     this.taskTable.refresh();
                 });
            }
        } else {
             new Alert(Alert.AlertType.WARNING, "Seleziona un task non completato per modificarlo.").show();
        }
    }

    /**
     * Gestisce il completamento di un task.
     * Chiede all'utente di inserire la durata effettiva prima di marcare il task come completato.
     */
    @FXML
    private void handleCompleteTask() {
        Task selected = this.taskTable.getSelectionModel().getSelectedItem();
        if (selected != null && !selected.isCompleted()) {
            DialogManager.showCompleteTaskDialog(selected.getTitle(), selected.getEstimatedDuration().toMinutes())
                .ifPresent(minutes -> {
                    selected.complete(Duration.ofMinutes(minutes));
                    this.saveProject();
                    this.taskTable.refresh();
                });
        }
    }

    /**
     * Genera e visualizza il report testuale del progetto corrente
     * utilizzando il pattern Visitor.
     */
    @FXML
    private void handleReport() {
        if (this.currentProject == null) return;
        TextReportVisitor visitor = new TextReportVisitor();
        this.currentProject.accept(visitor);
        ReportManager.showReportDialog(this.projectNameLabel.getScene().getWindow(), visitor.getReport(), "Report_" + this.currentProject.getName());
    }

    /**
     * Gestisce il cambio di stato del progetto (Attivo <-> Completato).
     * <p>
     *  Implementa una regola di business fondamentale: un progetto NON può essere chiuso
     *  se ci sono ancora attività pendenti (non completate).
     * </p>
     */
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
        this.saveProject();
        this.updateView();
    }

    /**
     * Elimina il task selezionato dal progetto.
     */
    @FXML
    private void handleDeleteTask() {
        if (this.currentProject.isCompleted()) return; 
        
        Task selected = this.taskTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            this.currentProject.removeTask(selected);
            this.saveProject();
            this.updateView();
        }
    }

    /**
     * Torna alla vista della lista progetti.
     */
    @FXML
    private void handleBack() {
        SceneManager.changeScene(this.projectNameLabel, "/it/unicam/cs/mpgc/jtime125667/view/ProjectList.fxml");
    }
}