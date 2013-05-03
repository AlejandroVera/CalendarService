/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import SOSCalendar.Calendars;
import SOSCalendar.Dates;
import SOSCalendar.Users;
import com.sun.xml.rpc.processor.modeler.j2ee.xml.messageDestinationLinkType;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;


@Stateless
@Path("")
public class DatesFacadeREST extends AbstractFacade<Dates> {
    @PersistenceContext(unitName = "SOSCalendarPU")
    private EntityManager em;

    public DatesFacadeREST() {
        super(Dates.class);
    }

    @POST
    @Consumes({"application/xml", "application/json"})
    @Path("{id_usu}/calendars/{id_calen}/dates")
    public void create(Dates entity, @PathParam("id_usu") Integer id_usu, @PathParam("id_calen") Integer id_calen) {
	
	//Primero, comprobamos que el usuario exista
	checkUser(id_usu);
	
	//Comprobamos que el calendario exista
	checkCalendar(id_calen);
	
        super.create(entity);
	
	System.out.println("ID creado:"+entity.getDateId());
    }

    @PUT
    @Consumes({"application/xml", "application/json"})
    @Path("{id_usu}/dates/{id_date}")
    public void edit(Dates entity, @PathParam("id_usu") Integer id_usu, @PathParam("id_date") Integer id_date) {
	
	//Primero, comprobamos que el usuario exista
	checkUser(id_usu);
	
	//Comprobamos y obtenemos la cita
	checkDate(id_date);
	
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
    public Response findDatesOfCalendar(@PathParam("id_usu") Integer id_usu,
			@PathParam("id_calen") Integer id_calen,
			@QueryParam("max") @DefaultValue("-1") int max,
			@QueryParam("from_date") @DefaultValue("") String from_date_str,
			@QueryParam("to_date") @DefaultValue("") String to_date_str) {
	
	//Primero, comprobamos que el usuario exista
	checkUser(id_usu);
	
	//Comprobamos que el calendario exista
	checkCalendar(id_calen);
	
	//Obtenemos los parámetros de filtrado
	Date from_date = null;
	Date to_date = null;
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	try {
	    from_date = df.parse(from_date_str);
	} catch (ParseException e) {}
	try {
	    to_date = df.parse(to_date_str);
	} catch (ParseException e) {}


	Calendars cal = new Calendars(id_calen);
	String querytxt = "SELECT d FROM Dates d WHERE d.calendarId = :cal ";
	
	//Aplicamos los filtros de fecha
	if(from_date != null && to_date != null){
	    querytxt += "AND ((d.fechaComienzo >= :from AND d.fechaComienzo <= :to) "
		   + "OR (d.fechaFinalizado >= :from AND d.fechaFinalizado <= :to))";
	}else if(from_date != null){
	    querytxt += "AND (d.fechaComienzo >= :from OR d.fechaFinalizado >= :from)";
	}else if(to_date != null){
	    querytxt += "AND (d.fechaComienzo <= :to OR d.fechaFinalizado <= :to)";
	}
	Query query = em.createQuery(querytxt).setParameter("cal", cal);
	if(from_date != null)
	    query.setParameter("from", from_date);
	if(to_date != null)
	    query.setParameter("to", to_date);
	
	//Aplicamos el filtro de maximo devuelto
	if(max > 0)
	    query.setMaxResults(max);
	
	List<Dates> dates = query.getResultList();
	if(dates.size() > 0){
	    Dates []d = new Dates[dates.size()];
	    return Response.ok((Dates[])dates.toArray(d)).build();
	}else
	    return Response.noContent().build();
	
    }
    
    @GET
    @Path("{id_usu}/dates/")
    @Produces({"application/xml", "application/json"})
    public Response findDates(@PathParam("id_usu") Integer id_usu,
			@QueryParam("max") @DefaultValue("-1") int max,
			@QueryParam("from_date") @DefaultValue("") String from_date_str,
			@QueryParam("to_date") @DefaultValue("") String to_date_str) {
	
	//Primero, comprobamos que el usuario exista
	checkUser(id_usu);
	
	
	//Obtenemos los parámetros de filtrado
	Date from_date = null;
	Date to_date = null;
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	try {
	    from_date = df.parse(from_date_str);
	} catch (ParseException e) {}
	try {
	    to_date = df.parse(to_date_str);
	} catch (ParseException e) {}


	String querytxt = "SELECT d FROM Dates d JOIN d.calendarId c WHERE c.userId = :usu ";
	
	//Aplicamos los filtros de fecha
	if(from_date != null && to_date != null){
	    querytxt += "AND ((d.fechaComienzo >= :from AND d.fechaComienzo <= :to) "
		   + "OR (d.fechaFinalizado >= :from AND d.fechaFinalizado <= :to))";
	}else if(from_date != null){
	    querytxt += "AND (d.fechaComienzo >= :from OR d.fechaFinalizado >= :from)";
	}else if(to_date != null){
	    querytxt += "AND (d.fechaComienzo <= :to OR d.fechaFinalizado <= :to)";
	}
	Query query = em.createQuery(querytxt).setParameter("usu", new Users(id_usu));
	if(from_date != null)
	    query.setParameter("from", from_date);
	if(to_date != null)
	    query.setParameter("to", to_date);
	
	//Aplicamos el filtro de maximo devuelto
	if(max > 0)
	    query.setMaxResults(max);
	
	List<Dates> dates = query.getResultList();
	if(dates.size() > 0){
	    Dates []d = new Dates[dates.size()];
	    return Response.ok((Dates[])dates.toArray(d)).build();
	}else
	    return Response.noContent().build();
	
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
            Query q = getEntityManager().createQuery("SELECT e FROM Users e where e.userId = :user_id");
	    Object u = q.setParameter("user_id", id).getSingleResult();
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
    
    private void checkCalendar(int id){
	try {
            Query q = getEntityManager().createQuery("SELECT e FROM Calendars e where e.calendarId = :calendar_id");
	    q.setParameter("calendar_id", id).getSingleResult();
        } catch (NoResultException ex) {
            throw new WebApplicationException(new Throwable("Calendar not found"), 404);
	}
    }
}
