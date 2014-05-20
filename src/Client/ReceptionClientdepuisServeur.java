package Client;

import java.io.BufferedReader;
import java.io.IOException;


public class ReceptionClientdepuisServeur implements Runnable {

	private BufferedReader in;
	private String message = null;
	
	public ReceptionClientdepuisServeur(BufferedReader in){
		this.in = in;
	}
	
	public void run() {
		
		while(true){
	        try {
		        // On récupère le message envoyé par le serveur
				message = in.readLine();
				//On l'affiche
				System.out.println("Le serveur vous dit :" +message);
		    } 
	        catch (IOException e) {	
				e.printStackTrace();
			}
		}
	}
}
