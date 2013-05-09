/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Client;

import SOSCalendar.Calendars;
import SOSCalendar.Dates;
import SOSCalendar.Users;
import com.sun.jersey.api.client.ClientResponse;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

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
		    System.out.println("Acción aun por crear\n");
		    break;
		case 5:
		    System.out.println("Acción aun por crear\n");
		    break;
		default:
		    System.out.println("Acción desconocida\n");
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
	System.out.println("[5] Ver una cita");
	System.out.println("[6] Obtener lista de usuarios");
	System.out.println("[7] Obtener lista de calendarios de un usuario");
	System.out.println("[8] Obtener lista de citas de un calendario");
	System.out.println("[9] Obtener lista de citas de un usuario");
	
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
	cli.create_XML(us);
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
	    System.out.println("El calendario ha sido creado");
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
	date.setCalendarId(new Calendars(idCal));
	date.setName(readLine("Nombre de la cita: "));
	date.setDescription(readLine("Descripción: "));
	date.setFechaComienzo(readDate("Fecha comienzo (yyyy-MM-dd HH:mm): "));
	date.setFechaFinalizado(readDate("Fecha finalización (yyyy-MM-dd HH:mm): "));
	date.setLugar(readLine("Lugar: "));

	DatesClient cli = new DatesClient();
	ClientResponse resp = cli.create(date, ""+idUs, ""+idCal);
	
	if (resp.getStatus() == ClientResponse.Status.NO_CONTENT.getStatusCode()) {
	    System.out.println("La cita ha sido creada");
	} else if (resp.getStatus() == ClientResponse.Status.NOT_FOUND.getStatusCode()) {
	    System.out.println("El propietario o calendario no existe");
	} else {
	    System.out.println("Error desconocido");
	}
	cli.close();
	
    }
    
    //TODO
    private static void editDate() {
	int idUs = readInt("ID del usuario: ");
	int idCal = readInt("ID del calendario: ");

	Dates date = new Dates();
	date.setCalendarId(new Calendars(idCal));
	date.setName(readLine("Nombre de la cita: "));
	date.setDescription(readLine("Descripción: "));
	date.setFechaComienzo(readDate("Fecha comienzo (yyyy-MM-dd HH:mm): "));
	date.setFechaFinalizado(readDate("Fecha finalización (yyyy-MM-dd HH:mm): "));
	date.setLugar(readLine("Lugar: "));

	DatesClient cli = new DatesClient();
	ClientResponse resp = cli.create(date, ""+idUs, ""+idCal);
	
	if (resp.getStatus() == ClientResponse.Status.NO_CONTENT.getStatusCode()) {
	    System.out.println("La cita ha sido creada");
	} else if (resp.getStatus() == ClientResponse.Status.NOT_FOUND.getStatusCode()) {
	    System.out.println("El propietario o calendario no existe");
	} else {
	    System.out.println("Error desconocido");
	}
	cli.close();
	
    }

    private static void requestUserCalendars() {
	System.out.print("ID del usuario:");
	in.hasNextInt();
	Integer id = in.nextInt();
	in.nextLine();
	System.out.println();

	CalendarsClient calcli = new CalendarsClient();

	//TODO: que hay que pasarlecomo primer argumento?
	//calcli.findAll_XML(List.class, id.toString());
    }

    private static String readLine(String texto) {
	if (texto != null) {
	    System.out.print(texto);
	}
	in.hasNextLine();
	return in.nextLine().trim();
    }

    private static int readInt(String texto){
	return readInt(texto, false);
    }
    
    private static int readInt(String texto, boolean opcional) {
	int id = -1;
	do {
	    try {
		String l = readLine(texto);
		if(opcional && l.equals(""))
		    return -1;
		id = Integer.parseInt(l);
	    } catch (NumberFormatException e) {
		id = -1;
	    }
	} while (id == -1);

	return id;
    }

    private static Date readDate(String texto){
	return readDate(texto, false);
    }
    
    private static Date readDate(String texto, boolean opcional) {
	Date date = null;
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	do {
	    try {
		String l = readLine(texto);
		if(opcional && l.equals(""))
		    return null;
		date = df.parse(l);
	    } catch (ParseException e) {
		date = null;
	    }
	} while (date == null);

	return date;
    }
    
}
