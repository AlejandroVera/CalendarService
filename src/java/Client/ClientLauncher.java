/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import SOSCalendar.CalendarResponse;
import SOSCalendar.CalendarUri;
import SOSCalendar.Calendars;
import SOSCalendar.DateResponse;
import SOSCalendar.DateUri;
import SOSCalendar.Dates;
import SOSCalendar.RESTUri;
import SOSCalendar.UserUri;
import SOSCalendar.Users;
import com.sun.jersey.api.client.ClientResponse;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import javax.ws.rs.core.Response;

/**
 *
 * @author xafilox
 */
public class ClientLauncher {

    private static Scanner in = new Scanner(System.in);

    public static void main(String[] args) {

        int action;
        while ((action = requestAction()) != 0) {

            switch (action) {
                case 1:
                    createUser();
                    break;
                case 2:
                    createCalendar();
                    break;
                case 3:
                    createDate();
                    break;
                case 4:
                    editDate();
                    break;
                case 5:
                    requestDate();
                    break;
                case 6:
                    requestUsers();
                    break;
                case 7:
                    requestUserCalendars();
                    break;
                case 8:
                    requestDates();
                    break;
                case 9:
                    editCalendar();
                    break;
                case 10:
                    requestCalendar();
                    break;
                default:
                    System.out.println();
            }

        }

    }

    private static int requestAction() {
        String leido;
        System.out.println();
        System.out.println("Indique el número de la acción que quiere realizar");
        System.out.println("[0] Salir del programa");
        System.out.println("[1] Crear un usuario");
        System.out.println("[2] Crear un calendario");
        System.out.println("[3] Crear una cita en un calendario");
        System.out.println("[4] Modificar una cita");
        System.out.println("[5] Ver una cita de un calendario");
        System.out.println("[6] Obtener lista de usuarios");
        System.out.println("[7] Obtener lista de calendarios de un usuario");
        System.out.println("[8] Obtener lista de citas de un usuario o calendario");
        System.out.println("[9] Modificar el nombre de un calendario");
        System.out.println("[10] Obtener un calendario y la lista de citas del calendario");

        leido = readLine("Número de acción: ");
        try {
            return Integer.parseInt(leido);
        } catch (NumberFormatException e) {
            return -1;
        }

    }

    private static void createUser() {
        String name = readLine("Nombre del usuario: ");
        Users us = new Users();
        us.setName(name);
        UsersClient cli = new UsersClient();
        ClientResponse resp = cli.createUser_XML(us);
        System.out.println(); 
       if (resp.getStatus() == ClientResponse.Status.NO_CONTENT.getStatusCode()) {           
           System.out.println("Usuario creado con uri: "+resp.getHeaders().getFirst("Location"));
            
        } else {
            System.out.println("Error al crear el usuario");
        }
        cli.close();
    }

    private static void createCalendar() {
        int id = readInt("ID del propietario: ");
        String name = readLine("Nombre del calendario: ");

        Calendars cal = new Calendars();
        cal.setUserId(new Users(id));
        cal.setName(name);
        
        CalendarsClient cli = new CalendarsClient();
        ClientResponse resp = cli.create(cal, "" + id);

        if (resp.getStatus() == ClientResponse.Status.NO_CONTENT.getStatusCode()) {
            System.out.println("Calendario ha sido creado con uri: "+resp.getHeaders().getFirst("Location"));
            
        } else if (resp.getStatus() == ClientResponse.Status.NOT_FOUND.getStatusCode()) {
            System.out.println("El propietario no existe");
            
        } else {
            System.out.println("Error desconocido");
        }
        cli.close();
    }

