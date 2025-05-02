package org.sheasepherd.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.sheasepherd.entity.Reporter;

import java.util.List;

/**
 * DAO-Klasse für den Zugriff auf Reporter in der Datenbank.
 */
@Stateless
public class ReporterDAO {
    
    @PersistenceContext(unitName = "ghostNetPU")
    private EntityManager em;
    
    /**
     * Speichert einen neuen Reporter in der Datenbank.
     */
    public void create(Reporter reporter) {
        em.persist(reporter);
    }
    
    /**
     * Aktualisiert einen bestehenden Reporter in der Datenbank.
     */
    public Reporter update(Reporter reporter) {
        return em.merge(reporter);
    }
    
    /**
     * Findet einen Reporter anhand seiner ID.
     */
    public Reporter findById(Long id) {
        return em.find(Reporter.class, id);
    }
    
    /**
     * Gibt alle Reporter zurück.
     */
    public List<Reporter> findAll() {
        TypedQuery<Reporter> query = em.createQuery("SELECT r FROM Reporter r", Reporter.class);
        return query.getResultList();
    }
    
    /**
     * Sucht Reporter nach Namen.
     */
    public List<Reporter> findByName(String name) {
        TypedQuery<Reporter> query = em.createQuery(
                "SELECT r FROM Reporter r WHERE LOWER(r.name) LIKE LOWER(:name)", 
                Reporter.class);
        query.setParameter("name", "%" + name + "%");
        return query.getResultList();
    }
    
    /**
     * Löscht einen Reporter aus der Datenbank.
     */
    public void delete(Reporter reporter) {
        if (!em.contains(reporter)) {
            reporter = em.merge(reporter);
        }
        em.remove(reporter);
    }
    
    /**
     * Erstellt einen anonymen Reporter.
     */
    public Reporter createAnonymousReporter() {
        Reporter anonymousReporter = new Reporter("Anonym", null, true);
        create(anonymousReporter);
        return anonymousReporter;
    }
}