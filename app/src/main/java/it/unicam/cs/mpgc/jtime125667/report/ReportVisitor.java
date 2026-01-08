package it.unicam.cs.mpgc.jtime125667.report;

import it.unicam.cs.mpgc.jtime125667.model.*;

/**
 * Interfaccia che definisce il contratto per un visitatore nel pattern Visitor.
 * <p>
 *  Questo pattern comportamentale permette di separare l'algoritmo (es. la generazione di un report)
 *  dalla struttura degli oggetti su cui opera (Progetti e Task).
 * </p>
 * <p>
 *  Implementando questa interfaccia, è possibile creare nuovi tipi di report o operazioni
 *  (es. report testuale, report HTML, calcolo statistiche) senza dover modificare il codice
 *  delle classi {@link Project} o {@link Task}.
 * </p>
 */
public interface ReportVisitor {

    /**
     * Visita un oggetto di tipo {@link Project}.
     * <p>
     *  Questo metodo viene chiamato quando il visitatore viene accettato da un progetto.
     *  Qui deve essere implementata la logica per elaborare i dati del progetto
     *  (es. stampare il nome, la descrizione o calcolare totali).
     * </p>
     *
     * @param project Il progetto da visitare.
     */
    void visit(Project project);

    /**
     * Visita un oggetto di tipo {@link Task}.
     * <p>
     *  Questo metodo viene chiamato quando il visitatore viene accettato da un task.
     *  Qui deve essere implementata la logica per elaborare i dati dell'attività
     *  (es. stampare titolo, durata, stato di completamento).
     * </p>
     *
     * @param task Il task da visitare.
     */
    void visit(Task task);
}