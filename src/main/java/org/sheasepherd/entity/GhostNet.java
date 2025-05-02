package org.sheasepherd.entity;

import org.sheasepherd.enums.Status;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity-Klasse für ein Geisternetz.
 */
@Entity
@Table(name = "ghost_nets")
public class GhostNet implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "latitude", nullable = false)
    private Double latitude;
    
    @Column(name = "longitude", nullable = false)
    private Double longitude;
    
    @Column(name = "estimated_size", nullable = false)
    private String estimatedSize;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;
    
    @Column(name = "report_date", nullable = false)
    private LocalDateTime reportDate;
    
    @Column(name = "last_update_date")
    private LocalDateTime lastUpdateDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private Reporter reporter;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rescuer_id")
    private Rescuer rescuer;
    
    // Konstruktoren
    public GhostNet() {
        this.reportDate = LocalDateTime.now();
        this.status = Status.REPORTED;
    }
    
    public GhostNet(Double latitude, Double longitude, String estimatedSize) {
        this();
        this.latitude = latitude;
        this.longitude = longitude;
        this.estimatedSize = estimatedSize;
    }
    
    // Getter und Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getEstimatedSize() {
        return estimatedSize;
    }

    public void setEstimatedSize(String estimatedSize) {
        this.estimatedSize = estimatedSize;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        this.lastUpdateDate = LocalDateTime.now();
    }

    public LocalDateTime getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDateTime reportDate) {
        this.reportDate = reportDate;
    }

    public LocalDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(LocalDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public Reporter getReporter() {
        return reporter;
    }

    public void setReporter(Reporter reporter) {
        this.reporter = reporter;
    }

    public Rescuer getRescuer() {
        return rescuer;
    }

    public void setRescuer(Rescuer rescuer) {
        if (this.rescuer != null && this.rescuer != rescuer) {
            // Wenn bereits ein Rescuer zugewiesen ist, entferne dieses Netz von dessen Liste
            this.rescuer.getAssignedNets().remove(this);
        }
        this.rescuer = rescuer;
        
        // Status anpassen, wenn ein Rescuer zugewiesen wird
        if (rescuer != null && this.status == Status.REPORTED) {
            this.status = Status.RESCUE_PENDING;
            this.lastUpdateDate = LocalDateTime.now();
        }
    }
    
    /**
     * Markiert das Geisternetz als geborgen.
     */
    public void markAsRescued() {
        if (this.rescuer != null) {
            this.status = Status.RESCUED;
            this.lastUpdateDate = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Ein Geisternetz kann nur als geborgen markiert werden, " +
                    "wenn eine bergende Person zugewiesen ist.");
        }
    }
    
    /**
     * Markiert das Geisternetz als verschollen.
     */
    public void markAsLost() {
        this.status = Status.LOST;
        this.lastUpdateDate = LocalDateTime.now();
        // Wenn es einem Rescuer zugewiesen war, entfernen wir diese Zuweisung
        if (this.rescuer != null) {
            Rescuer oldRescuer = this.rescuer;
            this.rescuer = null;
            oldRescuer.getAssignedNets().remove(this);
        }
    }
    
    /**
     * Überprüft, ob dieses Geisternetz zur Bergung verfügbar ist.
     */
    public boolean isAvailableForRescue() {
        return this.status == Status.REPORTED;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        GhostNet ghostNet = (GhostNet) o;
        
        return id != null ? id.equals(ghostNet.id) : ghostNet.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}