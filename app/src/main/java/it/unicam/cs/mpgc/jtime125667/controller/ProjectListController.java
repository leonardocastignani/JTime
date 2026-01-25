package it.unicam.cs.mpgc.jtime125667.controller;

import it.unicam.cs.mpgc.jtime125667.model.*;
import it.unicam.cs.mpgc.jtime125667.persistence.*;
import it.unicam.cs.mpgc.jtime125667.util.*;

import javafx.application.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;

import java.util.stream.*;
import java.util.*;

/**
 * Controller per la gestione della vista principale "Lista Progetti".
 * <p>
 *  Questa classe è il punto di ingresso dell'interfaccia grafica principale.
 *  Si occupa di visualizzare l'elenco di tutti i progetti salvati, permettendo all'utente di:
 *  <ul>
 *      <li>Creare nuovi progetti.</li>
 *      <li>Filtrare i progetti per nascondere quelli già completati.</li>
 *      <li>Aprire il dettaglio di un progetto selezionato.</li>
 *      <li>Eliminare progetti esistenti.</li>
 *      <li>Navigare verso l'Agenda giornaliera.</li>
 *  </ul>
 * </p>
 */
public class ProjectListController {

    @FXML private ListView<ConcreteProject> projectListView;
    @FXML private CheckBox filterActiveCheckBox; 

    /**
     * Repository per l'accesso ai dati persistenti dei progetti.
     */
    private final Repository<ConcreteProject, String> repository;

    /**
     * Lista osservabile che mantiene i dati visualizzati nella ListView.
     * Le modifiche a questa lista si riflettono automaticamente nell'interfaccia.
     */
    private final ObservableList<ConcreteProject> projects = FXCollections.observableArrayList();

    /**
     * Costruttore predefinito.
     * Inizializza il repository utilizzando l'implementazione basata su Hibernate.
     */
    public ProjectListController() {
        this.repository = new HibernateRepository<>(ConcreteProject.class);
    }

    /**
     * Metodo di inizializzazione chiamato automaticamente da JavaFX dopo il caricamento dell'FXML.
     * <p>
     *  Configura il rendering personalizzato delle celle della lista (per mostrare nome e stato)
     *  e aggiunge un listener alla checkbox di filtro per aggiornare la lista in tempo reale.
     * </p>
     */
    @FXML
    public void initialize() {
        this.projectListView.setCellFactory(param -> new ProjectCell());

        this.projectListView.setFocusTraversable(false);

        Label emptyLabel = new Label("Nessun progetto presente.\nClicca '+ Nuovo' per iniziare.");
        emptyLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 14px; -fx-text-alignment: center;");
        this.projectListView.setPlaceholder(emptyLabel);

        if (this.filterActiveCheckBox != null) {
            this.filterActiveCheckBox.setFocusTraversable(false);
            this.filterActiveCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> this.loadData());
        }

        this.loadData();
    }

    /**
     * Carica i progetti dal database e aggiorna la ListView.
     * <p>
     *  Se la checkbox "Nascondi completati" è selezionata, filtra la lista
     *  mostrando solo i progetti attivi. Altrimenti, mostra tutti i progetti.
     * </p>
     */
    private void loadData() {
        List<ConcreteProject> allProjects = this.repository.findAll();
        if (this.filterActiveCheckBox != null && this.filterActiveCheckBox.isSelected()) {
            List<ConcreteProject> activeOnly = allProjects.stream()
                .filter(p -> !p.isCompleted())
                .collect(Collectors.toList());
            this.projects.setAll(activeOnly);
        } else {
            this.projects.setAll(allProjects);
        }
        
        this.projectListView.setItems(this.projects);

        Platform.runLater(() -> {
            this.projectListView.getSelectionModel().clearSelection();
        });
    }

    /**
     * Gestisce la creazione di un nuovo progetto.
     * Apre una finestra di dialogo per l'inserimento dati e, se confermato, salva il nuovo progetto.
     */
    @FXML
    private void handleNewProject() {
        DialogManager.showNewProjectDialog().ifPresent(pair -> {
            if (!pair.getKey().trim().isEmpty()) {
                this.repository.save(new ConcreteProject(pair.getKey(), pair.getValue()));
                this.loadData();
            }
        });
    }

    /**
     * Apre la schermata di dettaglio del progetto selezionato.
     * <p>
     *  Utilizza `SceneManager` per cambiare scena e inietta il progetto selezionato
     *  e il repository nel controller di destinazione (`ProjectDetailController`).
     * </p>
     */
    @FXML
    private void handleOpenProject() {
        ConcreteProject selected = this.projectListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            SceneManager.changeScene(this.projectListView, "/it/unicam/cs/mpgc/jtime125667/view/ProjectDetail.fxml", 
                (ProjectDetailController controller) -> {
                    controller.setRepository(this.repository);
                    controller.setProject(selected);
                }
            );
        }
    }

    /**
     * Naviga verso la schermata dell'Agenda Giornaliera.
     */
    @FXML
    private void handleOpenAgenda() {
        SceneManager.changeScene(this.projectListView, "/it/unicam/cs/mpgc/jtime125667/view/Agenda.fxml");
    }

    /**
     * Elimina il progetto selezionato dal database.
     * Ricarica la lista dopo l'eliminazione.
     */
    @FXML
    private void handleDeleteProject() {
        ConcreteProject selected = this.projectListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
             this.repository.delete(selected);
             this.loadData();
        }
    }
}