package Serveur;

import java.io.BufferedReader;
import java.io.IOException;


public class ReceptionServeurdepuisClient implements Runnable {

	private BufferedReader in;
	private String message = null, login = null;
	
	public ReceptionServeurdepuisClient(BufferedReader in, String login){
		this.in = in;
		this.login = login;
	}
	
	public void run() {
		
		while(true){
	        try {
	        	//On récupère sur l'entrée le message et on l'affiche
				message = in.readLine();
				System.out.println(login+" : "+message);
				//TODO Il faut renvoyer le message à tous les clients
		    } 
	        catch (IOException e) {	
				e.printStackTrace();
			}
		}
	}

}
