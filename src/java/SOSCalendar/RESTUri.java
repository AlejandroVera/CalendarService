/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package SOSCalendar;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author xafilox
 */
@Entity
@XmlRootElement
public class RESTUri implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String uri;

    public RESTUri() {
    }
    
    public RESTUri(String id) {
	this.uri = id;
    }
    
    public String getUri() {
	return uri;
    }

    public void setUri(String uri) {
	this.uri = uri;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (uri != null ? uri.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the uri fields are not set
	if (!(object instanceof RESTUri)) {
	    return false;
	}
	RESTUri other = (RESTUri) object;
	if ((this.uri == null && other.uri != null) || (this.uri != null && !this.uri.equals(other.uri))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "SOSCalendar.RESTUri[ id=" + uri + " ]";
    }
    
}
