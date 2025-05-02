package org.sheasepherd.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity-Klasse für eine meldende Person (Reporter).
 */
@Entity
@Table(name = "reporters")
public class Reporter implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "is_anonymous")
    private boolean isAnonymous;
    
    @OneToMany(mappedBy = "reporter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GhostNet> reportedNets = new ArrayList<>();
    
    // Konstruktoren
    public Reporter() {
    }
    
    public Reporter(String name, String phoneNumber, boolean isAnonymous) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.isAnonymous = isAnonymous;
    }
    
    // Getter und Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }

    public List<GhostNet> getReportedNets() {
        return reportedNets;
    }

    public void setReportedNets(List<GhostNet> reportedNets) {
        this.reportedNets = reportedNets;
    }
    
    // Helper-Methoden für die Beziehung
    public void addGhostNet(GhostNet ghostNet) {
        reportedNets.add(ghostNet);
        ghostNet.setReporter(this);
    }
    
    public void removeGhostNet(GhostNet ghostNet) {
        reportedNets.remove(ghostNet);
        ghostNet.setReporter(null);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Reporter reporter = (Reporter) o;
        
        return id != null ? id.equals(reporter.id) : reporter.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}