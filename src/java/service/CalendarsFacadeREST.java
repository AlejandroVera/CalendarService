/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import SOSCalendar.Calendars;
import SOSCalendar.Dates;
import SOSCalendar.Users;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 *
 * @author borja
 */
@Stateless
@Path("calendars")
public class CalendarsFacadeREST extends AbstractFacade<Calendars> {

    @PersistenceContext(unitName = "SOSCalendarPU")
    private EntityManager em;

    public CalendarsFacadeREST() {
        super(Calendars.class);
    }

    @POST
    @Path("{id_usu}")
    public Response create(Calendars entity,
            @PathParam("id_usu") Integer id_usu) {

        //Primero, comprobamos que el usuario exista
        checkUser(id_usu);

        //Ahora vamos a buscar conflictos
        String name = entity.getName();

        String querytxt = "SELECT c FROM Calendars c WHERE c.name = :calName";
        Query query = em.createQuery(querytxt);
        query.setParameter("calName", entity);
        if (!query.getResultList().isEmpty()) {
            throw new WebApplicationException(new Throwable("Conflict: "
                    + "There is already a calendar with this name"), 409);
        }

        super.create(entity);
        
        return Response.status(Response.Status.NO_CONTENT).header("Location", entity.toUri(""+id_usu)).build();
    }

    @GET
    @Path("{id_usu}/{id_calen}")
    @Produces({"application/xml", "application/json"})
    public Response find(@PathParam("id_usu") Integer id_usu,
            @PathParam("id_calen") Integer id_calen) {
        //Se comprueba la existencia del usuario
        this.checkUser(id_usu);

        /*Comprobamos que existe y obtenemos el calendario*/
        Calendars calendar = this.checkCalendar(id_calen);
       
        /*Obtenemos los dates para hacer el composite*/
        String querytxt = "SELECT d FROM Dates d WHERE d.calendarId = :calId " ;
        Query query = em.createQuery(querytxt);
        query.setParameter("calId", calendar);
        
        List <Dates> dates = query.getResultList();
        Dates [] d = new Dates [dates.size()];
        
        String resultado = this.datesToUriListString(dates.toArray(d), id_usu, calendar);
        
        return Response.ok(resultado).build();
    }

    @PUT
    @Path("{id_usu}/{id_calen}")
    public void edit(Calendars entity,
            @PathParam("id_usu") Integer id_usu, @PathParam("id_calen") Integer id_calen) {

        //Primero, comprobamos que el usuario exista
        checkUser(id_usu);

        //Comprobamos y obtenemos el calendario
        checkCalendar(id_calen);

        String querytxt = "SELECT c FROM Calendars c WHERE c.name = :calName";
        Query query = em.createQuery(querytxt);
        query.setParameter("calName", entity);
        if (!query.getResultList().isEmpty()) {
            throw new WebApplicationException(new Throwable("Conflict: "
                    + "There is already a calendar with this name"), 409);
        }

        super.edit(entity);
    }

    @DELETE
    @Path("{id_usu}/{id_calen}")
    public void remove(@PathParam("id_usu") Integer id_usu,
                        @PathParam("id_calen") Integer id) {
        //Comprobamos que existe el usuario
        this.checkUser(id_usu);

        //Comprobamos que existe el calendario y lo obtenemos
        Calendars calendar = this.checkCalendar(id);

        //Lo borramos
        super.remove(calendar);
    }

    @GET
    @Path("{id_usu}")
    @Produces({"application/xml", "application/json"})
    public Response findAll(@PathParam("id_usu") Integer id_usu) {
        //Primero, comprobamos que el usuario exista
        checkUser(id_usu);

        String querytxt = "SELECT c FROM Calendars c WHERE c.userId = :userId";
        
        Query query = em.createQuery(querytxt);
        query.setParameter("userId", new Users(id_usu));
        List<Calendars> calendars = query.getResultList();

        if (calendars.isEmpty()) {
            return Response.noContent().build();
        } else {
            Calendars [] c = new Calendars[calendars.size()];
            return Response.ok(calendarsToUriListString((Calendars[]) calendars.toArray(c), id_usu)).build();
        }


    }

    /*@GET
    @Path("{from}/{to}")
    @Produces({"application/xml", "application/json"})
    public List<Calendars> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }*/

    @GET
    @Path("count")
    @Produces("text/plain")
    public String countREST() {
        return String.valueOf(super.count());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    private void checkUser(int id) {
        try {
            Query q = getEntityManager().createQuery("SELECT e FROM Users e where e.userId = :user_id");
            Object u = q.setParameter("user_id", id).getSingleResult();
        } catch (NoResultException ex) {
            throw new WebApplicationException(new Throwable("User not found"), 404);
        }

    }

    private Calendars checkCalendar(int id) {
        Calendars cal = super.find(id);
        if (cal == null) {
            throw new WebApplicationException(new Throwable("Calendar not found"), 404);
        }
        return cal;
    }
    
    private String datesToUriListString(Dates[] dates, Integer user, Calendars calendar){
	
        String ret = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><calendars>";
        ret+="<calendarId>"+calendar.getCalendarId()+"</calendarId>";
        ret+="<name>"+calendar.getName()+"</name>";
        ret+="<user>"+user.toString()+"</user>";
        ret+="<dates>";
      
	for(Dates date : dates){
	    ret += "<date>"+date.toUri(user.toString())+"</date>";
	}
	
	ret += "</dates>";
        ret +="</calendars>";
	
	return ret;
    }
    
        private String calendarsToUriListString(Calendars[] calendars, Integer user){
	String ret = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><dates>";
	for(Calendars calendar : calendars){
	    ret += "<calendar>"+calendar.toUri(user.toString())+"</calendar>";
	}
	
	ret += "</dates>";
	
	return ret;
    }
}
