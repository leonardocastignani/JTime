package it.unicam.cs.mpgc.jtime125667.report;

import it.unicam.cs.mpgc.jtime125667.model.*;

/**
 * Implementazione concreta di {@link ReportVisitor} che genera un report testuale semplice.
 * <p>
 *  Questa classe attraversa la struttura di un progetto e dei suoi task, accumulando
 *  le informazioni in una stringa formattata. È utile per visualizzare un riepilogo
 *   leggibile o per esportare i dati in un file di testo (.txt).
 * </p>
 */
public class TextReportVisitor implements ReportVisitor {

    /**
     * Accumulatore per il testo del report.
     * Si usa StringBuilder per efficienza nelle operazioni di concatenazione ripetute.
     */
    private StringBuilder reportBuilder = new StringBuilder();

    /**
     * Visita un progetto e ne formatta l'intestazione.
     * <p>
     *  Stampa il nome, la descrizione e lo stato del progetto, seguiti da un separatore.
     *  I task verranno aggiunti successivamente quando il progetto delegherà la visita ai suoi figli.
     * </p>
     *
     * @param project Il progetto da visitare.
     */
    @Override
    public void visit(Project project) {
        reportBuilder.append("PROGETTO: ").append(project.getName()).append("\n")
                     .append("Descrizione: ").append(project.getDescription()).append("\n")
                     .append("Stato: ").append(project.isCompleted() ? "Completato" : "In Corso")
                     .append("\n--------------------------------------------------\n");
    }

    /**
     * Visita un task e ne formatta i dettagli come una voce di elenco.
     * <p>
     *  Il formato prodotto è:
     *  <pre>
     *      - [X] Titolo Task (Stimati: 60m, Effettivi: 50m)
     *      Tags: java, backend
     *  </pre>
     * </p>
     *
     * @param task Il task da visitare.
     */
    @Override
    public void visit(Task task) {
        reportBuilder.append(" - [")
                     .append(task.isCompleted() ? "X" : " ")
                     .append("] ")
                     .append(task.getTitle())
                     .append(" (Stimati: ").append(task.getEstimatedDuration().toMinutes()).append("m");

        if (task.isCompleted()) {
            reportBuilder.append(", Effettivi: ").append(task.getActualDuration().toMinutes()).append("m");
        }
        
        reportBuilder.append(")\n");

        if (!task.getTags().isEmpty()) {
            String tags = String.join(", ", task.getTags());
            reportBuilder.append("   Tags: ").append(tags).append("\n");
        }
        
        reportBuilder.append("\n");
    }

    /**
     * Restituisce il testo completo del report generato.
     *
     * @return Una stringa contenente tutto il report formattato.
     */
    public String getReport() {
        return reportBuilder.toString();
    }
}