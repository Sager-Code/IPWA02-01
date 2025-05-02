package org.sheasepherd.util;

import jakarta.ejb.EJB;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Named;

import org.sheasepherd.dao.RescuerDAO;
import org.sheasepherd.entity.Rescuer;

/**
 * Converter für Rescuer-Objekte in JSF-Komponenten.
 */
@Named
@FacesConverter(value = "rescuerConverter", managed = true)
public class RescuerConverter implements Converter<Rescuer> {
    
    @EJB
    private RescuerDAO rescuerDAO;
    
    @Override
    public Rescuer getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        
        try {
            Long id = Long.valueOf(value);
            return rescuerDAO.findById(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    @Override
    public String getAsString(FacesContext context, UIComponent component, Rescuer rescuer) {
        if (rescuer == null || rescuer.getId() == null) {
            return "";
        }
        
        return rescuer.getId().toString();
    }
}