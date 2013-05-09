/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Client;

import SOSCalendar.Calendars;
import SOSCalendar.Users;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author xafilox
 */
public class ClientLauncher {
    
    private static Scanner in = new Scanner(System.in);
    
    public static void main(String[] args){
	
	int action;
	while((action = requestAction()) != 0){
	    
	    switch(action){
		case 1:createUser(); break;
		default: System.out.println("Acción desconocida\n");
	    }
	    
	}
	
	
    }
    
    private static int requestAction(){
	String leido;
	System.out.println("Indique el número de la acción que quiere realizar");
	System.out.println("[0] Salir del programa");
	System.out.println("[1] Crear un usuario");
	System.out.println("[2] Crear un calendario");
	System.out.println("[3] Crear una cita en un calendario");
	System.out.println("[4] Modificar una cita");
	System.out.println("[5] Obtener lista de citas de un calendario");
	System.out.println("[6] Obtener lista de citas de un usuario");
	System.out.println("[7] Obtener lista de usuarios");
	System.out.print("Número de acción:");
	leido = readLine();
	try{
	    return Integer.parseInt(leido);
	}catch(NumberFormatException e){
	    return -1;
	}
	
    }
    
    private static void createUser(){
	System.out.print("Nombre del usuario: ");
	String name = readLine();
	Users us = new Users();
	us.setName(name);
	UsersClient ucli = new UsersClient();
	ucli.create_XML(us);
    }
    
    private static void createCalendar(){
	System.out.print("Nombre del calendario: ");
	String name = readLine();
	int id = -1;
	do{
	    System.out.print("ID del propietario: ");
	    try{
		id = Integer.parseInt(readLine());
	    }catch(NumberFormatException e){
		id = -1;
	    }
	}while(id == -1);
	
	Calendars cal = new Calendars();
	cal.setUserId(new Users(id));
	cal.setName(name);
	CalendarsClient ucal = new CalendarsClient();
	//ucal.create(name)
    }
    
    private static void requestUserCalendars(){
	System.out.print("ID del usuario:");
	in.hasNextInt();
	Integer id = in.nextInt();
	in.nextLine();
	System.out.println();
	
	CalendarsClient calcli = new CalendarsClient();
	
	//TODO: que hay que pasarlecomo primer argumento?
	//calcli.findAll_XML(List.class, id.toString());
    }
    
    private static String readLine(){
	in.hasNextLine();
	return in.nextLine().trim();
    }
    
}
