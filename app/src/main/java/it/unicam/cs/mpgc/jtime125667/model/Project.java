package it.unicam.cs.mpgc.jtime125667.model;

import it.unicam.cs.mpgc.jtime125667.report.*;

import java.util.*;

/**
 * Interfaccia che definisce il contratto per un progetto nel sistema JTime.
 * <p>
 *  Un progetto è un contenitore logico di attività ({@link Task}).
 *  Questa interfaccia estende {@link Visitable}, permettendo di applicare il pattern Visitor
 *  per operazioni come la generazione di report senza modificare la struttura delle classi.
 * </p>
 */
public interface Project extends Visitable {

    /**
     * Restituisce l'identificativo univoco del progetto.
     *
     * @return Una stringa che rappresenta l'ID univoco.
     */
    String getId();

    /**
     * Restituisce il nome del progetto.
     *
     * @return Il nome del progetto.
     */
    String getName();

    /**
     * Restituisce la descrizione del progetto.
     *
     * @return La descrizione testuale del progetto.
     */
    String getDescription();

    /**
     * Restituisce la lista di tutte le attività (Task) associate a questo progetto.
     *
     * @return Una lista di oggetti {@link Task}.
     */
    List<Task> getTasks();

    /**
     * Aggiunge una nuova attività al progetto.
     *
     * @param task L'attività da aggiungere.
     */
    void addTask(Task task);

    /**
     * Rimuove un'attività esistente dal progetto.
     *
     * @param task L'attività da rimuovere.
     */
    void removeTask(Task task);

    /**
     * Verifica se il progetto è stato marcato come completato.
     *
     * @return true se il progetto è concluso, false se è ancora attivo.
     */
    boolean isCompleted();
}