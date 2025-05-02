package org.sheasepherd.controller;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import org.sheasepherd.dao.ReporterDAO;
import org.sheasepherd.entity.Reporter;

import java.io.Serializable;
import java.util.List;

/**
 * Controller-Klasse für Reporter.
 */
@Named
@ViewScoped
public class ReporterController implements Serializable {
    
    @EJB
    private ReporterDAO reporterDAO;
    
    private Reporter currentReporter;
    private List<Reporter> reporters;
    private boolean anonymousReport;
    
    @PostConstruct
    public void init() {
        loadReporters();
        resetCurrentReporter();
    }
    
    /**
     * Lädt alle Reporter aus der Datenbank.
     */
    public void loadReporters() {
        reporters = reporterDAO.findAll();
    }
    
    /**
     * Setzt den aktuellen Reporter zurück.
     */
    public void resetCurrentReporter() {
        currentReporter = new Reporter();
    }
    
    /**
     * Speichert einen neuen Reporter.
     */
    public String saveReporter() {
        try {
            if (anonymousReport) {
                currentReporter.setAnonymous(true);
                currentReporter.setPhoneNumber(null);
                currentReporter.setName("Anonym");
            } else {
                // Prüfe, ob ein Name angegeben wurde
                if (currentReporter.getName() == null || currentReporter.getName().isBlank()) {
                    FacesContext.getCurrentInstance().addMessage(null, 
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                                    "Fehler", "Ein Name ist erforderlich, wenn nicht anonym gemeldet wird."));
                    return null;
                }
                
                // Prüfe, ob eine Telefonnummer angegeben wurde
                if (currentReporter.getPhoneNumber() == null || currentReporter.getPhoneNumber().isBlank()) {
                    FacesContext.getCurrentInstance().addMessage(null, 
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                                    "Fehler", "Eine Telefonnummer ist erforderlich, wenn nicht anonym gemeldet wird."));
                    return null;
                }
                
                currentReporter.setAnonymous(false);
            }
            
            if (currentReporter.getId() == null) {
                reporterDAO.create(currentReporter);
                FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_INFO, 
                                "Erfolg", "Reporter wurde erfolgreich erstellt."));
            } else {
                reporterDAO.update(currentReporter);
                FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_INFO, 
                                "Erfolg", "Reporter wurde erfolgreich aktualisiert."));
            }
            
            loadReporters();
            resetCurrentReporter();
            anonymousReport = false;
            
            return "report-ghostnet?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                            "Fehler", "Reporter konnte nicht gespeichert werden: " + e.getMessage()));
            return null;
        }
    }
    
    /**
     * Bereitet das Formular für einen anonymen Reporter vor.
     */
    public void prepareAnonymousReporter() {
        if (anonymousReport) {
            currentReporter.setName("Anonym");
            currentReporter.setPhoneNumber(null);
        } else {
            currentReporter.setName("");
            currentReporter.setPhoneNumber("");
        }
    }
    
    // Getter und Setter
    
    public Reporter getCurrentReporter() {
        return currentReporter;
    }

    public void setCurrentReporter(Reporter currentReporter) {
        this.currentReporter = currentReporter;
    }

    public List<Reporter> getReporters() {
        return reporters;
    }

    public boolean isAnonymousReport() {
        return anonymousReport;
    }

    public void setAnonymousReport(boolean anonymousReport) {
        this.anonymousReport = anonymousReport;
        prepareAnonymousReporter();
    }
}