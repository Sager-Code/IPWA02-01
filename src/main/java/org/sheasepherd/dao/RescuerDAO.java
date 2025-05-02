package org.sheasepherd.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.sheasepherd.entity.Rescuer;

import java.util.List;

/**
 * DAO-Klasse für den Zugriff auf Rescuer in der Datenbank.
 */
@Stateless
public class RescuerDAO {
    
    @PersistenceContext(unitName = "ghostNetPU")
    private EntityManager em;
    
    /**
     * Speichert einen neuen Rescuer in der Datenbank.
     */
    public void create(Rescuer rescuer) {
        em.persist(rescuer);
    }
    
    /**
     * Aktualisiert einen bestehenden Rescuer in der Datenbank.
     */
    public Rescuer update(Rescuer rescuer) {
        return em.merge(rescuer);
    }
    
    /**
     * Findet einen Rescuer anhand seiner ID.
     */
    public Rescuer findById(Long id) {
        return em.find(Rescuer.class, id);
    }
    
    /**
     * Gibt alle Rescuer zurück.
     */
    public List<Rescuer> findAll() {
        TypedQuery<Rescuer> query = em.createQuery("SELECT r FROM Rescuer r", Rescuer.class);
        return query.getResultList();
    }
    
    /**
     * Sucht Rescuer nach Namen.
     */
    public List<Rescuer> findByName(String name) {
        TypedQuery<Rescuer> query = em.createQuery(
                "SELECT r FROM Rescuer r WHERE LOWER(r.name) LIKE LOWER(:name)", 
                Rescuer.class);
        query.setParameter("name", "%" + name + "%");
        return query.getResultList();
    }
    
    /**
     * Löscht einen Rescuer aus der Datenbank.
     */
    public void delete(Rescuer rescuer) {
        if (!em.contains(rescuer)) {
            rescuer = em.merge(rescuer);
        }
        em.remove(rescuer);
    }
}