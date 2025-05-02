package org.sheasepherd.controller;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import org.sheasepherd.dao.GhostNetDAO;
import org.sheasepherd.dao.ReporterDAO;
import org.sheasepherd.dao.RescuerDAO;
import org.sheasepherd.entity.GhostNet;
import org.sheasepherd.entity.Reporter;
import org.sheasepherd.entity.Rescuer;
import org.sheasepherd.enums.Status;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller-Klasse für Geisternetze.
 */
@Named
@ViewScoped
public class GhostNetController implements Serializable {
    
    @EJB
    private GhostNetDAO ghostNetDAO;
    
    @EJB
    private ReporterDAO reporterDAO;
    
    @EJB
    private RescuerDAO rescuerDAO;
    
    private GhostNet currentGhostNet;
    private Reporter currentReporter;
    private Rescuer selectedRescuer;
    private Long selectedRescuerId;
    private boolean anonymousReport;
    
    private List<GhostNet> ghostNets;
    private List<GhostNet> availableGhostNets;
    private List<Rescuer> rescuers;
    
    private String filterStatus = "ALL";
    
    // Ein einfaches Map-Objekt für Statistiken anstelle des PieChartModel
    private Map<String, Long> statisticsData = new HashMap<>();
    
    @PostConstruct
    public void init() {
        loadGhostNets();
        loadAvailableGhostNets();
        loadRescuers();
        resetCurrentGhostNet();
        resetCurrentReporter();
        createStatisticsData();
    }
    
    /**
     * Lädt alle Geisternetze aus der Datenbank.
     */
    public void loadGhostNets() {
        ghostNets = ghostNetDAO.findAll();
    }
    
    /**
     * Lädt alle verfügbaren (gemeldeten) Geisternetze aus der Datenbank.
     */
    public void loadAvailableGhostNets() {
        availableGhostNets = ghostNetDAO.findAvailableForRescue();
    }
    
    /**
     * Lädt alle Berger aus der Datenbank.
     */
    public void loadRescuers() {
        rescuers = rescuerDAO.findAll();
    }
    
    /**
     * Setzt das aktuelle Geisternetz zurück.
     */
    public void resetCurrentGhostNet() {
        currentGhostNet = new GhostNet();
    }
    
    /**
     * Setzt den aktuellen Reporter zurück.
     */
    public void resetCurrentReporter() {
        currentReporter = new Reporter();
    }
    
