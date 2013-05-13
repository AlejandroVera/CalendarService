/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package SOSCalendar;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author xafilox
 */
@Entity
@Table(name = "calendars")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Calendars.findAll", query = "SELECT c FROM Calendars c"),
    @NamedQuery(name = "Calendars.findByCalendarId", query = "SELECT c FROM Calendars c WHERE c.calendarId = :calendarId"),
    @NamedQuery(name = "Calendars.findByName", query = "SELECT c FROM Calendars c WHERE c.name = :name")})
public class Calendars implements Serializable {
    private static final String BASE_URI = "http://localhost:8080/SOSCalendar/webresources";
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "calendar_id")
    private Integer calendarId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "name")
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "calendarId")
    private Collection<Dates> datesCollection;
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private Users userId;

    public Calendars() {
    }

    public Calendars(Integer calendarId) {
	this.calendarId = calendarId;
    }

    public Calendars(Integer calendarId, String name) {
	this.calendarId = calendarId;
	this.name = name;
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

    @XmlTransient
    public Collection<Dates> getDatesCollection() {
	return datesCollection;
    }

    public void setDatesCollection(Collection<Dates> datesCollection) {
	this.datesCollection = datesCollection;
    }

    public Users getUserId() {
	return userId;
    }

    public void setUserId(Users userId) {
	this.userId = userId;
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
	if (!(object instanceof Calendars)) {
	    return false;
	}
	Calendars other = (Calendars) object;
	if ((this.calendarId == null && other.calendarId != null) || (this.calendarId != null && !this.calendarId.equals(other.calendarId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "SOSCalendar.Calendars[ calendarId=" + calendarId + " ]";
    }
    
    public CalendarUri toUri(){
 	return new CalendarUri(BASE_URI+"/calendars/"+userId.getUserId()+"/"+this.calendarId);
    }
    
}
