package it.unicam.cs.mpgc.jtime125667.report;

import it.unicam.cs.mpgc.jtime125667.model.*;

public class TextReportVisitor implements ReportVisitor {

    private final StringBuilder sb = new StringBuilder();

    public String getReport() { return sb.toString(); }

    @Override
    public void visit(Project project) {
        sb.append("========================================\n");
        sb.append("PROGETTO: ").append(project.getName()).append("\n");
        sb.append("Descrizione: ").append(project.getDescription()).append("\n");
        sb.append("Stato: ").append(project.isCompleted() ? "Completato" : "In Corso").append("\n");
        sb.append("----------------------------------------\n");
        sb.append("ATTIVITÀ:\n");
    }

    @Override
    public void visit(Task task) {
        sb.append(" - [")
          .append(task.isCompleted() ? "X" : " ")
          .append("] ")
          .append(task.getTitle());
        
        if (task.getScheduledDate() != null) {
            sb.append(" (Data: ").append(task.getScheduledDate()).append(")");
        }

        if (task.isCompleted()) {
            long estimated = task.getEstimatedDuration().toMinutes();
            long actual = task.getActualDuration().toMinutes();
            long diff = actual - estimated;
            
            sb.append("\n     [Stima: ").append(estimated).append("m | Reale: ").append(actual).append("m");
            if (diff > 0) sb.append(" | Ritardo: +").append(diff).append("m");
            else if (diff < 0) sb.append(" | Anticipo: ").append(diff).append("m");
            sb.append("]");
        }
        
        sb.append("\n");
    }
}