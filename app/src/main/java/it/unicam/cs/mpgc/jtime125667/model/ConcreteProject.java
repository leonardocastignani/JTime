package it.unicam.cs.mpgc.jtime125667.model;

import it.unicam.cs.mpgc.jtime125667.report.*;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "projects")
public class ConcreteProject implements Project {

    @Id
    private String id;

    private String name;
    private String description;

    @OneToMany(targetEntity = ConcreteTask.class,
               cascade = CascadeType.ALL,
               fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private List<Task> tasks = new ArrayList<Task>();

    private boolean completed;

    public ConcreteProject() {}

    public ConcreteProject(String name, String description) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.completed = false;
    }

    @Override
    public String getId() { return id; }

    @Override
    public String getName() { return name; }

    @Override
    public String getDescription() { return description; }

    @Override
    public List<Task> getTasks() { return tasks; }

    @Override
    public void addTask(Task task) { this.tasks.add(task); }

    @Override
    public void removeTask(Task task) { this.tasks.remove(task); }

    @Override
    public boolean isCompleted() { return completed; }
    
    public void setCompleted(boolean completed) { this.completed = completed; }

    @Override
    public void accept(ReportVisitor visitor) {
        visitor.visit(this);

        for (Task task : this.tasks) {
            task.accept(visitor);
        }
    }
}