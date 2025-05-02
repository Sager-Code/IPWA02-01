package org.sheasepherd.util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Klasse für den Zugriff auf CDI-Ressourcen.
 */
@ApplicationScoped
public class Resources {
    
    @Produces
    @PersistenceContext(unitName = "ghostNetPU")
    private EntityManager em;
}