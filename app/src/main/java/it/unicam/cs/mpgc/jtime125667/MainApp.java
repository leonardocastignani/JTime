package it.unicam.cs.mpgc.jtime125667;

import it.unicam.cs.mpgc.jtime125667.persistence.*;

import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.*;

import java.util.*;

/**
 * Classe principale (Entry Point) dell'applicazione JTime.
 * <p>
 *  Estende {@link Application} per avviare il ciclo di vita di JavaFX.
 *  Si occupa di:
 *  <ul>
 *      <li>Caricare e mostrare la prima schermata dell'interfaccia grafica (Lista Progetti).</li>
 *      <li>Configurare la finestra principale (Stage).</li>
 *      <li>Gestire la chiusura corretta delle risorse (es. Database) al termine dell'esecuzione.</li>
 *  </ul>
 * </p>
 */
public class MainApp extends Application {

    /**
     * Metodo di avvio dell'applicazione JavaFX.
     * <p>
     *  Viene chiamato automaticamente dal framework dopo l'inizializzazione.
     *  Qui carichiamo il file FXML della vista principale e lo impostiamo nello Stage.
     * </p>
     *
     * @param primaryStage Lo Stage principale (finestra) fornito da JavaFX.
     * @throws Exception Se si verificano errori durante il caricamento della risorsa FXML.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/it/unicam/cs/mpgc/jtime125667/view/ProjectList.fxml")));
        
        Scene scene = new Scene(root, 800, 600);
        
        primaryStage.setTitle("JTime - Project Management");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Metodo chiamato quando l'applicazione viene interrotta (chiusura finestra).
     * <p>
     *  È fondamentale per rilasciare le risorse. In particolare, chiude la {@code SessionFactory}
     *  di Hibernate per assicurare che tutte le transazioni siano completate e che il file
     *  di database (H2) venga rilasciato correttamente dal sistema operativo.
     * </p>
     *
     * @throws Exception Se si verificano errori durante lo shutdown.
     */
    @Override
    public void stop() throws Exception {
        HibernateUtil.shutdown();
        super.stop();
    }

    /**
     * Punto di ingresso standard per l'applicazione Java.
     * Lancia l'avvio del framework JavaFX.
     *
     * @param args Argomenti da riga di comando (eventualmente passati a JavaFX).
     */
    public static void main(String[] args) {
        launch(args);
    }
}