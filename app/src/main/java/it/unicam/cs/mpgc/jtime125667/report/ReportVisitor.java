package it.unicam.cs.mpgc.jtime125667.report;

import it.unicam.cs.mpgc.jtime125667.model.*;

public interface ReportVisitor {
    void visit(Project project);
    void visit(Task task);
}