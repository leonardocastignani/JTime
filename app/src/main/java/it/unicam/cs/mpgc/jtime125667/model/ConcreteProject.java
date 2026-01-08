package it.unicam.cs.mpgc.jtime125667.model;

import it.unicam.cs.mpgc.jtime125667.report.*;

import jakarta.persistence.*;
import java.util.*;

/**
 * Implementazione concreta dell'interfaccia {@link Project}.
 * <p>
 *  Questa classe rappresenta un progetto all'interno del sistema JTime.
 *  È annotata come entità JPA (@Entity) per permettere il salvataggio automatico
 *  nel database tramite Hibernate.
 * </p>
 * <p>
 *  Un progetto contiene una lista di attività (Tasks) e possiede uno stato (Completato/In corso).
 * </p>
 */
@Entity
@Table(name = "projects")
public class ConcreteProject implements Project {

    /**
     * Identificativo univoco del progetto (UUID).
     */
    @Id
    private String id;

    private String name;
    private String description;

    /**
     * Lista dei task associati al progetto.
     * <p>
     *  <b>Configurazione Hibernate:</b>
     *  <ul>
     *      <li>targetEntity: Specifica la classe concreta da usare (ConcreteTask).</li>
     *      <li>cascade = ALL: Se elimino/salvo il progetto, l'operazione si propaga a tutti i suoi task.</li>
     *      <li>fetch = EAGER: Quando carico un progetto, carico SUBITO anche tutti i suoi task (utile per le performance in app piccole).</li>
     *  </ul>
     * </p>
     */
    @OneToMany(targetEntity = ConcreteTask.class,
               cascade = CascadeType.ALL,
               fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private List<Task> tasks = new ArrayList<Task>();

    private boolean completed;

    /**
     * Costruttore vuoto richiesto da Hibernate e JPA per istanziare l'oggetto tramite reflection.
     */
    public ConcreteProject() {}

    /**
     * Costruttore principale per creare un nuovo progetto.
     * Genera automaticamente un ID univoco.
     *
     * @param name        Il nome del progetto.
     * @param description Una breve descrizione del progetto.
     */
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

    /**
     * Imposta lo stato di completamento del progetto.
     *
     * @param completed true se il progetto è completato, false altrimenti.
     */
    public void setCompleted(boolean completed) { this.completed = completed; }

    /**
     * Verifica se il progetto può essere chiuso (Business Logic).
     * <p>
     *  Regola: Un progetto può essere marcato come "Completato" SOLO SE
     *  tutte le sue attività (task) sono state completate o se non ha attività.
     * </p>
     *
     * @return true se il progetto è chiudibile, false se ci sono attività pendenti.
     */
    public boolean canBeClosed() {
        if (this.tasks.isEmpty()) return true;
        return this.tasks.stream().allMatch(Task::isCompleted);
    }

    /**
     * Accetta un visitatore per la generazione di report (Pattern Visitor).
     * <p>
     *  Il progetto "si fa visitare" dal visitor e poi inoltra la chiamata
     *  a tutti i suoi task, permettendo al visitor di attraversare l'intera
     *  struttura gerarchica (Progetto -> Task).
     * </p>
     *
     * @param visitor Il visitatore (es. TextReportVisitor) che elaborerà i dati.
     */
    @Override
    public void accept(ReportVisitor visitor) {
        visitor.visit(this);

        for (Task task : this.tasks) {
            task.accept(visitor);
        }
    }
}