    private static void createDate() {

        int idUs = readInt("ID del usuario: ");
        int idCal = readInt("ID del calendario: ");
        

        Dates date = new Dates();
        Calendars cal= new Calendars(idCal);
        cal.setUserId(new Users(idUs));
        date.setCalendarId(cal);
        date.setName(readLine("Nombre de la cita: "));
        date.setDescription(readLine("Descripción: "));
        date.setFechaComienzo(readDate("Fecha comienzo (yyyy-MM-dd HH:mm): "));
        date.setFechaFinalizado(readDate("Fecha finalización (yyyy-MM-dd HH:mm): "));
        date.setLugar(readLine("Lugar: "));
        
        CalendarsClient cli = new CalendarsClient();
        
        ClientResponse resp = cli.createDate(date, "" + idUs, "" + idCal);
        
        if (resp.getStatus() == ClientResponse.Status.NO_CONTENT.getStatusCode()) {
            System.out.println("La cita ha sido creada con uri: "+resp.getHeaders().getFirst("Location"));

        } else if (resp.getStatus() == ClientResponse.Status.NOT_FOUND.getStatusCode()) {
            System.out.println("El propietario o calendario no existe");
        } else {
            System.out.println("Error desconocido");
        }
        cli.close();

    }

    private static void editDate() {
        Integer idUs = readInt("ID del usuario: ");
        Integer idCita = readInt("ID de la cita: ");
        Dates date;
        DateResponse fechaResponse;
        DatesClient dcli = new DatesClient();
        CalendarsClient calendarClient = new CalendarsClient();
        UsersClient userClient = new UsersClient();
        ClientResponse antes = dcli.find(ClientResponse.class, "" + idUs, "" + idCita);

        if (antes.getStatus() == Response.Status.OK.getStatusCode()) {
            fechaResponse = antes.getEntity(DateResponse.class);
            date = new Dates(fechaResponse.getDateId(), fechaResponse.getName(), fechaResponse.getDescription(),
                                fechaResponse.getLugar(), fechaResponse.getFechaComienzo(), fechaResponse.getFechaFinalizado());
            
            //Obtenemos el usuario para ponérselo al calendario
            ClientResponse userResponse = userClient.find_XML(ClientResponse.class, idUs.toString());
            
            String calendarId = fechaResponse.getCalendarUri().getUri().replaceFirst(".*/([^/?]+).*", "$1");
            
            //Obtenemos el calendario para ponérselo a la date1
            CalendarResponse calendario = calendarClient.find(CalendarResponse.class, idUs.toString(), calendarId);
            Calendars calendar = new Calendars();
            calendar.setCalendarId(calendario.getCalendarId());
            calendar.setName(calendario.getName());
            calendar.setUserId(userResponse.getEntity(Users.class));
            
            date.setCalendarId(calendar);
            System.out.println("Indique los valores a modificar. Si no quiere modificar alguno, pulse Enter para mantener el valor previo.");


        } else {
            System.out.println("El propietario o cita no existe");
            return;
        }

        String param;
        Date paramDate;

        param = readLine("Nombre de la cita: ");
        if (!param.equals("")) {
            date.setName(param);
        }

        param = readLine("Descripción: ");
        if (!param.equals("")) {
            date.setDescription(param);
        }

        paramDate = readDate("Fecha comienzo (yyyy-MM-dd HH:mm): ", true);
        if (paramDate != null) {
            date.setFechaComienzo(paramDate);
        }

        paramDate = readDate("Fecha finalización (yyyy-MM-dd HH:mm): ", true);
        if (paramDate != null) {
            date.setFechaFinalizado(paramDate);
        }

        param = readLine("Lugar: ");
        if (!param.equals("")) {
            date.setLugar(param);
        }

        dcli.edit(date, idUs.toString(), idCita.toString());
        dcli.close();
        calendarClient.close();
        userClient.close();
    }

