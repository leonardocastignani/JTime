package it.unicam.cs.mpgc.jtime125667.model;

import it.unicam.cs.mpgc.jtime125667.report.*;

import jakarta.persistence.*;
import java.time.*;
import java.util.*;

/**
 * Implementazione concreta dell'interfaccia {@link Task}.
 * <p>
 *  Questa classe rappresenta una singola attività (o "task") all'interno di un progetto.
 *  È un'entità persistente gestita da Hibernate/JPA.
 * </p>
 * <p>
 *  Ogni task ha un titolo, una descrizione, una stima temporale e può essere associato
 *  a una data specifica (per l'agenda). Può inoltre avere una lista di "tag" per la categorizzazione.
 * </p>
 */
@Entity
@Table(name = "tasks")
public class ConcreteTask implements Task {

    /**
     * Identificativo univoco del task (UUID).
     */
    @Id
    private String id;

    private String title;
    private String description;

    /**
     * Durata stimata per il completamento del task.
     * Hibernate gestisce automaticamente la conversione di java.time.Duration.
     */
    private Duration estimatedDuration;

    /**
     * Durata effettiva impiegata (registrata al momento del completamento).
     */
    private Duration actualDuration;

    /**
     * Data pianificata per l'esecuzione del task (opzionale).
     * Se null, il task non appare nell'agenda giornaliera.
     */
    private LocalDate scheduledDate;

    /**
     * Lista di etichette (tag) associate al task.
     * <p>
     *  <b>Configurazione Hibernate:</b>
     *  <ul>
     *      <li>@ElementCollection: Indica che questa è una collezione di valori semplici (Stringhe),
     *          non di altre Entità. Hibernate creerà una tabella separata per salvarli.</li>
     *      <li>FetchType.EAGER: I tag vengono caricati immediatamente insieme al task.</li>
     *  </ul>
     * </p>
     */
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> tags = new ArrayList<String>();
    
    private boolean completed;

    /**
     * Costruttore vuoto richiesto da JPA/Hibernate per l'istanziazione tramite reflection.
     */
    public ConcreteTask() {}

    /**
     * Costruttore base per creare un nuovo task.
     *
     * @param title             Il titolo breve dell'attività.
     * @param description       La descrizione dettagliata.
     * @param estimatedDuration La stima del tempo necessario.
     */
    public ConcreteTask(String title, String description, Duration estimatedDuration) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.estimatedDuration = estimatedDuration;
        this.completed = false;
        this.actualDuration = Duration.ZERO;
        this.scheduledDate = null;
    }

    /**
     * Costruttore completo per creare un task già pianificato per una data.
     *
     * @param title             Il titolo dell'attività.
     * @param description       La descrizione.
     * @param estimatedDuration La stima temporale.
     * @param scheduledDate     La data in cui svolgere l'attività.
     */
    public ConcreteTask(String title, String description, Duration estimatedDuration, LocalDate scheduledDate) {
        this(title, description, estimatedDuration);
        this.scheduledDate = scheduledDate;
    }

    @Override
    public String getId() { return this.id; }

    @Override
    public String getTitle() { return this.title; }

    /**
     * Aggiorna il titolo del task.
     * 
     * @param title Il nuovo titolo.
     */
    @Override
    public void setTitle(String title) { this.title = title; }

    @Override
    public String getDescription() { return this.description; }

    /**
     * Aggiorna la descrizione del task.
     * 
     * @param description La nuova descrizione.
     */
    @Override
    public void setDescription(String description) { this.description = description; }

    @Override
    public Duration getEstimatedDuration() { return this.estimatedDuration; }

    /**
     * Aggiorna la stima temporale del task.
     * 
     * @param estimatedDuration La nuova durata stimata.
     */
    @Override
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

    /**
     * Marca il task come completato e registra il tempo effettivamente impiegato.
     *
     * @param actualDuration La durata reale impiegata per svolgere l'attività.
     */
    @Override
    public void complete(Duration actualDuration) {
        this.actualDuration = actualDuration;
        this.completed = true;
    }

    /**
     * Accetta un visitatore per la generazione di report o altre operazioni (Pattern Visitor).
     * <p>
     *  Questo metodo permette al `visitor` di elaborare i dati di questo specifico task
     *  senza accedere direttamente alla sua logica interna.
     * </p>
     *
     * @param visitor L'oggetto visitor che eseguirà l'operazione.
     */
    @Override
    public void accept(ReportVisitor visitor) {
        visitor.visit(this);
    }
}