    /**
     * Meldet ein neues Geisternetz.
     */
    public String reportGhostNet() {
        try {
            // Wenn anonym, erstelle einen anonymen Reporter
            if (anonymousReport) {
                Reporter anonymousReporter = reporterDAO.createAnonymousReporter();
                currentGhostNet.setReporter(anonymousReporter);
            } else {
                // Prüfe ob Telefonnummer angegeben wurde, wenn nicht anonym
                if (currentReporter.getPhoneNumber() == null || currentReporter.getPhoneNumber().isBlank()) {
                    FacesContext.getCurrentInstance().addMessage(null, 
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                                    "Fehler", "Eine Telefonnummer ist erforderlich, wenn nicht anonym gemeldet wird."));
                    return null;
                }
                
                // Erstelle und setze den Reporter
                reporterDAO.create(currentReporter);
                currentGhostNet.setReporter(currentReporter);
            }
            
            // Erstelle das Geisternetz
            ghostNetDAO.create(currentGhostNet);
            
            // Aktualisiere die Listen
            loadGhostNets();
            loadAvailableGhostNets();
            createStatisticsData();
            
            // Setze Formulare zurück
            resetCurrentGhostNet();
            resetCurrentReporter();
            anonymousReport = false;
            
            FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, 
                            "Erfolg", "Geisternetz wurde erfolgreich gemeldet."));
            
            return "list-ghostnets?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                            "Fehler", "Geisternetz konnte nicht gemeldet werden: " + e.getMessage()));
            return null;
        }
    }
    
    /**
     * Weist ein Geisternetz einem Berger zu.
     */
    public String assignRescuer(GhostNet ghostNet) {
        try {
            if (selectedRescuerId == null) {
                FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                                "Fehler", "Bitte wählen Sie einen Berger aus."));
                return null;
            }
            
            Rescuer rescuer = rescuerDAO.findById(selectedRescuerId);
            if (rescuer == null) {
                FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                                "Fehler", "Der ausgewählte Berger wurde nicht gefunden."));
                return null;
            }
            
            ghostNet.setRescuer(rescuer);
            ghostNet.setStatus(Status.RESCUE_PENDING);
            ghostNetDAO.update(ghostNet);
            
            loadGhostNets();
            loadAvailableGhostNets();
            createStatisticsData();
            
            FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, 
                            "Erfolg", "Geisternetz wurde erfolgreich zugewiesen."));
            
            return "list-ghostnets?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                            "Fehler", "Geisternetz konnte nicht zugewiesen werden: " + e.getMessage()));
            return null;
        }
    }
    
    /**
     * Markiert ein Geisternetz als geborgen.
     */
    public String markAsRescued(GhostNet ghostNet) {
        try {
            ghostNet.markAsRescued();
            ghostNetDAO.update(ghostNet);
            
            loadGhostNets();
            loadAvailableGhostNets();
            createStatisticsData();
            
            FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, 
                            "Erfolg", "Geisternetz wurde erfolgreich als geborgen markiert."));
            
            return "list-ghostnets?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                            "Fehler", "Geisternetz konnte nicht als geborgen markiert werden: " + e.getMessage()));
            return null;
        }
    }
    
    /**
     * Markiert ein Geisternetz als verschollen.
     */
    public String markAsLost(GhostNet ghostNet) {
        try {
            // Prüfen, ob der Reporter anonym ist und das Netz als verschollen gemeldet werden soll
            if (ghostNet.getReporter() != null && ghostNet.getReporter().isAnonymous()) {
                FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                                "Fehler", "Anonym gemeldete Geisternetze können nicht als verschollen markiert werden."));
                return null;
            }
            
            ghostNet.markAsLost();
            ghostNetDAO.update(ghostNet);
            
            loadGhostNets();
            loadAvailableGhostNets();
            createStatisticsData();
            
            FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, 
                            "Erfolg", "Geisternetz wurde erfolgreich als verschollen markiert."));
            
            return "list-ghostnets?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                            "Fehler", "Geisternetz konnte nicht als verschollen markiert werden: " + e.getMessage()));
            return null;
        }
    }
    
    // Getter und Setter
    
    public GhostNet getCurrentGhostNet() {
        return currentGhostNet;
    }

    public void setCurrentGhostNet(GhostNet currentGhostNet) {
        this.currentGhostNet = currentGhostNet;
    }

    public Reporter getCurrentReporter() {
        return currentReporter;
    }

    public void setCurrentReporter(Reporter currentReporter) {
        this.currentReporter = currentReporter;
    }

    public Rescuer getSelectedRescuer() {
        return selectedRescuer;
    }

    public void setSelectedRescuer(Rescuer selectedRescuer) {
        this.selectedRescuer = selectedRescuer;
    }

    public Long getSelectedRescuerId() {
        return selectedRescuerId;
    }

    public void setSelectedRescuerId(Long selectedRescuerId) {
        this.selectedRescuerId = selectedRescuerId;
    }

    public boolean isAnonymousReport() {
        return anonymousReport;
    }

    public void setAnonymousReport(boolean anonymousReport) {
        this.anonymousReport = anonymousReport;
    }

    public List<GhostNet> getGhostNets() {
        return ghostNets;
    }

    public List<GhostNet> getAvailableGhostNets() {
        return availableGhostNets;
    }

    public List<Rescuer> getRescuers() {
        return rescuers;
    }
    
    public String getFilterStatus() {
        return filterStatus;
    }
    
    public void setFilterStatus(String filterStatus) {
        this.filterStatus = filterStatus;
    }
    
    public Map<String, Long> getStatisticsData() {
        return statisticsData;
    }
    
    /**
     * Filtert Geisternetze nach Status.
     */
    public void filterGhostNets() {
        if ("ALL".equals(filterStatus)) {
            loadGhostNets();
        } else {
            try {
                Status status = Status.valueOf(filterStatus);
                ghostNets = ghostNetDAO.findByStatus(status);
            } catch (IllegalArgumentException e) {
                // Wenn der Status nicht existiert, alle laden
                loadGhostNets();
            }
        }
    }
    
    /**
     * Erstellt Statistikdaten für die Anzahl der Geisternetze je Status.
     */
    private void createStatisticsData() {
        // Map leeren
        statisticsData.clear();
        
        // Geisternetze nach Status gruppieren und zählen
        Map<Status, Long> statusCounts = ghostNets.stream()
                .collect(Collectors.groupingBy(GhostNet::getStatus, Collectors.counting()));
        
        // Daten zur Map hinzufügen
        for (Status status : Status.values()) {
            Long count = statusCounts.getOrDefault(status, 0L);
            statisticsData.put(status.getDisplayName(), count);
        }
    }
}