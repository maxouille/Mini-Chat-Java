package Client;

import java.io.BufferedReader;
import java.io.IOException;


public class ReceptionClientdepuisServeur implements Runnable {

	private BufferedReader in;
	private String message = null, login = null;
	
	public ReceptionClientdepuisServeur(BufferedReader in){
		this.in = in;
	}
	
	public void run() {
		
		while(true){
	        try {
	        	//On récupère le login envoyé par le serveur
	        	login = in.readLine();
		        // On récupère le message envoyé par le serveur
				message = in.readLine();
				//On l'affiche
				System.out.println(login+" : "+message);
		    } 
	        catch (IOException e) {	
				e.printStackTrace();
			}
		}
	}
}
