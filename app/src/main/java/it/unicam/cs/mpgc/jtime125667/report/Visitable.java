package it.unicam.cs.mpgc.jtime125667.report;

/**
 * Interfaccia che definisce l'elemento "Visitabile" nel pattern Visitor.
 * <p>
 *  Qualsiasi classe che implementa questa interfaccia dichiara di poter accettare
 *  un visitatore (in questo caso un {@link ReportVisitor}).
 *  Questo meccanismo è utilizzato per separare l'algoritmo (es. generazione report)
 *  dalla struttura degli oggetti (es. Progetti e Task).
 * </p>
 */
public interface Visitable {

    /**
     * Accetta un visitatore.
     * <p>
     *  Questo metodo è il punto di ingresso per il pattern Visitor.
     *  L'implementazione tipica consisterà in una singola chiamata:
     *  {@code visitor.visit(this);}
     *  che permette al visitatore di eseguire l'operazione appropriata
     *  per il tipo specifico dell'oggetto chiamante (Double Dispatch).
     * </p>
     *
     * @param visitor Il visitatore che eseguirà l'operazione sull'oggetto corrente.
     */
    void accept(ReportVisitor visitor);
}