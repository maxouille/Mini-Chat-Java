package Serveur;

import java.io.PrintWriter;
import java.util.Scanner;


public class EmissionServeurversClients implements Runnable {

	private PrintWriter out;
	private String message = null;
	private Scanner sc = null;
	
	public EmissionServeurversClients(PrintWriter out) {
		this.out = out;
	}

	public void run() {
		
		  sc = new Scanner(System.in);
		  
		  while(true){
			    System.out.println("Votre message :");
				//On récupère le message tapé avant l'appui sur "Entrée"
			    message = sc.nextLine();
			    //On l'envoie au client de la socket out.
				out.println(message);
			    out.flush();
			    //TODO Renvoyer le message recu vers tous les clients
		  }
	}
}
