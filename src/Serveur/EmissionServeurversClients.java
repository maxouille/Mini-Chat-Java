package Serveur;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;


public class EmissionServeurversClients implements Runnable {

	private PrintWriter out;
	private String message = null;
	private Scanner sc = null;
	private Vector<PrintWriter> SocketVector = null;
	
	public EmissionServeurversClients(PrintWriter out, Vector<PrintWriter> sv) {
		this.out = out;
		SocketVector = sv;
	}

	public void run() {
		  sc = new Scanner(System.in);
		  
		  while(true){
			  //On récupère le message tapé avant l'appui sur "Entrée"
			  message = sc.nextLine();
			  for (int i = 0; i < SocketVector.size(); i++) {  
				  //On l'envoie au client correspondant au PrintWriter out.
				  SocketVector.elementAt(i).println(message);
				  SocketVector.elementAt(i).flush();
			  }
		  }
	}
}
