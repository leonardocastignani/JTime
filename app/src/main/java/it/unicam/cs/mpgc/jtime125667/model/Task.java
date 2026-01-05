package it.unicam.cs.mpgc.jtime125667.model;

import it.unicam.cs.mpgc.jtime125667.report.*;

import java.time.*;
import java.util.*;

public interface Task extends Visitable {
    
    String getId();
    String getTitle();
    String getDescription();
    Duration getEstimatedDuration();
    Duration getActualDuration();
    boolean isCompleted();
    void complete(Duration actualDuration);
    LocalDate getScheduledDate();
    void setScheduledDate(LocalDate date);
    List<String> getTags();
    void addTag(String tag);
    
    // Metodi per supportare estensioni future (es. Visitor pattern per reportistica)
    // void accept(TaskVisitor visitor);
}