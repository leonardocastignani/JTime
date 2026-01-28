package it.unicam.cs.mpgc.jtime125667.model;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.*;

class ConcreteProjectTest {

    @Test
    void testProjectCreation() {
        Project project = new ConcreteProject("Tesi Triennale", "Sviluppo App Java");
        assertEquals("Tesi Triennale", project.getName());
        assertEquals("Sviluppo App Java", project.getDescription());
        assertTrue(project.getTasks().isEmpty());
    }

    @Test
    void testAddAndRemoveTask() {
        Project project = new ConcreteProject("Test", "Desc");
        ConcreteTask task = new ConcreteTask("Analisi", "Analisi requisiti", Duration.ofMinutes(60), LocalDate.now());

        project.addTask(task);
        assertEquals(1, project.getTasks().size());

        project.removeTask(task);
        assertEquals(0, project.getTasks().size());
    }

    @Test
    void testProjectComplationLogic() {
        ConcreteProject project = new ConcreteProject("Test", "Desc");
        ConcreteTask task = new ConcreteTask("Task 1", "Desc", Duration.ofMinutes(30), LocalDate.now());

        project.addTask(task);

        assertFalse(task.isCompleted());
        assertFalse(project.isCompleted(), "Il progetto non deve risultare completato se ha task aperti.");
    }
}