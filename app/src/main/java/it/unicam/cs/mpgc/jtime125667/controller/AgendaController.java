package it.unicam.cs.mpgc.jtime125667.controller;

import it.unicam.cs.mpgc.jtime125667.model.*;
import it.unicam.cs.mpgc.jtime125667.persistence.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;

import java.io.*;
import java.time.*;
import java.util.*;
import java.util.stream.*;

public class AgendaController {

    @FXML private DatePicker agendaDatePicker;
    @FXML private TableView<AgendaItem> agendaTable;
    @FXML private TableColumn<AgendaItem, String> projectColumn;
    @FXML private TableColumn<AgendaItem, String> taskColumn;
    @FXML private TableColumn<AgendaItem, String> timeColumn;
    @FXML private TableColumn<AgendaItem, String> statusColumn;

    private final HibernateRepository<ConcreteProject> repository;

    public AgendaController() {
        this.repository = new HibernateRepository<ConcreteProject>(ConcreteProject.class);
    }

    @FXML
    public void initialize() {
        this.agendaDatePicker.setValue(LocalDate.now());

        this.projectColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().projectName));
        this.taskColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().task.getTitle()));
        this.timeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().task.getEstimatedDuration().toMinutes() + " min"));
        this.statusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().task.isCompleted() ? "Fatto" : "Da fare"));

        this.loadTasksForDate(LocalDate.now());
    }

    @FXML
    private void handleDateChange() {
        this.loadTasksForDate(this.agendaDatePicker.getValue());
    }

    private void loadTasksForDate(LocalDate date) {
        if (date == null) return;

        List<ConcreteProject> allProjects = this.repository.findAll();

        List<AgendaItem> items = allProjects.stream()
            .flatMap(p -> p.getTasks().stream()
                .filter(t -> date.equals(t.getScheduledDate()))
                .map(t -> new AgendaItem(p.getName(), t)))
            .collect(Collectors.toList());

        this.agendaTable.setItems(FXCollections.observableArrayList(items));
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unicam/cs/mpgc/jtime125667/view/ProjectList.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) this.agendaDatePicker.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class AgendaItem {
        String projectName;
        Task task;

        public AgendaItem(String projectName, Task task) {
            this.projectName = projectName;
            this.task = task;
        }
    }
}