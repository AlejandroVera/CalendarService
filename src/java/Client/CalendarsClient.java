/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

/**
 * Jersey REST client generated for REST resource:CalendarsFacadeREST
 * [calendars]<br>
 * USAGE:
 * <pre>
 *        CalendarsClient client = new CalendarsClient();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author borja
 */
public class CalendarsClient {
    private WebResource webResource;
    private Client client;
    private static final String BASE_URI = "http://localhost:8080/SOSCalendar/webresources";

    public CalendarsClient() {
        com.sun.jersey.api.client.config.ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
        client = Client.create(config);
        webResource = client.resource(BASE_URI).path("calendars");
    }

    public void remove(String id_usu, String id_calen) throws UniformInterfaceException {
        webResource.path(java.text.MessageFormat.format("{0}/{1}", new Object[]{id_usu, id_calen})).delete();
    }

    public String countREST() throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path("count");
        return resource.accept(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
    }

    public <T> T findAll_XML(Class<T> responseType, String id_usu) throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{id_usu}));
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
    }

    public <T> T findAll_JSON(Class<T> responseType, String id_usu) throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{id_usu}));
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public void edit(String id_usu, String id_calen) throws UniformInterfaceException {
        webResource.path(java.text.MessageFormat.format("{0}/{1}", new Object[]{id_usu, id_calen})).put();
    }

    public ClientResponse create(String id_usu) throws UniformInterfaceException {
        return webResource.path(java.text.MessageFormat.format("{0}", new Object[]{id_usu})).post(ClientResponse.class);
    }

    public <T> T find_XML(Class<T> responseType, String id_usu, String id_calen) throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path(java.text.MessageFormat.format("{0}/{1}", new Object[]{id_usu, id_calen}));
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
    }

    public <T> T find_JSON(Class<T> responseType, String id_usu, String id_calen) throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path(java.text.MessageFormat.format("{0}/{1}", new Object[]{id_usu, id_calen}));
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public void close() {
        client.destroy();
    }
    
}
