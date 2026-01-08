package it.unicam.cs.mpgc.jtime125667.model;

import it.unicam.cs.mpgc.jtime125667.report.*;

import java.time.*;
import java.util.*;

public interface Task extends Visitable {
    
    String getId();
    String getTitle();
    void setTitle(String title);
    String getDescription();
    void setDescription(String description);
    Duration getEstimatedDuration();
    void setEstimatedDuration(Duration estimatedDuration);
    Duration getActualDuration();
    boolean isCompleted();
    void complete(Duration actualDuration);
    LocalDate getScheduledDate();
    void setScheduledDate(LocalDate date);
    List<String> getTags();
    void addTag(String tag);
}