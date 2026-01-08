package it.unicam.cs.mpgc.jtime125667.util;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;

import java.io.*;
import java.util.function.*;

/**
 * Classe di utilità per la gestione della navigazione tra le scene (schermate) dell'applicazione.
 * <p>
 *  Questa classe fornisce metodi statici per caricare file FXML e cambiare la scena visualizzata
 *  nello Stage principale. Gestisce anche l'inizializzazione dei controller, permettendo
 *  il passaggio di dati tra una vista e l'altra.
 * </p>
 */
public class SceneManager {

    /**
     * Cambia la scena corrente caricando una nuova vista da un file FXML.
     * <p>
     *  Questo è un metodo di comodo da usare quando non è necessario passare dati
     *  o configurare il controller della vista di destinazione.
     * </p>
     *
     * @param sourceNode Un nodo della scena corrente (usato per recuperare il riferimento allo Stage).
     * @param fxmlPath   Il percorso del file FXML da caricare (es. "/views/MyView.fxml").
     */
    public static void changeScene(Node sourceNode, String fxmlPath) {
        changeScene(sourceNode, fxmlPath, null);
    }

    /**
     * Cambia la scena corrente caricando una nuova vista e configurandone il controller.
     * <p>
     *  Questo metodo utilizza i Generics per permettere di configurare qualsiasi tipo di controller.
     *  È fondamentale per passare oggetti (come il Progetto selezionato) alla nuova schermata
     *  prima che questa venga mostrata all'utente.
     * </p>
     *
     * @param sourceNode      Un nodo della scena corrente (usato per ottenere lo Stage).
     * @param fxmlPath        Il percorso del file FXML da caricare.
     * @param controllerSetup Un Consumer (funzione lambda) che riceve il controller della nuova vista
     *  e permette di chiamare i suoi metodi (es. {@code controller -> controller.setProject(...)}).
     *  Può essere {@code null} se non serve configurazione.
     * @param <T>             Il tipo del controller della vista di destinazione.
     */
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