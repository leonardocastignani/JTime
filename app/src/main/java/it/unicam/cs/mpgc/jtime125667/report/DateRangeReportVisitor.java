package it.unicam.cs.mpgc.jtime125667.report;

import it.unicam.cs.mpgc.jtime125667.model.*;
import java.time.LocalDate;

public class DateRangeReportVisitor implements ReportVisitor {
    private final LocalDate start;
    private final LocalDate end;
    private final StringBuilder sb = new StringBuilder();

    public DateRangeReportVisitor(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
        sb.append("Report Attività dal ").append(start).append(" al ").append(end).append("\n\n");
    }

    public String getReport() { return sb.toString(); }

    @Override
    public void visit(Project project) {
        // Non stampiamo il progetto a meno che non abbia task nel range, 
        // ma per semplicità visitiamo solo i figli.
        for (Task task : project.getTasks()) {
            task.accept(this);
        }
    }

    @Override
    public void visit(Task task) {
        LocalDate date = task.getScheduledDate();
        if (date != null && !date.isBefore(start) && !date.isAfter(end)) {
            sb.append(date).append(": ").append(task.getTitle())
              .append(" (").append(task.isCompleted() ? "Completato" : "In corso").append(")\n");
        }
    }
}