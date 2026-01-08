package it.unicam.cs.mpgc.jtime125667.persistence;

import org.hibernate.*;

import java.util.*;
import java.util.logging.*;

/**
 * Implementazione generica dell'interfaccia {@link Repository} basata su Hibernate.
 * <p>
 *  Questa classe fornisce l'implementazione concreta delle operazioni CRUD (Create, Read, Update, Delete)
 *  per qualsiasi entità gestita da Hibernate. Utilizza i Generics (&lt;T&gt;) per essere riutilizzabile
 *  con qualsiasi tipo di entità (es. Project, Task).
 * </p>
 * <p>
 *  Gestisce automaticamente:
 *  <ul>
 *      <li>L'apertura e chiusura delle Sessioni Hibernate.</li>
 *      <li>Il ciclo di vita delle Transazioni (commit/rollback).</li>
 *      <li>Il logging degli errori in caso di fallimento.</li>
 *  </ul>
 * </p>
 *
 * @param <T> Il tipo dell'entità gestita (es. ConcreteProject).
 */
public class HibernateRepository<T> implements Repository<T, String> {

    /**
     * Riferimento alla classe dell'entità specifica.
     * Necessario perché in Java i tipi generici vengono cancellati a runtime (Type Erasure),
     * quindi dobbiamo passare esplicitamente la classe per le query Hibernate.
     */
    private final Class<T> entityClass;

    /**
     * Logger per registrare errori e warning.
     * Meglio di System.out.println perché permette di configurare livelli e output.
     */
    private static final Logger logger = Logger.getLogger(HibernateRepository.class.getName());

    /**
     * Costruttore del repository.
     *
     * @param entityClass La classe dell'entità che questo repository gestirà.
     */
    public HibernateRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Cerca un'entità nel database tramite il suo identificativo univoco.
     *
     * @param id L'identificativo dell'entità da cercare.
     * @return L'entità trovata, oppure null se non esiste.
     */
    @Override
    public T findById(String id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(entityClass, id);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Errore durante la ricerca per ID: " + id, e);
            return null;
        }
    }

    /**
     * Recupera tutte le istanze dell'entità presenti nel database.
     *
     * @return Una lista contenente tutte le entità trovate. Se vuota o in caso di errore, restituisce una lista vuota.
     */
    @Override
    public List<T> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from " + entityClass.getName(), entityClass).list();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Errore durante il recupero di tutti gli elementi", e);
            return List.of();
        }
    }

    /**
     * Salva o aggiorna un'entità nel database.
     * <p>
     *  Se l'entità non esiste (nuovo ID), viene inserita (INSERT).
     *  Se esiste già, viene aggiornata (UPDATE).
     *  L'operazione è transazionale: o riesce completamente o viene annullata.
     * </p>
     *
     * @param entity L'entità da salvare.
     * @throws RuntimeException se il salvataggio fallisce.
     */
    @Override
    public void save(T entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.log(Level.SEVERE, "Errore durante il salvataggio dell'entità", e);
            throw new RuntimeException("Salvataggio fallito", e);
        }
    }

    /**
     * Elimina un'entità dal database.
     * Anche questa operazione è transazionale.
     *
     * @param entity L'entità da rimuovere.
     * @throws RuntimeException se l'eliminazione fallisce.
     */
    @Override
    public void delete(T entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.remove(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.log(Level.SEVERE, "Errore durante l'eliminazione dell'entità", e);
            throw new RuntimeException("Eliminazione fallita", e);
        }
    }
}