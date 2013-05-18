/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import SOSCalendar.UserUri;
import SOSCalendar.Users;
import java.util.LinkedList;
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
@Path("users")
public class UsersFacadeREST extends AbstractFacade<Users> {
    @PersistenceContext(unitName = "SOSCalendarPU")
    private EntityManager em;

    public UsersFacadeREST() {
        super(Users.class);
    }

    @POST    
    @Consumes({"application/xml", "application/json"})
    public Response createUser(Users entity) {
        
        super.create(entity);
        //Necesario para obtener el id           
        String querytxt = "SELECT u FROM Users u WHERE u.name = :userName";
       
        Query query = em.createQuery(querytxt);
        query.setParameter("userName", entity.getName());
        entity = (Users) query.getSingleResult();

        return Response.status(Response.Status.NO_CONTENT).header("Location", entity.toUri().getUri()).build();        
    }


    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        //Se mira si existe el user
        checkUser(id);
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    public Users find(@PathParam("id") Integer id) {
        //Se mira si existe el user
        checkUser(id);
        return super.find(id);
    }

    @GET 
    @Produces({"application/xml"})    
    public Response findAllUsers() {
        List <Users> list = super.findAll();
        List<UserUri> listUris = new LinkedList<UserUri>();
        
        for (Users user:list){
            listUris.add(user.toUri());            
        }
        
        return Response.ok((UserUri[])listUris.toArray(new UserUri[listUris.size()])).build();
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
}
