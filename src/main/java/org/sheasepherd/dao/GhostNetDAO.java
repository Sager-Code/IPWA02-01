package org.sheasepherd.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.sheasepherd.entity.GhostNet;
import org.sheasepherd.enums.Status;

import java.util.List;

/**
 * DAO-Klasse für den Zugriff auf Geisternetze in der Datenbank.
 */
@Stateless
public class GhostNetDAO {
    
    @PersistenceContext(unitName = "ghostNetPU")
    private EntityManager em;
    
    /**
     * Speichert ein neues Geisternetz in der Datenbank.
     */
    public void create(GhostNet ghostNet) {
        em.persist(ghostNet);
    }
    
    /**
     * Aktualisiert ein bestehendes Geisternetz in der Datenbank.
     */
    public GhostNet update(GhostNet ghostNet) {
        return em.merge(ghostNet);
    }
    
    /**
     * Findet ein Geisternetz anhand seiner ID.
     */
    public GhostNet findById(Long id) {
        return em.find(GhostNet.class, id);
    }
    
    /**
     * Gibt alle Geisternetze zurück.
     */
    public List<GhostNet> findAll() {
        TypedQuery<GhostNet> query = em.createQuery("SELECT g FROM GhostNet g ORDER BY g.reportDate DESC", GhostNet.class);
        return query.getResultList();
    }
    
    /**
     * Gibt alle Geisternetze mit einem bestimmten Status zurück.
     */
    public List<GhostNet> findByStatus(Status status) {
        TypedQuery<GhostNet> query = em.createQuery(
                "SELECT g FROM GhostNet g WHERE g.status = :status ORDER BY g.reportDate DESC", 
                GhostNet.class);
        query.setParameter("status", status);
        return query.getResultList();
    }
    
    /**
     * Gibt alle Geisternetze zurück, die zur Bergung verfügbar sind (Status REPORTED).
     */
    public List<GhostNet> findAvailableForRescue() {
        return findByStatus(Status.REPORTED);
    }
    
    /**
     * Löscht ein Geisternetz aus der Datenbank.
     */
    public void delete(GhostNet ghostNet) {
        if (!em.contains(ghostNet)) {
            ghostNet = em.merge(ghostNet);
        }
        em.remove(ghostNet);
    }
}