    private static void requestUserCalendars() {
        Integer idUs = readInt("ID del usuario: ");

        CalendarsClient calcli = new CalendarsClient();


        ClientResponse response = calcli.findAll(ClientResponse.class, idUs.toString());
        int status = response.getStatus();        
        if (status == ClientResponse.Status.OK.getStatusCode()) {           
                      
            CalendarUri[] calendars = response.getEntity(CalendarUri[].class);
            System.out.println("Lista de calendarios:");
            for (int i=0; i<calendars.length; i++){
                String calendarId = calendars[i].getUri().replaceFirst(".*/([^/?]+).*", "$1");
                ClientResponse response2 = calcli.find(ClientResponse.class, idUs.toString(), calendarId);
                CalendarResponse respuestaCalendar = response2.getEntity(CalendarResponse.class);
                System.out.println();
                System.out.println("    Id del calendario: "+calendarId);
                System.out.println("    Nombre del calendario: "+ respuestaCalendar.getName());
                System.out.println("    Uri del calendario: "+calendars[i].getUri());
            }
        } else if (status == ClientResponse.Status.NOT_FOUND.getStatusCode()) {
            System.out.println("El propietario o calendario no existe");
        } else if (status == ClientResponse.Status.NO_CONTENT.getStatusCode()) {
            System.out.println("Este usuario no tiene calendarios");
        } else {
            System.out.println("Error desconocido");
        }
        calcli.close();
    }

    private static void requestUsers() {
        UsersClient cli = new UsersClient();

        ClientResponse respuesta = cli.findAllUsers(ClientResponse.class);
        UserUri[] usuarios = respuesta.getEntity(UserUri[].class);
        System.out.println();
        System.out.println("Lista de usuarios:");
        for (int i=0;i<usuarios.length;i++){
            System.out.println();
            String userId = usuarios[i].getUri().replaceFirst(".*/([^/?]+).*", "$1");
            Users user =cli.find_XML(Users.class, userId);
            System.out.println("    Id de usuario: "+user.getUserId());
            System.out.println("    Nombre de usuario: "+user.getName());
            System.out.println("    Uri: "+usuarios[i].getUri());            
        }
        
        cli.close();
    }

    private static void requestDates() {

        DatesClient cli = new DatesClient();

        Integer idUs = readInt("ID del usuario: ");

        ClientResponse response;

        String respuesta = readLine("¿Quiere usar filtros? Si/No: ");
        if (respuesta.equals("No")) {

            response = cli.findDates(ClientResponse.class, idUs.toString(), "-1", "", "", "-1");
        } else {

            System.out.println("Inserte los valores que se indican a continuacion");
            System.out.println("Si no desea asignar ese filtro, pulse enter");

            String fechaComienzo, fechaFinal;
            Integer max;

            Integer idCal = readInt("ID del calendario: ", true);
            fechaComienzo = readLine("Fecha comienzo (yyyy-MM-dd HH:mm): ");
            fechaFinal = readLine("Fecha finalización (yyyy-MM-dd HH:mm): ");
            max = readInt("Número máximo de resultados: ", true);

            response = cli.findDates(ClientResponse.class, idUs.toString(), max.toString(), fechaFinal, fechaComienzo, idCal.toString());
        }

        int status = response.getStatus();
        System.out.println();
        if (status == ClientResponse.Status.OK.getStatusCode()) {
           
            DateUri[] uris = response.getEntity(DateUri[].class);
            System.out.println("Lista de citas:");
            for (int i=0; i<uris.length; i++){
                String dateId = uris[i].getUri().replaceFirst(".*/([^/?]+).*", "$1");
                ClientResponse response2 = cli.find(ClientResponse.class, idUs.toString(), dateId);
                DateResponse respuestaDate = response2.getEntity(DateResponse.class);
                System.out.println();
                System.out.println("    Id de la cita: "+dateId);
                System.out.println("    Nombre de la cita: "+ respuestaDate.getName());
                System.out.println("    Uri de la cita: "+uris[i].getUri());
                
            }

        } else if (status == ClientResponse.Status.NOT_FOUND.getStatusCode()) {
            System.out.println("El propietario o calendario no existe");
        } else if (status == ClientResponse.Status.NO_CONTENT.getStatusCode()) {
            System.out.println("Este calendario no tiene dates");
        } else {
            System.out.println("Error desconocido");
        }

        cli.close();

    }

