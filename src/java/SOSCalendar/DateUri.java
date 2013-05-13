/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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
public class DateUri extends RESTUri implements Serializable {

    public DateUri() {
	super();
    }

    DateUri(String string) {
	super(string);
    }
       
}
