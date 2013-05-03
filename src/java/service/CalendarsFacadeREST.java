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

    @PUT
    @Override
    @Consumes({"application/xml", "application/json"})
    public void edit(Calendars entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id_usu}/calendars/")
    @Produces({"application/xml", "application/json"})
    public Calendars find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @GET    
    @Path("{id_usu}/calendars")
    @Produces({"application/xml", "application/json"})
    public List<Calendars> findAll(@PathParam("id_usu") Integer id_usu) {
        //Primero, comprobamos que el usuario exista
        checkUser(id_usu);
        
        String querytxt = "SELECT calendar_id FROM calendars c WHERE c.user_id = " + id_usu;
        Query query = em.createQuery(querytxt);
        List <Calendars> calendars = query.getResultList();
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
    
    private Calendars checkCalendar(int id){
	Calendars cal = super.find(id);
	if(cal == null)
	    throw new WebApplicationException(new Throwable("Calendar not found"), 404);
	return cal;
    }
}
