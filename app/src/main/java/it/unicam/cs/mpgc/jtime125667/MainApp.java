package it.unicam.cs.mpgc.jtime125667;

import it.unicam.cs.mpgc.jtime125667.persistence.*;
import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.*;

import java.util.*;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/it/unicam/cs/mpgc/jtime125667/view/ProjectList.fxml")));
        
        Scene scene = new Scene(root, 800, 600);
        
        primaryStage.setTitle("JTime - Project Management");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        HibernateUtil.shutdown(); // Chiude la connessione quando chiudi la finestra
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}