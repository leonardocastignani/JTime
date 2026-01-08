package it.unicam.cs.mpgc.jtime125667.model;

import it.unicam.cs.mpgc.jtime125667.report.*;

import java.time.*;
import java.util.*;

/**
 * Interfaccia che definisce il contratto per un'attività (Task) nel sistema JTime.
 * <p>
 *  Un Task rappresenta un'unità di lavoro atomica all'interno di un progetto.
 *  Ogni task ha un titolo, una descrizione, una stima temporale e uno stato di completamento.
 *  Può essere opzionalmente pianificato per una data specifica e categorizzato tramite tag.
 * </p>
 * <p>
 *  Questa interfaccia estende {@link Visitable}, permettendo di utilizzare il pattern Visitor
 *  per operazioni trasversali come la generazione di report.
 * </p>
 */
public interface Task extends Visitable {

    /**
     * Restituisce l'identificativo univoco del task.
     *
     * @return Una stringa che rappresenta l'ID univoco.
     */
    String getId();

    /**
     * Restituisce il titolo breve del task.
     *
     * @return Il titolo del task.
     */
    String getTitle();

    /**
     * Imposta o aggiorna il titolo del task.
     *
     * @param title Il nuovo titolo da assegnare.
     */
    void setTitle(String title);

    /**
     * Restituisce la descrizione dettagliata del task.
     *
     * @return La descrizione del task.
     */
    String getDescription();

    /**
     * Imposta o aggiorna la descrizione del task.
     *
     * @param description La nuova descrizione da assegnare.
     */
    void setDescription(String description);

    /**
     * Restituisce la durata stimata per il completamento del task.
     *
     * @return Un oggetto {@link Duration} che rappresenta il tempo previsto.
     */
    Duration getEstimatedDuration();

    /**
     * Imposta o aggiorna la stima temporale del task.
     *
     * @param estimatedDuration La nuova durata stimata.
     */
    void setEstimatedDuration(Duration estimatedDuration);

    /**
     * Restituisce la durata effettiva impiegata per completare il task.
     * <p>
     *  Se il task non è ancora completato, questo metodo dovrebbe restituire una durata pari a zero o null,
     *  a seconda dell'implementazione.
     * </p>
     *
     * @return Un oggetto {@link Duration} che rappresenta il tempo realmente impiegato.
     */
    Duration getActualDuration();

    /**
     * Verifica se il task è stato completato.
     *
     * @return true se il task è completato, false altrimenti.
     */
    boolean isCompleted();

    /**
     * Marca il task come completato e registra il tempo effettivamente impiegato.
     *
     * @param actualDuration La durata reale impiegata per svolgere l'attività.
     */
    void complete(Duration actualDuration);

    /**
     * Restituisce la data pianificata per l'esecuzione del task.
     *
     * @return La data di pianificazione ({@link LocalDate}), oppure null se il task non è pianificato.
     */
    LocalDate getScheduledDate();

    /**
     * Imposta la data di pianificazione del task.
     * Utile per visualizzare il task nell'agenda giornaliera.
     *
     * @param date La data in cui pianificare il task.
     */
    void setScheduledDate(LocalDate date);

    /**
     * Restituisce la lista di tag associati al task.
     * I tag sono etichette testuali utili per categorizzare le attività (es. "urgente", "backend").
     *
     * @return Una lista di stringhe contenente i tag.
     */
    List<String> getTags();

    /**
     * Aggiunge un nuovo tag alla lista dei tag del task.
     *
     * @param tag La stringa del tag da aggiungere.
     */
    void addTag(String tag);
}