package Client;

import java.io.PrintWriter;
import java.util.Scanner;


public class EmissionClientversServeur implements Runnable {

	private PrintWriter out;
	private String login = null, message = null;
	private Scanner sc = null;
	
	public EmissionClientversServeur(PrintWriter out, String l) {
		this.out = out;
		login = l;
	}
	
	public void run() {
		
		//On créé un scanner sur l'entrée standard.
		sc = new Scanner(System.in);
		  
		while(true){
			System.out.println("Votre message :");
			//On récupère ce qu'on vient d'écrire
			message = sc.nextLine();
			//On envoie notre login au serveur
			out.println(login);
			out.flush();
			//On envoie le message au serveur
			out.println(message);
			out.flush();
			
		}
	}
}