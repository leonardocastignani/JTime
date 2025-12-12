package it.unicam.cs.mpgc.jtime125667.persistence;

import java.util.*;

public interface Repository<T, ID> {
    void save(T entity);
    T findById(ID id);
    List<T> findAll();
    void delete(T entity);
}