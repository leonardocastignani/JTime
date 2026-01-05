package it.unicam.cs.mpgc.jtime125667.report;

import it.unicam.cs.mpgc.jtime125667.model.*;

public class TextReportVisitor implements ReportVisitor {

    private StringBuilder reportBuilder = new StringBuilder();

    @Override
    public void visit(Project project) {
        reportBuilder.append("PROGETTO: ").append(project.getName()).append("\n")
                     .append("Descrizione: ").append(project.getDescription()).append("\n")
                     .append("Stato: ").append(project.isCompleted() ? "Completato" : "In Corso")
                     .append("\n--------------------------------------------------\n");
    }

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

    public String getReport() {
        return reportBuilder.toString();
    }
}