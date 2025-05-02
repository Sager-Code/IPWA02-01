package org.sheasepherd.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity-Klasse für eine bergende Person (Rescuer).
 */
@Entity
@Table(name = "rescuers")
public class Rescuer implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    
    @OneToMany(mappedBy = "rescuer")
    private List<GhostNet> assignedNets = new ArrayList<>();
    
    // Konstruktoren
    public Rescuer() {
    }
    
    public Rescuer(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
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

    public List<GhostNet> getAssignedNets() {
        return assignedNets;
    }

    public void setAssignedNets(List<GhostNet> assignedNets) {
        this.assignedNets = assignedNets;
    }
    
    // Helper-Methoden für die Beziehung
    public void assignGhostNet(GhostNet ghostNet) {
        assignedNets.add(ghostNet);
        ghostNet.setRescuer(this);
    }
    
    public void unassignGhostNet(GhostNet ghostNet) {
        assignedNets.remove(ghostNet);
        ghostNet.setRescuer(null);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Rescuer rescuer = (Rescuer) o;
        
        return id != null ? id.equals(rescuer.id) : rescuer.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}