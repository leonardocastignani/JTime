package it.unicam.cs.mpgc.jtime125667.persistence;

import org.hibernate.*;

import java.util.*;
import java.util.logging.*;

public class HibernateRepository<T> implements Repository<T, String> {

    private final Class<T> entityClass;
    private static final Logger logger = Logger.getLogger(HibernateRepository.class.getName());

    public HibernateRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public T findById(String id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(entityClass, id);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Errore durante la ricerca per ID: " + id, e);
            return null;
        }
    }

    @Override
    public List<T> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from " + entityClass.getName(), entityClass).list();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Errore durante il recupero di tutti gli elementi", e);
            return List.of();
        }
    }

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
            throw new RuntimeException("Salvataggio fallito", e); // Rilancia per gestire nella UI
        }
    }

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