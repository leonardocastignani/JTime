package it.unicam.cs.mpgc.jtime125667.persistence;

import java.util.*;

/**
 * Interfaccia generica che definisce il contratto per la persistenza dei dati (Pattern Repository).
 * <p>
 *  Il Repository agisce come una collezione in memoria di oggetti di dominio.
 *  Astrae i dettagli sottostanti di accesso ai dati (come query SQL, file system, ecc.),
 *  permettendo al resto dell'applicazione di lavorare con oggetti Java standard senza
 *  dipendere da una specifica tecnologia di database.
 * </p>
 *
 * @param <T>  Il tipo dell'entità di dominio gestita dal repository (es. {@code ConcreteProject}).
 * @param <ID> Il tipo dell'identificativo univoco dell'entità (es. {@code String}, {@code Long}).
 */
public interface Repository<T, ID> {

    /**
     * Salva una nuova entità o aggiorna un'entità esistente nel sistema di persistenza.
     * <p>
     *  Se l'entità ha già un ID esistente, i suoi dati verranno aggiornati.
     *  Altrimenti, verrà creata una nuova voce.
     * </p>
     *
     * @param entity L'entità da salvare o aggiornare.
     */
    void save(T entity);

    /**
     * Cerca e restituisce un'entità basandosi sul suo identificativo univoco.
     *
     * @param id L'identificativo dell'entità da recuperare.
     * @return L'entità trovata, oppure {@code null} se non esiste alcuna entità con quell'ID.
     */
    T findById(ID id);

    /**
     * Recupera tutte le entità di tipo {@code T} presenti nel repository.
     *
     * @return Una lista contenente tutte le entità trovate. Restituisce una lista vuota se non c'è nulla.
     */
    List<T> findAll();

    /**
     * Elimina l'entità specificata dal sistema di persistenza.
     *
     * @param entity L'entità da rimuovere.
     */
    void delete(T entity);
}