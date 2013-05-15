/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package SOSCalendar;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author xafilox
 */
@Entity
@XmlRootElement
public class UserUri extends RESTUri implements Serializable {

    public UserUri() {
	super();
    }

    UserUri(String string) {
	super(string);
    }
       
}