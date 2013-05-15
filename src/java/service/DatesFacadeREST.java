/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import SOSCalendar.Calendars;
import SOSCalendar.DateResponse;
import SOSCalendar.Dates;
import SOSCalendar.DateUri;
import SOSCalendar.RESTUri;
import SOSCalendar.Users;
import com.sun.xml.rpc.processor.modeler.j2ee.xml.messageDestinationLinkType;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
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

    @PUT
    @Consumes({"application/xml"})
    @Path("dates/{id_usu}/{id_date}")
    public void edit(Dates entity, @PathParam("id_usu") Integer id_usu, @PathParam("id_date") Integer id_date) {
	
	//Primero, comprobamos que el usuario exista
	checkUser(id_usu);
	
	//Comprobamos y obtenemos la cita
	checkDate(id_date);
	
        super.edit(entity);
    }

    @DELETE
    @Path("dates/{id_usu}/{id_date}")
    public void remove(@PathParam("id_usu") Integer id_usu, @PathParam("id_date") Integer id_date) {
	
	//Primero, comprobamos que el usuario exista
	checkUser(id_usu);
	
	//Comprobamos y obtenemos la cita
	Dates date = checkDate(id_date);
	
	//La borramos
        super.remove(date);
    }

    
    @GET
    @Path("dates/{id_usu}/")
    @Produces({"application/xml"})
    public Response findDates(@PathParam("id_usu") Integer id_usu,
			@QueryParam("max") @DefaultValue("-1") int max,
			@QueryParam("from_date") @DefaultValue("") String from_date_str,
			@QueryParam("to_date") @DefaultValue("") String to_date_str,
			@QueryParam("calendar") @DefaultValue("-1") int calendar_id) {
	
	//Primero, comprobamos que el usuario exista
	checkUser(id_usu);
	
	
	//Obtenemos los parámetros de filtrado
	Date from_date = null;
	Date to_date = null;
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	try {
	    from_date = df.parse(from_date_str);
            System.out.println(from_date.toString());
	} catch (ParseException e) {}
	try {
	    to_date = df.parse(to_date_str);
            System.out.println(to_date.toString());

	} catch (ParseException e) {}


	String querytxt = "SELECT d FROM Dates d JOIN d.calendarId c WHERE c.userId = :usu ";
	
	if(calendar_id > 0)
	    querytxt += "AND d.calendarId = :cal ";
	
	//Aplicamos los filtros de fecha
	if(from_date != null && to_date != null){
	    querytxt += "AND ((d.fechaComienzo >= :from AND d.fechaComienzo <= :to) "
		   + "OR (d.fechaFinalizado >= :from AND d.fechaFinalizado <= :to)"
		   + "OR (d.fechaComienzo <= :from AND d.fechaFinalizado >= :to))";
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
	if(calendar_id >= 0)
	    query.setParameter("cal", new Calendars(calendar_id));
	
	//Aplicamos el filtro de maximo devuelto
	if(max > 0)
	    query.setMaxResults(max);
	
	List<Dates> dates = query.getResultList();
	
	if(dates.size() > 0){
	    return Response.ok(datesToUriList(dates.toArray(new Dates[0]), id_usu)).build();
	}else
	    return Response.noContent().build();
	
    }
    
    @GET
    @Path("dates/{id_usu}/{id_date}")
    @Produces({"application/xml"})
    public Response find(@PathParam("id_usu") Integer id_usu, @PathParam("id_date") Integer id_date) {
	
	//Primero, comprobamos que el usuario exista
	checkUser(id_usu);
	
	//Comprobamos y obtenemos la cita
	Dates date = checkDate(id_date);
	
	//La borramos
        return Response.ok(new DateResponse(date)).build();
    }

    @GET
    @Path("datescount/{id_usu}")
    @Produces({"text/plain"})
    public String datescount(@PathParam("id_usu") Integer id_usu,
			    @QueryParam("period") @DefaultValue("") String period) {
	
	checkUser(id_usu);
	
	String querytxt = "SELECT d FROM Dates d JOIN d.calendarId c WHERE c.userId = :usu ";
	Calendar cal = new GregorianCalendar();
	cal.setFirstDayOfWeek(Calendar.MONDAY);
	cal.set(Calendar.HOUR, 0);
	cal.set(Calendar.MINUTE, 0);
	cal.set(Calendar.SECOND, 0);
	cal.set(Calendar.MILLISECOND, 0);
	
	Calendar cal2 = null;
	
	period = period.toLowerCase();
	if(period.equals("day")){
	    cal2 = (Calendar)cal.clone();
	    cal2.add(Calendar.DAY_OF_YEAR, 1);
	}else if(period.equals("week")){
	    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
	    cal2 = (Calendar)cal.clone();
	    cal2.add(Calendar.WEEK_OF_YEAR, 1);
	}else if(period.equals("month")){
	    cal.set(Calendar.DAY_OF_MONTH, 1);
	    cal2 = (Calendar)cal.clone();
	    cal2.add(Calendar.MONTH, 1);
	}else if(period.equals("year")){
	    cal.set(Calendar.DAY_OF_MONTH, 1);
	    cal.set(Calendar.MONTH, 1);
	    cal2 = (Calendar)cal.clone();
	    cal2.add(Calendar.YEAR, 1);
	}
	if(cal2 != null){
	    querytxt += " AND ((d.fechaComienzo BETWEEN :fecha AND :fecha2 )"
		    + " OR (d.fechaFinalizado BETWEEN :fecha AND :fecha2 )"
		    + " OR (d.fechaComienzo <= :fecha AND :fecha2 >= d.fechaFinalizado))";
	}
	
	Query q = em.createQuery(querytxt);
	q.setParameter("usu", new Users(id_usu));
	if(cal2 != null){
	    q.setParameter("fecha", cal.getTime());
	    q.setParameter("fecha2", cal2.getTime());
	}
	
	return ""+q.getResultList().size();

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
    
    private DateUri[] datesToUriList(Dates[] dates, Integer user){
	List<DateUri> ul = new LinkedList<DateUri>();

	for(Dates d : dates){
	    ul.add(d.toUri());
	}
	
	return (DateUri[]) ul.toArray(new DateUri[ul.size()]);
	
    }
}
