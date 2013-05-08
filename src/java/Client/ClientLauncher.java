/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Client;

import java.util.List;
import java.util.Scanner;

/**
 *
 * @author xafilox
 */
public class ClientLauncher {
    
    private static Scanner in = new Scanner(System.in);
    
    public static void main(String[] args){
	
	String action;
	while((action = requestAction()) != null){
	    
	    if(action.equals("getUserCalendars")){
		requestUserCalendars();
	    }else
		System.out.println("Acción desconocida\n");
	    
	}
	
	
    }
    
    private static String requestAction(){
	System.out.println("Indique la acción que quiere realizar");
	in.hasNextLine();
	return in.nextLine();
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
    
}
