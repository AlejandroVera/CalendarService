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

/**
 *
 * @author borja
 */
@Stateless
@Path("")
public class CalendarsFacadeREST extends AbstractFacade<Calendars> {

    @PersistenceContext(unitName = "SOSCalendarPU")
    private EntityManager em;

    public CalendarsFacadeREST() {
        super(Calendars.class);
    }

    @POST
    @Override
    @Consumes({"application/xml", "application/json"})
    public void create(Calendars entity) {
        super.create(entity);
    }

    @POST
    @Consumes({"{id_usu}/calendars"})
    public void create(Calendars entity,
            @PathParam("id_usu") Integer id_usu) {

        //Primero, comprobamos que el usuario exista
        checkUser(id_usu);

        //Ahora vamos a buscar conflictos
        String name = entity.getName();

        String querytxt = "SELECT c FROM calendars c WHERE c.name = " + name;
        Query query = em.createQuery(querytxt);
        if (!query.getResultList().isEmpty()) {
            throw new WebApplicationException(new Throwable("Conflict: "
                    + "There is already a calendar with this name"), 409);
        }

        super.create(entity);
    }

    @PUT
    @Consumes({"{id_usu}/calendars/{calendar_id}"})
    public void edit(Calendars entity,
            @PathParam("id_usu") Integer id_usu, @PathParam("calendar_id") Integer calendar_id) {

        //Primero, comprobamos que el usuario exista
        checkUser(id_usu);

        //Comprobamos y obtenemos el calendario
        checkCalendar(calendar_id);

        //Ahora vamos a buscar conflictos
        String name = entity.getName();

        String querytxt = "SELECT c FROM calendars c WHERE c.name = " + name;
        Query query = em.createQuery(querytxt);
        if (!query.getResultList().isEmpty()) {
            throw new WebApplicationException(new Throwable("Conflict: "
                    + "There is already a calendar with this name"), 409);
        }

        super.edit(entity);
    }

    @DELETE
    @Path("{id_usu}/calendars/{calendar_id}")
    public void remove(@PathParam("calendar_id") Integer id,
            @PathParam("id_usu") Integer id_usu) {
        //Comprobamos que existe el usuario
        this.checkUser(id_usu);

        //Comprobamos que existe el calendario y lo obtenemos
        Calendars calendar = this.checkCalendar(id);

        //Lo borramos
        super.remove(calendar);
    }

    @GET
    @Path("{id_usu}/calendars/{calendar_id}")
    @Produces({"application/xml", "application/json"})
    public Calendars find(@PathParam("calendar_id") Integer calendar_id,
            @PathParam("id_usu") Integer id_usu) {
        //Se comprueba la existencia del usuario
        this.checkUser(id_usu);

        /*Comprobamos que existe y obtenemos el calendario*/
        Calendars calendar = this.checkCalendar(calendar_id);
        
        return calendar;
        /*List<Calendars> calendars;

        String querytxt = "SELECT d FROM dates d WHERE d.calendar_id = " + calendar_id;
        Query query = em.createQuery(querytxt);
        List<Calendars> calendars = query.getResultList();
        return calendars;*/

    }

    @GET
    @Path("{id_usu}/calendars")
    @Produces({"application/xml", "application/json"})
    public List<Calendars> findAll(@PathParam("id_usu") Integer id_usu) {
        //Primero, comprobamos que el usuario exista
        checkUser(id_usu);

        String querytxt = "SELECT c FROM calendars c WHERE c.user_id = " + id_usu;
        Query query = em.createQuery(querytxt);
        List<Calendars> calendars = query.getResultList();
        return calendars;


    }

    @GET
    @Path("{from}/{to}")
    @Produces({"application/xml", "application/json"})
    public List<Calendars> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

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
            em.createQuery("SELECT e FROM Users e where e.user_id = :user_id").setParameter("user_id", id).getSingleResult();
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
}
