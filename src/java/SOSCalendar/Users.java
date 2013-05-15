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
@Table(name = "users")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Users.findAll", query = "SELECT u FROM Users u"),
    @NamedQuery(name = "Users.findByUserId", query = "SELECT u FROM Users u WHERE u.userId = :userId"),
    @NamedQuery(name = "Users.findByName", query = "SELECT u FROM Users u WHERE u.name = :name")})
public class Users implements Serializable {
    private static final String BASE_URI = "http://localhost:8080/SOSCalendar/webresources";
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "user_id")
    private Integer userId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "name")
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId")
    private Collection<Calendars> calendarsCollection;

    public Users() {
    }

    public Users(Integer userId) {
	this.userId = userId;
    }

    public Users(Integer userId, String name) {
	this.userId = userId;
	this.name = name;
    }

    public Integer getUserId() {
	return userId;
    }

    public void setUserId(Integer userId) {
	this.userId = userId;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @XmlTransient
    public Collection<Calendars> getCalendarsCollection() {
	return calendarsCollection;
    }

    public void setCalendarsCollection(Collection<Calendars> calendarsCollection) {
	this.calendarsCollection = calendarsCollection;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (userId != null ? userId.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof Users)) {
	    return false;
	}
	Users other = (Users) object;
	if ((this.userId == null && other.userId != null) || (this.userId != null && !this.userId.equals(other.userId))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "SOSCalendar.Users[ userId=" + userId + " ]";
    }
    
    public UserUri toUri(){
 	return new UserUri(BASE_URI+"/users/"+this.userId);
    }
    
}
