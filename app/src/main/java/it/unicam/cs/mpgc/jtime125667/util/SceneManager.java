package it.unicam.cs.mpgc.jtime125667.util;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;

import java.io.*;
import java.util.function.*;

public class SceneManager {

    public static void changeScene(Node sourceNode, String fxmlPath) {
        changeScene(sourceNode, fxmlPath, null);
    }

    public static <T> void changeScene(Node sourceNode, String fxmlPath, Consumer<T> controllerSetup) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            if (controllerSetup != null) {
                T controller = loader.getController();
                controllerSetup.accept(controller);
            }

            Stage stage = (Stage) sourceNode.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Errore nel caricamento della vista: " + fxmlPath).show();
        }
    }
}