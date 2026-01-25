package it.unicam.cs.mpgc.jtime125667.util;

import it.unicam.cs.mpgc.jtime125667.model.*;

import javafx.geometry.*;
import javafx.scene.control.*;

public class TaskStatusCell extends TableCell<Task, String> {

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || getTableRow() == null || getTableRow().getItem() == null) {
            setText(null);
            setGraphic(null);
        } else {
            Task currentTask = getTableRow().getItem();
            boolean isDone = currentTask.isCompleted();

            Label badge = new Label(isDone ? "COMPLETATO" : "IN CORSO");

            if (isDone) {
                badge.setStyle("-fx-background-color: #d1f2eb; -fx-text-fill: #117864; -fx-background-radius: 5; -fx-padding: 4 8 4 8; -fx-font-weight: bold; -fx-font-size: 10px;");
            } else {
                badge.setStyle("-fx-background-color: #fcf3cf; -fx-text-fill: #b7950b; -fx-background-radius: 5; -fx-padding: 4 8 4 8; -fx-font-weight: bold; -fx-font-size: 10px;");
            }

            setText(null);
            setGraphic(badge);
            setAlignment(Pos.CENTER);
        }
    }
}