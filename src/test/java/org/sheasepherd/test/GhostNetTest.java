package org.sheasepherd.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sheasepherd.entity.GhostNet;
import org.sheasepherd.entity.Reporter;
import org.sheasepherd.entity.Rescuer;
import org.sheasepherd.enums.Status;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test-Klasse für die GhostNet-Entität und deren Funktionalitäten.
 */
public class GhostNetTest {
    
    private GhostNet ghostNet;
    private Reporter reporter;
    private Rescuer rescuer;
    
    @BeforeEach
    public void setUp() {
        // Testdaten initialisieren
        ghostNet = new GhostNet(54.12345, 10.98765, "20m²");
        
        reporter = new Reporter("Test Reporter", "+49123456789", false);
        reporter.setId(1L);
        
        rescuer = new Rescuer("Test Rescuer", "+49987654321");
        rescuer.setId(1L);
    }
    
    @Test
    public void testGhostNetInitialization() {
        assertNotNull(ghostNet);
        assertEquals(54.12345, ghostNet.getLatitude());
        assertEquals(10.98765, ghostNet.getLongitude());
        assertEquals("20m²", ghostNet.getEstimatedSize());
        assertEquals(Status.REPORTED, ghostNet.getStatus());
        assertNotNull(ghostNet.getReportDate());
    }
    
    @Test
    public void testReporterAssignment() {
        ghostNet.setReporter(reporter);
        assertEquals(reporter, ghostNet.getReporter());
        assertFalse(ghostNet.getReporter().isAnonymous());
    }
    
    @Test
    public void testAnonymousReporter() {
        Reporter anonymousReporter = new Reporter("Anonym", null, true);
        ghostNet.setReporter(anonymousReporter);
        
        assertEquals(anonymousReporter, ghostNet.getReporter());
        assertTrue(ghostNet.getReporter().isAnonymous());
        assertNull(ghostNet.getReporter().getPhoneNumber());
    }
    
    @Test
    public void testRescuerAssignment() {
        ghostNet.setRescuer(rescuer);
        
        assertEquals(rescuer, ghostNet.getRescuer());
        assertEquals(Status.RESCUE_PENDING, ghostNet.getStatus());
        assertNotNull(ghostNet.getLastUpdateDate());
    }
    
    @Test
    public void testMarkAsRescued() {
        // Zuerst einen Rescuer zuweisen
        ghostNet.setRescuer(rescuer);
        
        // Dann als geborgen markieren
        ghostNet.markAsRescued();
        
        assertEquals(Status.RESCUED, ghostNet.getStatus());
        assertNotNull(ghostNet.getLastUpdateDate());
    }
    
    @Test
    public void testMarkAsRescuedWithoutRescuer() {
        // Versuchen, ein Netz ohne Rescuer als geborgen zu markieren
        assertThrows(IllegalStateException.class, () -> {
            ghostNet.markAsRescued();
        });
    }
    
    @Test
    public void testMarkAsLost() {
        // Zuerst einen Rescuer zuweisen
        ghostNet.setRescuer(rescuer);
        
        // Dann als verschollen markieren
        ghostNet.markAsLost();
        
        assertEquals(Status.LOST, ghostNet.getStatus());
        assertNull(ghostNet.getRescuer());
        assertNotNull(ghostNet.getLastUpdateDate());
    }
    
    @Test
    public void testRescuerReassignment() {
        // Ersten Rescuer zuweisen
        ghostNet.setRescuer(rescuer);
        assertEquals(rescuer, ghostNet.getRescuer());
        
        // Zweiten Rescuer erstellen und zuweisen
        Rescuer newRescuer = new Rescuer("New Rescuer", "+49111222333");
        newRescuer.setId(2L);
        
        // Das Netz sollte automatisch vom alten Rescuer entfernt werden
        ghostNet.setRescuer(newRescuer);
        
        assertEquals(newRescuer, ghostNet.getRescuer());
    }
    
    @Test
    public void testAvailableForRescue() {
        // Initial sollte das Netz verfügbar sein
        assertTrue(ghostNet.isAvailableForRescue());
        
        // Nach Zuweisung eines Rescuers sollte es nicht mehr verfügbar sein
        ghostNet.setRescuer(rescuer);
        assertFalse(ghostNet.isAvailableForRescue());
        
        // Wenn als verschollen markiert, sollte es auch nicht verfügbar sein
        GhostNet anotherNet = new GhostNet(53.54321, 11.12345, "15m²");
        anotherNet.markAsLost();
        assertFalse(anotherNet.isAvailableForRescue());
    }
    
    @Test
    public void testStatusTransitions() {
        // Testen aller möglichen Status-Übergänge
        
        // Initial REPORTED
        assertEquals(Status.REPORTED, ghostNet.getStatus());
        
        // REPORTED -> RESCUE_PENDING
        ghostNet.setRescuer(rescuer);
        assertEquals(Status.RESCUE_PENDING, ghostNet.getStatus());
        
        // RESCUE_PENDING -> RESCUED
        ghostNet.markAsRescued();
        assertEquals(Status.RESCUED, ghostNet.getStatus());
        
        // Reset für nächsten Test
        ghostNet = new GhostNet(54.12345, 10.98765, "20m²");
        ghostNet.setRescuer(rescuer);
        
        // RESCUE_PENDING -> LOST
        ghostNet.markAsLost();
        assertEquals(Status.LOST, ghostNet.getStatus());
        
        // Reset für nächsten Test
        ghostNet = new GhostNet(54.12345, 10.98765, "20m²");
        
        // REPORTED -> LOST
        ghostNet.markAsLost();
        assertEquals(Status.LOST, ghostNet.getStatus());
    }
    
    @Test
    public void testEqualsAndHashCode() {
        GhostNet net1 = new GhostNet(54.12345, 10.98765, "20m²");
        GhostNet net2 = new GhostNet(54.12345, 10.98765, "20m²");
        
        // Ohne IDs sollten sie gleich sein (basierend auf der Implementierung)
        assertEquals(net1, net1);
        
        // Mit verschiedenen IDs
        net1.setId(1L);
        net2.setId(2L);
        
        assertNotEquals(net1, net2);
        assertNotEquals(net1.hashCode(), net2.hashCode());
        
        // Mit gleichen IDs
        net2.setId(1L);
        assertEquals(net1, net2);
        assertEquals(net1.hashCode(), net2.hashCode());
    }
}