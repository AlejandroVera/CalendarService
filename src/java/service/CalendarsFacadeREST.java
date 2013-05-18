/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import SOSCalendar.Calendars;
import SOSCalendar.Dates;
import SOSCalendar.RESTUri;
import SOSCalendar.Users;
import SOSCalendar.CalendarResponse;
import SOSCalendar.CalendarUri;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
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

    @EJB
    private DatesFacadeREST datesFacadeREST;
    @PersistenceContext(unitName = "SOSCalendarPU")
    private EntityManager em;

    public CalendarsFacadeREST() {
        super(Calendars.class);
    }

    @POST
    @Consumes({"application/xml"})
    @Path("{id_usu}")
    public Response create(Calendars entity,
            @PathParam("id_usu") Integer id_usu) {

        //Primero, comprobamos que el usuario exista
        checkUser(id_usu);

        //Ahora vamos a buscar conflictos
        
        String querytxt = "SELECT c FROM Calendars c WHERE c.name = :calName";
       
        Query query = em.createQuery(querytxt);
        query.setParameter("calName", entity.getName());
        if (!query.getResultList().isEmpty()) {
            throw new WebApplicationException(new Throwable("Conflict: "
                    + "There is already a calendar with this name"), 409);
        }

        super.create(entity);
        
        //Necesario para obtener el id           
        querytxt = "SELECT c FROM Calendars c WHERE c.name = :calName";
       
        query = em.createQuery(querytxt);
        query.setParameter("calName", entity.getName());
        entity = (Calendars) query.getSingleResult();

        return Response.status(Response.Status.NO_CONTENT).header("Location", entity.toUri().getUri()).build();
    }

    @POST
    @Consumes({"application/xml"})
    @Path("{id_usu}/{id_calen}/dates")
    public Response createDate(Dates entity, @PathParam("id_usu") Integer id_usu, @PathParam("id_calen") Integer id_calen) {
        System.out.println("USU:" + id_usu + " CAL:" + id_calen);
        //Primero, comprobamos que el usuario exista
        checkUser(id_usu);
        
        //Comprobamos que el calendario exista
        Calendars c= checkCalendar(id_calen);
        
        //Comprobamos que el calendario es del usuario
        checkCoherencia(c,id_usu);
        
        System.out.println("Fecha comienzo: " + entity.getFechaComienzo().toString());
        System.out.println("Fecha final: " + entity.getFechaFinalizado().toString());
        datesFacadeREST.create(entity);
        
        //Necesario para obtener el id
        String querytxt = "SELECT d FROM Dates d WHERE d.name = :dateName";
        Query query = em.createQuery(querytxt);
        query.setParameter("dateName", entity.getName());
        Dates newentity = (Dates) query.getSingleResult();
        entity.setDateId(newentity.getDateId());

        return Response.status(Response.Status.NO_CONTENT).header("Location", entity.toUri().getUri()).build();
    }

    @GET
    @Path("{id_usu}/{id_calen}")
    @Produces({"application/xml"})
    public Response find(@PathParam("id_usu") Integer id_usu,
            @PathParam("id_calen") Integer id_calen) {
        //Se comprueba la existencia del usuario
        this.checkUser(id_usu);

        /*Comprobamos que existe y obtenemos el calendario*/
        Calendars calendar = this.checkCalendar(id_calen);
        
        //Comprobamos que el calendario es del usuario
        checkCoherencia(calendar,id_usu);

        /*Obtenemos los dates para hacer el composite*/
        String querytxt = "SELECT d FROM Dates d WHERE d.calendarId = :calId ";
        Query query = em.createQuery(querytxt);
        query.setParameter("calId", calendar);

        List<Dates> dates = query.getResultList();        
        calendar.setDatesCollection(dates);

        return Response.ok(new CalendarResponse(calendar)).build();
    }

    @PUT
    @Consumes({"application/xml"})
    @Path("{id_usu}/{id_calen}")
    public void edit(Calendars entity,
            @PathParam("id_usu") Integer id_usu, @PathParam("id_calen") Integer id_calen) {

        //Primero, comprobamos que el usuario exista
        checkUser(id_usu);

        //Comprobamos y obtenemos el calendario
        Calendars c =checkCalendar(id_calen);
        
        //Comprobamos que el calendario es del usuario
        checkCoherencia(c,id_usu);
        System.out.println();
        System.out.println(entity.getName());
        System.out.println(entity.getCalendarId());
        System.out.println(entity.getUserId().toString());
        
        
        String querytxt = "SELECT c FROM Calendars c WHERE c.name = :calName";
        Query query = em.createQuery(querytxt);
        query.setParameter("calName", entity.getName());
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
        
        //Comprobamos que el calendario es del usuario
        checkCoherencia(calendar,id_usu);

        //Lo borramos
        super.remove(calendar);
    }

    @GET
    @Path("{id_usu}")
    @Produces({"application/xml"})
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
            Calendars[] c = new Calendars[calendars.size()];
            return Response.ok(calendarsToUriList((Calendars[]) calendars.toArray(c), id_usu)).build();
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
    
    private void checkCoherencia(Calendars c, Integer id_usu){
            if (!c.getUserId().getUserId().equals(id_usu)){
                System.out.println("ID usuario: "+ c.getUserId().getUserId());
                System.out.println("ID usuario calendario: "+ c.getUserId().getUserId());
                throw new WebApplicationException(new Throwable("Calendario no correspondiente a usuario"), 404);
        }
    }

    private CalendarUri[] calendarsToUriList(Calendars[] calendars, Integer user) {
        List<CalendarUri> ul = new LinkedList<CalendarUri>();

        for (Calendars c : calendars) {
            ul.add(c.toUri());
        }

        return (CalendarUri[]) ul.toArray(new CalendarUri[ul.size()]);

    }
}
