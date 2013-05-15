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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author xafilox
 */
@Entity
@XmlRootElement
public class CalendarResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer calendarId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "name")
    private String name;
    @ManyToOne(optional = false)
    private UserUri user;
    private DateUri[] dates;

    public CalendarResponse() {
    }

    public CalendarResponse(Calendars calendar) {

	this.setCalendarId(calendar.getCalendarId());
	
	this.setName(calendar.getName());
	
	Collection<Dates> dates = calendar.getDatesCollection();
	List<DateUri> datesUris = new LinkedList<DateUri>();
	for(Dates d : dates){
	    datesUris.add(d.toUri());
	}
	this.setDates(datesUris.toArray(new DateUri[0]));
	
	this.setUser(calendar.getUserId().toUri());
	
    }

    
    public Integer getCalendarId() {
	return calendarId;
    }

    public void setCalendarId(Integer calendarId) {
	this.calendarId = calendarId;
    }
    
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public DateUri[] getDates() {
	return dates;
    }

    public void setDates(DateUri[] dates) {
	this.dates = dates;
    }

    public UserUri getUser() {
	return user;
    }

    public void setUser(UserUri user) {
	this.user = user;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (calendarId != null ? calendarId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof CalendarResponse)) {
	    return false;
	}
	CalendarResponse other = (CalendarResponse) object;
	if ((this.calendarId == null && other.calendarId != null) || (this.calendarId != null && !this.calendarId.equals(other.calendarId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	String respuesta = "ID del calendario: " + calendarId + "\n";
        respuesta+="userUri: "+this.getUser().getUri() +"\n" ;
        respuesta+="Nombre: "+this.getName() +"\n" ;
        respuesta+="Citas:\n";
        for (DateUri d:this.dates){
            respuesta+="    dateUri: "+d.getUri()+"\n";
        }
        
        return respuesta;
    }
    
}
