package it.unicam.cs.mpgc.jtime125667.model;

import it.unicam.cs.mpgc.jtime125667.report.*;

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

    private Duration estimatedDuration;
    private Duration actualDuration;
    private LocalDate scheduledDate;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> tags = new ArrayList<String>();
    
    private boolean completed;

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
    public String getId() { return this.id; }

    @Override
    public String getTitle() { return this.title; }
    public void setTitle(String title) { this.title = title; }

    @Override
    public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public Duration getEstimatedDuration() { return this.estimatedDuration; }
    public void setEstimatedDuration(Duration estimatedDuration) { this.estimatedDuration = estimatedDuration; }

    @Override
    public Duration getActualDuration() { return this.actualDuration; }

    @Override
    public LocalDate getScheduledDate() { return this.scheduledDate; }

    @Override
    public List<String> getTags() { return this.tags; }

    @Override
    public void setScheduledDate(LocalDate date) { this.scheduledDate = date; }

    @Override
    public void addTag(String tag) { this.tags.add(tag); }

    @Override
    public boolean isCompleted() { return this.completed; }

    @Override
    public void complete(Duration actualDuration) {
        this.actualDuration = actualDuration;
        this.completed = true;
    }

    @Override
    public void accept(ReportVisitor visitor) {
        visitor.visit(this);
    }
}