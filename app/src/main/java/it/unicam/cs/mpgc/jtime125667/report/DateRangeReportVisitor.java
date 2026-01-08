package it.unicam.cs.mpgc.jtime125667.report;

import it.unicam.cs.mpgc.jtime125667.model.*;

import java.time.*;

/**
 * Implementazione del pattern Visitor per la generazione di report basati su un intervallo temporale.
 * <p>
 *  Questo visitor attraversa la struttura dei progetti e dei task, ma raccoglie e formatta
 *  solo le attività (Task) che sono pianificate all'interno di un range di date specifico.
 *  È utilizzato principalmente per generare il report giornaliero o settimanale nell'Agenda.
 * </p>
 */
public class DateRangeReportVisitor implements ReportVisitor {

    private final LocalDate start;
    private final LocalDate end;

    /**
     * StringBuilder usato per accumulare il testo del report man mano che si visitano gli elementi.
     */
    private final StringBuilder sb = new StringBuilder();

    /**
     * Costruisce un nuovo visitor per il range di date specificato.
     *
     * @param start La data di inizio dell'intervallo (inclusa).
     * @param end   La data di fine dell'intervallo (inclusa).
     */
    public DateRangeReportVisitor(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
        sb.append("Report Attività dal ").append(start).append(" al ").append(end).append("\n\n");
    }

    /**
     * Restituisce il testo completo del report generato finora.
     *
     * @return Una stringa contenente il report formattato.
     */
    public String getReport() { return sb.toString(); }

    /**
     * Visita un progetto.
     * <p>
     *  In questo tipo di report, non siamo interessati ai dettagli del progetto in sé,
     *  ma dobbiamo attraversarlo per accedere ai suoi task. Quindi, questo metodo
     *  delega semplicemente la visita a tutti i task contenuti nel progetto.
     * </p>
     *
     * @param project Il progetto da visitare.
     */
    @Override
    public void visit(Project project) {
        for (Task task : project.getTasks()) {
            task.accept(this);
        }
    }

    /**
     * Visita un task e, se rientra nell'intervallo di date, lo aggiunge al report.
     * <p>
     *  Verifica se la data pianificata (`scheduledDate`) del task è compresa tra
     *  `start` ed `end`. Se sì, aggiunge titolo, stato e tag al report.
     * </p>
     *
     * @param task Il task da visitare ed eventualmente includere nel report.
     */
    @Override
    public void visit(Task task) {
        LocalDate date = task.getScheduledDate();
        if (date != null && !date.isBefore(start) && !date.isAfter(end)) {
            sb.append(date).append(": ").append(task.getTitle())
              .append(" (").append(task.isCompleted() ? "Completato" : "In corso").append(")\n");

            if (!task.getTags().isEmpty()) {
                String tags = String.join(", ", task.getTags());
                sb.append("   Tags: ").append(tags).append("\n");
            }
        }
    }
}