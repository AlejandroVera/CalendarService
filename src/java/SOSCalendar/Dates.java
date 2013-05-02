/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package SOSCalendar;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
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
@Table(name = "dates")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Dates.findAll", query = "SELECT d FROM Dates d"),
    @NamedQuery(name = "Dates.findByDateId", query = "SELECT d FROM Dates d WHERE d.dateId = :dateId"),
    @NamedQuery(name = "Dates.findByName", query = "SELECT d FROM Dates d WHERE d.name = :name"),
    @NamedQuery(name = "Dates.findByDescription", query = "SELECT d FROM Dates d WHERE d.description = :description"),
    @NamedQuery(name = "Dates.findByLugar", query = "SELECT d FROM Dates d WHERE d.lugar = :lugar"),
    @NamedQuery(name = "Dates.findByFechaComienzo", query = "SELECT d FROM Dates d WHERE d.fechaComienzo = :fechaComienzo"),
    @NamedQuery(name = "Dates.findByFechaFinalizado", query = "SELECT d FROM Dates d WHERE d.fechaFinalizado = :fechaFinalizado")})
public class Dates implements Serializable {
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
    private static final long serialVersionUID = 1L;
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
    @JoinColumn(name = "calendar_id", referencedColumnName = "calendar_id")
    @ManyToOne(optional = false)
    private Calendars calendarId;

    public Dates() {
    }

    public Dates(Integer dateId) {
	this.dateId = dateId;
    }

    public Dates(Integer dateId, String name, String description, String lugar, Date fechaComienzo, Date fechaFinalizado) {
	this.dateId = dateId;
	this.name = name;
	this.description = description;
	this.lugar = lugar;
	this.fechaComienzo = fechaComienzo;
	this.fechaFinalizado = fechaFinalizado;
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

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String getLugar() {
	return lugar;
    }

    public void setLugar(String lugar) {
	this.lugar = lugar;
    }

    public Calendars getCalendarId() {
	return calendarId;
    }

    public void setCalendarId(Calendars calendarId) {
	this.calendarId = calendarId;
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
	if (!(object instanceof Dates)) {
	    return false;
	}
	Dates other = (Dates) object;
	if ((this.dateId == null && other.dateId != null) || (this.dateId != null && !this.dateId.equals(other.dateId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "SOSCalendar.Dates[ dateId=" + dateId + " ]";
    }

    public Date getFechaComienzo() {
        return fechaComienzo;
    }

    public void setFechaComienzo(Date fechaComienzo) {
        this.fechaComienzo = fechaComienzo;
    }

    public Date getFechaFinalizado() {
        return fechaFinalizado;
    }

    public void setFechaFinalizado(Date fechaFinalizado) {
        this.fechaFinalizado = fechaFinalizado;
    }
    
}