    private static void editCalendar() {

        CalendarsClient cli = new CalendarsClient();

        Integer idUs = readInt("ID del usuario: ");
        Integer idCal = readInt("ID del calendario: ");
        CalendarResponse calendar;
        UsersClient userClient = new UsersClient();
        DatesClient dateClient = new DatesClient();

        ClientResponse antes = cli.find(ClientResponse.class, idUs.toString(), idCal.toString());
        if (antes.getStatus() == Response.Status.OK.getStatusCode()) {
            calendar = antes.getEntity(CalendarResponse.class);
            
            Calendars calendario = new Calendars();
            calendario.setCalendarId(calendar.getCalendarId());
            
            //Le ponemos el usuario obtenido del id del usuario
            Users usuario = userClient.find_XML(Users.class, idUs.toString());
                    
            calendario.setUserId(usuario);
                               
            System.out.println("Indique los valores a modificar. Si no quiere modificar alguno, pulse Enter para mantener el valor previo.");
            String param = readLine("Nombre del calendario: ");
            if (!param.equals("")) {
                calendario.setName(param);
            }else
                calendario.setName(calendar.getName());

            cli.edit(calendario, idUs.toString(), idCal.toString());
        } else {
            System.out.println("El propietario o cita no existe");           
        }

        dateClient.close();
        userClient.close();
        cli.close();

    }

    private static void requestDate() {
        Integer idUs = readInt("ID del usuario: ");
        Integer idDate = readInt("ID de la cita: ");

        DatesClient cli = new DatesClient();
        ClientResponse response = cli.find(ClientResponse.class, idUs.toString(), idDate.toString());
        int status = response.getStatus();
        System.out.println();
        
        if (status == ClientResponse.Status.OK.getStatusCode()) {
            DateResponse date = response.getEntity(DateResponse.class);
            System.out.print(date.toString());

        } else if (status == ClientResponse.Status.NOT_FOUND.getStatusCode()) {
            System.out.println("El propietario o cita no existe");
        } else {
            System.out.println("Error desconocido");
        }
        cli.close();
    }

    private static void requestCalendar() {
        Integer idUs = readInt("ID del usuario: ");
        Integer idCalendar = readInt("ID del calendario: ");

        CalendarsClient cli = new CalendarsClient();
        ClientResponse response = cli.find(ClientResponse.class, idUs.toString(), idCalendar.toString());
        int status = response.getStatus();
        System.out.println();

        if (status == ClientResponse.Status.OK.getStatusCode()) {

            CalendarResponse respuesta = response.getEntity(CalendarResponse.class);
            System.out.print(respuesta.toString());

        } else if (status == ClientResponse.Status.NOT_FOUND.getStatusCode()) {
            System.out.println("El propietario o calendario no existe");
        } else {
            System.out.println("Error desconocido");
        }
        cli.close();
    }

    private static String readLine(String texto) {
        if (texto != null) {
            System.out.print(texto);
        }
        in.hasNextLine();
        return in.nextLine().trim();
    }

    private static int readInt(String texto) {
        return readInt(texto, false);
    }

    private static int readInt(String texto, boolean opcional) {
        int id = -1;
        do {
            try {
                String l = readLine(texto);
                if (opcional && l.equals("")) {
                    return -1;
                }
                id = Integer.parseInt(l);
            } catch (NumberFormatException e) {
                id = -1;
            }
        } while (id == -1);

        return id;
    }

    private static Date readDate(String texto) {
        return readDate(texto, false);
    }

    private static Date readDate(String texto, boolean opcional) {
        Date date = null;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        do {
            try {
                String l = readLine(texto);
                if (opcional && l.equals("")) {
                    return null;
                }
                date = df.parse(l);
            } catch (ParseException e) {
                date = null;
            }
        } while (date == null);
        System.out.println(date.toString());
        return date;
    }

}
