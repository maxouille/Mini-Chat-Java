package Serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Chat_ClientServeur implements Runnable {

	private Socket socket = null;
	private BufferedReader in = null;
	private PrintWriter out = null;
	private String login = "zero";
	private Thread t3, t4;
	
	
	public Chat_ClientServeur(Socket s, String log){
		socket = s;
		login = log;
	}
	
	public void run() {
		
		try {
			// On créé un flux d'entré pour recevoir et un flux de sortie pour écrire.
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream());
			
			//On créé un nouveau Thread pour la reception de messages
			t3 = new Thread(new ReceptionServeurdepuisClient(in,login));
			t3.start();
			//On créé un nouveau Thread pour l'émission de messages
			t4 = new Thread(new EmissionServeurversClients(out));
			t4.start();
		
		} 
		catch (IOException e) {
			System.err.println(login +"s'est déconnecté ");
		}
	}
}
