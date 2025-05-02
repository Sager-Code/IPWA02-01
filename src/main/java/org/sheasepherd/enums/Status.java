package org.sheasepherd.enums;

/**
 * Enum für den Status eines Geisternetzes.
 */
public enum Status {
    REPORTED("Gemeldet"), 
    RESCUE_PENDING("Bergung bevorstehend"), 
    RESCUED("Geborgen"), 
    LOST("Verschollen");
    
    private final String displayName;
    
    Status(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}