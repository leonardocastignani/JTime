package it.unicam.cs.mpgc.jtime125667.model;

import jakarta.persistence.*;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "tasks")
public class ConcreteTask implements Task {

    @Id
    private String id;

    private String title;
    private String description;
    
    // Hibernate converte Duration in nanosecondi o secondi automaticamente nelle versioni recenti,
    // oppure si può usare un converter custom se necessario.
    private Duration estimatedDuration;
    private Duration actualDuration;
    private LocalDate scheduledDate;
    
    private boolean completed;

    // Costruttore vuoto richiesto da Hibernate
    public ConcreteTask() {}

    public ConcreteTask(String title, String description, Duration estimatedDuration) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.estimatedDuration = estimatedDuration;
        this.completed = false;
        this.actualDuration = Duration.ZERO;
        this.scheduledDate = null;
    }

    public ConcreteTask(String title, String description, Duration estimatedDuration, LocalDate scheduledDate) {
        this(title, description, estimatedDuration);
        this.scheduledDate = scheduledDate;
    }

    @Override
    public String getId() { return id; }

    @Override
    public String getTitle() { return title; }

    @Override
    public String getDescription() { return description; }

    @Override
    public Duration getEstimatedDuration() { return estimatedDuration; }

    @Override
    public Duration getActualDuration() { return actualDuration; }

    @Override
    public LocalDate getScheduledDate() { return scheduledDate; }

    @Override
    public void setScheduledDate(LocalDate date) { this.scheduledDate = date; }

    @Override
    public boolean isCompleted() { return completed; }

    @Override
    public void complete(Duration actualDuration) {
        this.actualDuration = actualDuration;
        this.completed = true;
    }
}