package it.unicam.cs.mpgc.jtime125667.controller;

import it.unicam.cs.mpgc.jtime125667.model.ConcreteProject;
import it.unicam.cs.mpgc.jtime125667.model.Task;
import it.unicam.cs.mpgc.jtime125667.persistence.HibernateRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class AgendaController {

    @FXML private DatePicker agendaDatePicker;
    @FXML private TableView<AgendaItem> agendaTable;
    @FXML private TableColumn<AgendaItem, String> projectColumn;
    @FXML private TableColumn<AgendaItem, String> taskColumn;
    @FXML private TableColumn<AgendaItem, String> timeColumn;
    @FXML private TableColumn<AgendaItem, String> statusColumn;

    private final HibernateRepository<ConcreteProject> repository;

    public AgendaController() {
        this.repository = new HibernateRepository<>(ConcreteProject.class);
    }

    @FXML
    public void initialize() {
        agendaDatePicker.setValue(LocalDate.now()); // Imposta oggi come default

        projectColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().projectName));
        taskColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().task.getTitle()));
        timeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().task.getEstimatedDuration().toMinutes() + " min"));
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().task.isCompleted() ? "Fatto" : "Da fare"));

        loadTasksForDate(LocalDate.now());
    }

    @FXML
    private void handleDateChange() {
        loadTasksForDate(agendaDatePicker.getValue());
    }

    private void loadTasksForDate(LocalDate date) {
        if (date == null) return;

        List<ConcreteProject> allProjects = repository.findAll();
        
        // Filtra i task di TUTTI i progetti che hanno la data selezionata
        List<AgendaItem> items = allProjects.stream()
            .flatMap(p -> p.getTasks().stream()
                .filter(t -> date.equals(t.getScheduledDate()))
                .map(t -> new AgendaItem(p.getName(), t)))
            .collect(Collectors.toList());

        agendaTable.setItems(FXCollections.observableArrayList(items));
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unicam/cs/mpgc/jtime125667/view/ProjectList.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) agendaDatePicker.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Classe helper interna per visualizzare i dati nella tabella
    private static class AgendaItem {
        String projectName;
        Task task;

        public AgendaItem(String projectName, Task task) {
            this.projectName = projectName;
            this.task = task;
        }
    }
}