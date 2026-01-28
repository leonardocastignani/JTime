package it.unicam.cs.mpgc.jtime125667.controller;

import it.unicam.cs.mpgc.jtime125667.model.*;
import it.unicam.cs.mpgc.jtime125667.persistence.*;
import it.unicam.cs.mpgc.jtime125667.report.*;
import it.unicam.cs.mpgc.jtime125667.util.*;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;

import java.time.*;
import java.util.*;
import java.util.stream.*;

/**
 * Controller per la gestione della vista "Agenda Giornaliera".
 * 
 * <p>
 *  Questa classe si occupa di visualizzare tutte le attività (Task) pianificate per una specifica data.
 *  Permette di filtrare i task per giorno, calcolare l'impegno totale (in ore e minuti)
 *  e generare un report specifico per la giornata selezionata.
 * </p>
 */
public class AgendaController {

    private static final int MAX_DAILY_MINUTES = 480;

    @FXML private DatePicker agendaDatePicker;
    @FXML private TableView<AgendaItem> agendaTable;
    @FXML private TableColumn<AgendaItem, String> projectColumn;
    @FXML private TableColumn<AgendaItem, String> taskColumn;
    @FXML private TableColumn<AgendaItem, String> timeColumn;
    @FXML private TableColumn<AgendaItem, Task> statusColumn;
    @FXML private Label totalEffortLabel;

    /**
     * Repository per l'accesso ai dati dei progetti (e dei relativi task).
     */
    private Repository<ConcreteProject, String> repository;

    /**
     * Costruttore predefinito.
     * Inizializza il repository utilizzando l'implementazione basata su Hibernate.
     */
    public AgendaController() {
        this.repository = DataService.getInstance().getProjectRepository();
    }

    /**
     * Metodo di inizializzazione chiamato automaticamente da JavaFX dopo il caricamento dell'FXML.
     * <p>
     *  Configura:
     *      <ol>
     *          <li>La data iniziale (oggi).</li>
     *          <li>Il binding dei dati tra le proprietà degli oggetti {@link AgendaItem} e le colonne della tabella.</li>
     *          <li>Il rendering personalizzato (CellFactory) per la colonna di stato (badge colorati).</li>
     *          <li>Il caricamento iniziale dei dati.</li>
     *      </ol>
     * </p>
     */
    @FXML
    public void initialize() {
        this.agendaDatePicker.setValue(LocalDate.now());

        this.projectColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().projectName));
        this.taskColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().task.getTitle()));
        this.timeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().task.getEstimatedDuration().toMinutes() + " min"));
        this.statusColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().task));

        this.statusColumn.setCellFactory(column -> new TableCell<AgendaItem, Task>() {
            @Override
            protected void updateItem(Task item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    boolean isDone = item.isCompleted();
                    Label badge = new Label(isDone ? "COMPLETATO" : "DA FARE");
                    if (isDone) {
                         badge.setStyle("-fx-background-color: #d1f2eb; -fx-text-fill: #117864; -fx-background-radius: 4; -fx-padding: 3 8; -fx-font-weight: bold; -fx-font-size: 10px;");
                    } else {
                         badge.setStyle("-fx-background-color: #fcf3cf; -fx-text-fill: #b7950b; -fx-background-radius: 4; -fx-padding: 3 8; -fx-font-weight: bold; -fx-font-size: 10px;");
                    }
                    setGraphic(badge);
                    setAlignment(javafx.geometry.Pos.CENTER);
                    setText(null);
                }
            }
        });

        this.loadTasksForDate(LocalDate.now());
    }

    /**
     * Gestisce l'evento di cambio data nel DatePicker.
     * Ricarica la lista dei task in base alla nuova data selezionata.
     */
    @FXML
    private void handleDateChange() {
        this.loadTasksForDate(this.agendaDatePicker.getValue());
    }

    /**
     * Carica e visualizza i task pianificati per la data specificata.
     * <p>
     *  Questo metodo recupera tutti i progetti, filtra i task che hanno la data
     *  pianificata (`scheduledDate`) coincidente con quella richiesta e calcola
     *  il tempo totale stimato.
     * </p>
     *
     * @param date La data per la quale visualizzare l'agenda.
     */
    private void loadTasksForDate(LocalDate date) {
        if (date == null) return;

        List<ConcreteProject> allProjects = this.repository.findAll();

        List<AgendaItem> items = allProjects.stream()
            .flatMap(p -> p.getTasks().stream()
                .filter(t -> date.equals(t.getScheduledDate()))
                .map(t -> new AgendaItem(p.getName(), t)))
            .collect(Collectors.toList());

        this.agendaTable.setItems(FXCollections.observableArrayList(items));

        long totalMinutes = items.stream()
                .mapToLong(item -> item.task.getEstimatedDuration().toMinutes())
                .sum();
        
        long hours = totalMinutes / 60;
        long min = totalMinutes % 60;
        this.totalEffortLabel.setText("Impegno Totale: " + hours + "h " + min + "m");

        if (totalMinutes > MAX_DAILY_MINUTES) {
            this.totalEffortLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else {
            this.totalEffortLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
        }
    }

    /**
     * Gestisce la navigazione per tornare alla schermata principale (Lista Progetti).
     */
    @FXML
    private void handleBack() {
        SceneManager.changeScene(this.agendaDatePicker, "/it/unicam/cs/mpgc/jtime125667/view/ProjectList.fxml");
    }

    /**
     * Genera un report delle attività per la sola giornata selezionata.
     * <p>
     *  Utilizza il pattern Visitor (`DateRangeReportVisitor`) impostando data di inizio
     *  e fine coincidenti con la data selezionata nel DatePicker.
     * </p>
     */
    @FXML
    private void handleDailyReport() {
        LocalDate date = this.agendaDatePicker.getValue();
        if (date == null) return;

        DateRangeReportVisitor visitor = new DateRangeReportVisitor(date, date);
        
        List<ConcreteProject> allProjects = repository.findAll();
        for (ConcreteProject p : allProjects) {
            p.accept(visitor);
        }

        ReportManager.showReportDialog(
            this.agendaDatePicker.getScene().getWindow(), 
            visitor.getReport(), 
            "Report_Agenda_" + date.toString()
        );
    }

    /**
     * Classe interna (DTO - Data Transfer Object) utilizzata esclusivamente
     * per popolare la {@link TableView}.
     * 
     * <p>
     *  Serve ad associare in modo piatto un Task al nome del suo Progetto contenitore,
     *  facilitando la visualizzazione tabellare.
     * </p>
     */
    public static class AgendaItem {
        String projectName;
        Task task;

        /**
         * Crea un nuovo elemento per l'agenda.
         * 
         * @param projectName Nome del progetto di appartenenza.
         * @param task Il task da visualizzare.
         */
        public AgendaItem(String projectName, Task task) {
            this.projectName = projectName;
            this.task = task;
        }

        public Task getTask() { return task; }
    }
}