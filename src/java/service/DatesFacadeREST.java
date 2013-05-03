/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import SOSCalendar.Dates;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
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
public class DatesFacadeREST extends AbstractFacade<Dates> {
    @PersistenceContext(unitName = "SOSCalendarPU")
    private EntityManager em;

    public DatesFacadeREST() {
        super(Dates.class);
    }

    @POST
    @Override
    @Consumes({"application/xml", "application/json"})
    public void create(Dates entity) {
        super.create(entity);
    }

    @PUT
    @Consumes({"application/xml", "application/json"})
    @Path("{id_usu}/dates/{id_date}")
    public void edit(Dates entity, @PathParam("id_usu") Integer id_usu, @PathParam("id_date") Integer id_date) {
	
	//Primero, comprobamos que el usuario exista
	checkUser(id_usu);
	
	//Comprobamos y obtenemos la cita
	checkDate(id_date);
	
	//TODO: buscar conflictos
        super.edit(entity);
    }

    @DELETE
    @Path("{id_usu}/dates/{id_date}")
    public void remove(@PathParam("id_usu") Integer id_usu, @PathParam("id_date") Integer id_date) {
	
	//Primero, comprobamos que el usuario exista
	checkUser(id_usu);
	
	//Comprobamos y obtenemos la cita
	Dates date = checkDate(id_date);
	
	//La borramos
        super.remove(date);
    }

    @GET
    @Path("{id_usu}/calendars/{id_calen}/dates")
    @Produces({"application/xml", "application/json"})
    public Dates find(@PathParam("id_usu") Integer id_usu, @PathParam("id_calen") Integer id_calen) {
        
	//Primero, comprobamos que el usuario exista
	checkUser(id_usu);
	
	//TODO
	return super.find(1);
	
	
	
	/*protected Customer getEntity() {
        try {
            return (Customer) em.createQuery("SELECT e FROM Customer e where e.customerId = :customerId").setParameter("customerId", id).getSingleResult();
        } catch (NoResultException ex) {
            throw new WebApplicationException(new Throwable("Resource for " + uriInfo.getAbsolutePath() + " does not exist."), 404);
        }*/
    }
    
    @GET
    @Override
    @Produces({"application/xml", "application/json"})
    public List<Dates> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({"application/xml", "application/json"})
    public List<Dates> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
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
    
    private Dates checkDate(int id){
	Dates date = super.find(id);
	if(date == null)
	    throw new WebApplicationException(new Throwable("Date not found"), 404);
	return date;
    }
}
