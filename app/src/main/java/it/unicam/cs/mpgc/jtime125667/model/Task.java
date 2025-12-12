package it.unicam.cs.mpgc.jtime125667.model;

import java.time.*;

/**
 * Interfaccia che definisce le responsabilità di un'attività (Task).
 */
public interface Task {
    
    String getId();
    
    String getTitle();
    
    String getDescription();
    
    /**
     * Restituisce la stima del tempo necessario.
     */
    Duration getEstimatedDuration();
    
    /**
     * Restituisce la durata effettiva se completata.
     */
    Duration getActualDuration();
    
    boolean isCompleted();
    
    void complete(Duration actualDuration);

    /**
     * Restituisce la data pianificata per lo svolgimento.
     */
    LocalDate getScheduledDate();

    void setScheduledDate(LocalDate date);
    
    // Metodi per supportare estensioni future (es. Visitor pattern per reportistica)
    // void accept(TaskVisitor visitor);
}