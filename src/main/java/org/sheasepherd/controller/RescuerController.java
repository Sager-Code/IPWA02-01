package org.sheasepherd.controller;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import org.sheasepherd.dao.GhostNetDAO;
import org.sheasepherd.dao.RescuerDAO;
import org.sheasepherd.entity.GhostNet;
import org.sheasepherd.entity.Rescuer;
import org.sheasepherd.enums.Status;

import java.io.Serializable;
import java.util.List;

/**
 * Controller-Klasse für Berger.
 */
@Named
@ViewScoped
public class RescuerController implements Serializable {
    
    @EJB
    private RescuerDAO rescuerDAO;
    
    @EJB
    private GhostNetDAO ghostNetDAO;
    
    private Rescuer currentRescuer;
    private List<Rescuer> rescuers;
    private List<GhostNet> availableGhostNets;
    private List<GhostNet> assignedGhostNets;
    
    @PostConstruct
    public void init() {
        loadRescuers();
        loadAvailableGhostNets();
        resetCurrentRescuer();
    }
    
    /**
     * Lädt alle Berger aus der Datenbank.
     */
    public void loadRescuers() {
        rescuers = rescuerDAO.findAll();
    }
    
    /**
     * Lädt alle verfügbaren Geisternetze.
     */
    public void loadAvailableGhostNets() {
        availableGhostNets = ghostNetDAO.findAvailableForRescue();
    }
    
    /**
     * Lädt die einem Berger zugewiesenen Geisternetze.
     */
    public void loadAssignedGhostNets(Rescuer rescuer) {
        if (rescuer != null && rescuer.getId() != null) {
            Rescuer loadedRescuer = rescuerDAO.findById(rescuer.getId());
            if (loadedRescuer != null) {
                assignedGhostNets = loadedRescuer.getAssignedNets();
            }
        }
    }
    
    /**
     * Setzt den aktuellen Berger zurück.
     */
    public void resetCurrentRescuer() {
        currentRescuer = new Rescuer();
    }
    
    /**
     * Speichert einen neuen Berger.
     */
    public String saveRescuer() {
        try {
            if (currentRescuer.getId() == null) {
                rescuerDAO.create(currentRescuer);
                FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_INFO, 
                                "Erfolg", "Berger wurde erfolgreich erstellt."));
            } else {
                rescuerDAO.update(currentRescuer);
                FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_INFO, 
                                "Erfolg", "Berger wurde erfolgreich aktualisiert."));
            }
            
            loadRescuers();
            resetCurrentRescuer();
            
            return "list-ghostnets?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                            "Fehler", "Berger konnte nicht gespeichert werden: " + e.getMessage()));
            return null;
        }
    }
    
    /**
     * Meldet ein Geisternetz zur Bergung an.
     */
    public String assignGhostNetToRescuer(GhostNet ghostNet, Rescuer rescuer) {
        try {
            ghostNet.setRescuer(rescuer);
            ghostNet.setStatus(Status.RESCUE_PENDING);
            ghostNetDAO.update(ghostNet);
            
            loadAvailableGhostNets();
            loadAssignedGhostNets(rescuer);
            
            FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, 
                            "Erfolg", "Geisternetz wurde erfolgreich zur Bergung angemeldet."));
            
            return "rescue-ghostnet?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                            "Fehler", "Geisternetz konnte nicht zur Bergung angemeldet werden: " + e.getMessage()));
            return null;
        }
    }
    
    /**
     * Markiert ein Geisternetz als geborgen.
     */
    public String markGhostNetAsRescued(GhostNet ghostNet) {
        try {
            ghostNet.markAsRescued();
            ghostNetDAO.update(ghostNet);
            
            // Aktualisiere die Listen
            loadAvailableGhostNets();
            loadAssignedGhostNets(ghostNet.getRescuer());
            
            FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, 
                            "Erfolg", "Geisternetz wurde erfolgreich als geborgen markiert."));
            
            return "rescue-ghostnet?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                            "Fehler", "Geisternetz konnte nicht als geborgen markiert werden: " + e.getMessage()));
            return null;
        }
    }
    
    // Getter und Setter
    
    public Rescuer getCurrentRescuer() {
        return currentRescuer;
    }

    public void setCurrentRescuer(Rescuer currentRescuer) {
        this.currentRescuer = currentRescuer;
        if (currentRescuer != null && currentRescuer.getId() != null) {
            loadAssignedGhostNets(currentRescuer);
        }
    }

    public List<Rescuer> getRescuers() {
        return rescuers;
    }

    public List<GhostNet> getAvailableGhostNets() {
        return availableGhostNets;
    }

    public List<GhostNet> getAssignedGhostNets() {
        return assignedGhostNets;
    }
}