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
                default:
                    requestUsers();
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
            /*String uri = resp.getEntity(String.class);
             System.out.println("Su uri es: " + uri);*/
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

        CalendarsClient cli = new CalendarsClient();
        ClientResponse resp = cli.createDate(date, "" + idUs, "" + idCal);

        if (resp.getStatus() == ClientResponse.Status.NO_CONTENT.getStatusCode()) {
            System.out.println("La cita ha sido creada");

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
        DatesClient dcli = new DatesClient();
        ClientResponse antes = dcli.find(ClientResponse.class, "" + idUs, "" + idCita);

        if (antes.getStatus() == Response.Status.OK.getStatusCode()) {
            date = antes.getEntity(Dates.class);
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

        paramDate = readDate("Fecha comienzo (yyyy-MM-dd HH:mm): ");
        if (paramDate != null) {
            date.setFechaComienzo(paramDate);
        }

        paramDate = readDate("Fecha finalización (yyyy-MM-dd HH:mm): ");
        if (paramDate != null) {
            date.setFechaFinalizado(paramDate);
        }

        param = readLine("Lugar: ");
        if (!param.equals("")) {
            date.setLugar(param);
        }

        dcli.edit(date, idUs.toString(), idCita.toString());
        dcli.close();
    }

    private static void requestUserCalendars() {
        System.out.print("ID del usuario:");
        in.hasNextInt();
        Integer id = in.nextInt();
        in.nextLine();
        System.out.println();

        CalendarsClient calcli = new CalendarsClient();


        ClientResponse response = calcli.findAll(ClientResponse.class, id.toString());
        int status = response.getStatus();
        if (status == ClientResponse.Status.OK.getStatusCode()) {
            //Parsear xml??
            String xml = response.getEntity(String.class);
            System.out.println(xml);
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

        List<Users> usuarios = cli.findAll_XML(List.class);
        for (Users user : usuarios) {
            System.out.println(user.toString());
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

            Integer idCal = readInt("ID del calendario: ");
            fechaComienzo = readLine("Fecha comienzo (yyyy-MM-dd HH:mm): ");
            fechaFinal = readLine("Fecha finalización (yyyy-MM-dd HH:mm): ");
            max = readInt("Número máximo de resultados: ");

            response = cli.findDates(ClientResponse.class, idUs.toString(), max.toString(), fechaFinal, fechaComienzo, idCal.toString());
        }

        int status = response.getStatus();
        if (status == ClientResponse.Status.OK.getStatusCode()) {
            //TODO: Parsear XML?
            String xml = response.getEntity(String.class);
            System.out.println(xml);

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
        Calendars calendar;

        ClientResponse antes = cli.find(ClientResponse.class, idUs.toString(), idCal.toString());
        if (antes.getStatus() == Response.Status.OK.getStatusCode()) {
            calendar = antes.getEntity(Calendars.class);
            System.out.println("Indique los valores a modificar. Si no quiere modificar alguno, pulse Enter para mantener el valor previo.");

        } else {
            System.out.println("El propietario o cita no existe");
            return;
        }

        String param = readLine("Nombre del calendario: ");
        if (!param.equals("")) {
            calendar.setName(param);
        }

        cli.close();

    }

    private static void requestDate() {
        Integer idUs = readInt("ID del usuario: ");
        Integer idDate = readInt("ID de la cita: ");

        DatesClient cli = new DatesClient();
        ClientResponse response = cli.find(ClientResponse.class, idUs.toString(), idDate.toString());
        int status = response.getStatus();

        if (status == ClientResponse.Status.OK.getStatusCode()) {
            Dates date = response.getEntity(Dates.class);
            System.out.println(date.toString());

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

        if (status == ClientResponse.Status.OK.getStatusCode()) {
            
            //TODO: Parsear xml?
            String respuesta = response.getEntity(String.class);
            System.out.println(respuesta);

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

        return date;
    }
}
