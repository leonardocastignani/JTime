package it.unicam.cs.mpgc.jtime125667.model;

import java.util.List;

public interface Project {
    String getId();
    String getName();
    String getDescription();
    List<Task> getTasks();
    void addTask(Task task);
    void removeTask(Task task);
    boolean isCompleted();
}