package it.unicam.cs.mpgc.jtime125667.util;

import it.unicam.cs.mpgc.jtime125667.model.*;
import it.unicam.cs.mpgc.jtime125667.persistence.*;

/**
 * Servizio centralizzato per la gestione dell'accesso ai dati e della persistenza.
 * 
 * <p>
 *  Implementa il pattern <b>Singleton</b> per garantire un'unica istanza dei repository
 *  all'interno dell'applicazione, facilitando il coordinamento tra i vari componenti.
 * </p>
 */
public class DataService {

    /**
     * Unica istanza della classe {@code DataService}.
     */
    private static DataService instance;

    /**
     * Repository dedicato alla gestione della persistenza degli oggetti {@link ConcreteProject}.
     */
    private final Repository<ConcreteProject, String> projectRepository;

    /**
     * Costruttore privato per impedire l'istanziazione esterna della classe.
     * Inizializza il repository dei progetti utilizzando l'implementazione Hibernate.
     */
    private DataService() {
        this.projectRepository = new HibernateRepository<ConcreteProject>(ConcreteProject.class);
    }

    /**
     * Restituisce l'istanza unica di {@code DataService}. 
     * Se l'istanza non esiste, provvede alla sua creazione (Lazy Initialization).
     *
     * @return l'unica istanza di questa classe.
     */
    public static DataService getInstance() {
        if (instance == null) {
            instance = new DataService();
        }
        return instance;
    }

    /**
     * Fornisce l'accesso al repository per la gestione dei progetti.
     *
     * @return l'oggetto {@link Repository} configurato per {@link ConcreteProject}.
     */
    public Repository<ConcreteProject, String> getProjectRepository() {
        return projectRepository;
    }
}