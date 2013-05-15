/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package SOSCalendar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author xafilox
 */
@Entity
@XmlRootElement
public class DateResponse implements Serializable {
    private static final long serialVersionUID = 1L;
     @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_comienzo")
    @Temporal(TemporalType.DATE)
    private Date fechaComienzo;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_finalizado")
    @Temporal(TemporalType.DATE)
    private Date fechaFinalizado;   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "date_id")
    private Integer dateId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 120)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "lugar")
    private String lugar;    
    private CalendarUri calendarUri;
    private UserUri userUri;    

    public DateResponse() {
    }

    public DateResponse(Dates date) {

	this.setDateId(date.getDateId());
	
	this.setName(date.getName());
	
	this.setCalendarUri(date.getCalendarId().toUri());
        this.setUserUri(date.getCalendarId().getUserId().toUri());
	   
	this.setDescription(date.getDescription());
        this.setLugar(date.getLugar());
	this.setFechaComienzo(date.getFechaComienzo());
        this.setFechaFinalizado(date.getFechaFinalizado());
	
    }

    
    public Integer getDateId() {
	return dateId;
    }

    public void setDateId(Integer dateId) {
	this.dateId = dateId;
    }
    
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }
    
    public CalendarUri getCalendarUri(){
        return this.calendarUri;
    }
    
    public void setCalendarUri(CalendarUri cal){
        this.calendarUri=cal;
    }
    
    public UserUri getUserUri() {
	return userUri;
    }

    public void setUserUri(UserUri user) {
	this.userUri = user;
    }
    
    public String getDescription(){
        return this.description;
    }
    
    public void setDescription(String des){
        this.description=des;
    }
    
    public String getLugar(){
        return this.lugar;
    }
    
    public void setLugar(String lugar){
        this.lugar=lugar;
    }
    
    public Date getFechaComienzo(){
        return this.fechaComienzo;
    }
    
    public void setFechaComienzo(Date date){
        this.fechaComienzo=date;
    }
    
    public Date getFechaFinalizado(){
        return this.fechaFinalizado;
    }
    
    public void setFechaFinalizado(Date date){
        this.fechaFinalizado=date;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (dateId != null ? dateId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof CalendarResponse)) {
	    return false;
	}
	DateResponse other = (DateResponse) object;
	if ((this.dateId == null && other.dateId != null) || (this.dateId != null && !this.dateId.equals(other.dateId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	String respuesta = "ID de la cita: " + dateId + "\n";        
        respuesta+="userUri: "+this.getUserUri().getUri()+"\n";
        respuesta+="calendarUri: "+this.getCalendarUri().getUri()+"\n";
        respuesta+="Nombre: "+this.getName()+"\n"; 
        respuesta+="Descripción: "+this.getDescription()+"\n"; 
        respuesta+="Lugar: "+this.getLugar()+"\n";
        respuesta+="Fecha Comienzo: "+this.getFechaComienzo().toString()+"\n";
        respuesta+="Fecha Finalizado: "+this.getFechaFinalizado().toString()+"\n";        
        return respuesta;
    }
    
}
