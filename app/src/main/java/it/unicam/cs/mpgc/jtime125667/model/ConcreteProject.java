package it.unicam.cs.mpgc.jtime125667.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "projects")
public class ConcreteProject implements Project {

    @Id
    private String id;

    private String name;
    private String description;

    // Relazione 1-a-Molti: Un progetto ha molti task.
    // CascadeType.ALL significa che se salvo/cancello il progetto, 
    // l'azione si propaga ai task.
    @OneToMany(targetEntity = ConcreteTask.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id") // Crea una colonna project_id nella tabella tasks
    private List<Task> tasks = new ArrayList<>();

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
    public void addTask(Task task) {
        this.tasks.add(task);
    }

    @Override
    public void removeTask(Task task) {
        this.tasks.remove(task);
    }

    @Override
    public boolean isCompleted() { return completed; }
    
    public void setCompleted(boolean completed) { this.completed = completed; }
}