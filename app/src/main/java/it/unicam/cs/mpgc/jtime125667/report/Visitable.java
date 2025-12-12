package it.unicam.cs.mpgc.jtime125667.report;

public interface Visitable {
    void accept(ReportVisitor visitor);
}