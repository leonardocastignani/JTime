package it.unicam.cs.mpgc.jtime125667.persistence;

import org.hibernate.*;
import org.hibernate.cfg.*;

/**
 * Classe di utilità per la gestione della configurazione e della sessione di Hibernate.
 * <p>
 *  Questa classe implementa il pattern Singleton (tramite inizializzazione statica) per fornire
 *  un'unica istanza globale di {@link SessionFactory}. La SessionFactory è un oggetto pesante
 *  che non dovrebbe mai essere ricreato frequentemente.
 * </p>
 */
public class HibernateUtil {

    /**
     * L'unica istanza di SessionFactory, creata staticamente all'avvio dell'applicazione.
     */
    private static final SessionFactory sessionFactory = buildSessionFactory();

    /**
     * Costruisce la SessionFactory caricando la configurazione dal file XML.
     * 
     * @return La SessionFactory configurata.
     * @throws ExceptionInInitializerError Se la creazione fallisce (es. file cfg non trovato o errore DB).
     */
    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure("it/unicam/cs/mpgc/jtime125667/db/hibernate.cfg.xml").buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Restituisce l'istanza globale della SessionFactory.
     * Da utilizzare per aprire nuove sessioni (`openSession()`) per le operazioni sul database.
     *
     * @return La SessionFactory corrente.
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Chiude la SessionFactory e rilascia tutte le risorse (connessioni, cache).
     * <p>
     *  Questo metodo deve essere chiamato quando l'applicazione viene chiusa (es. nel metodo stop()
     *  di JavaFX) per garantire che i dati vengano salvati correttamente e i file di lock rilasciati.
     * </p>
     */
    public static void shutdown() {
        getSessionFactory().close();
    }
}