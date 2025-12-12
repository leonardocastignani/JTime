package it.unicam.cs.mpgc.jtime125667.model;

import it.unicam.cs.mpgc.jtime125667.report.*;
import java.util.*;

public interface Project extends Visitable {
    String getId();
    String getName();
    String getDescription();
    List<Task> getTasks();
    void addTask(Task task);
    void removeTask(Task task);
    boolean isCompleted();
}