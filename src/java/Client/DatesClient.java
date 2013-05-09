/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

/**
 * Jersey REST client generated for REST resource:DatesFacadeREST<br>
 * USAGE:
 * <pre>
 *        DatesClient client = new DatesClient();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author borja
 */
public class DatesClient {
    private WebResource webResource;
    private Client client;
    private static final String BASE_URI = "http://localhost:8080/SOSCalendar/webresources";

    public DatesClient() {
        com.sun.jersey.api.client.config.ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
        client = Client.create(config);
        webResource = client.resource(BASE_URI);
    }

    public void remove(String id_usu, String id_date) throws UniformInterfaceException {
        webResource.path(java.text.MessageFormat.format("dates/{0}/{1}", new Object[]{id_usu, id_date})).delete();
    }

    public String datescount(String id_usu, String period) throws UniformInterfaceException {
        WebResource resource = webResource;
        if (period != null) {
            resource = resource.queryParam("period", period);
        }
        resource = resource.path(java.text.MessageFormat.format("datescount/{0}", new Object[]{id_usu}));
        return resource.accept(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
    }

    public <T> T findDates(Class<T> responseType, String id_usu, String max, String to_date, String from_date, String calendar) throws UniformInterfaceException {
        WebResource resource = webResource;
        if (max != null) {
            resource = resource.queryParam("max", max);
        }
        if (to_date != null) {
            resource = resource.queryParam("to_date", to_date);
        }
        if (from_date != null) {
            resource = resource.queryParam("from_date", from_date);
        }
        if (calendar != null) {
            resource = resource.queryParam("calendar", calendar);
        }
        resource = resource.path(java.text.MessageFormat.format("dates/{0}", new Object[]{id_usu}));
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
    }

    public void edit(Object requestEntity, String id_usu, String id_date) throws UniformInterfaceException {
        webResource.path(java.text.MessageFormat.format("dates/{0}/{1}", new Object[]{id_usu, id_date})).type(javax.ws.rs.core.MediaType.APPLICATION_XML).put(requestEntity);
    }

    public <T> T find(Class<T> responseType, String id_usu, String id_date) throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path(java.text.MessageFormat.format("dates/{0}/{1}", new Object[]{id_usu, id_date}));
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
    }

    public void close() {
        client.destroy();
    }
    
}
