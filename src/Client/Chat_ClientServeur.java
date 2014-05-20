package Client;

import java.io.*;
import java.net.*;
//import java.util.Scanner;


public class Chat_ClientServeur implements Runnable {

	private Socket socket;
	private PrintWriter out = null;
	private BufferedReader in = null;
	//private Scanner sc;
	private Thread t3, t4;
	
	public Chat_ClientServeur(Socket s){
		socket = s;
	}
	
	public void run() {
		try {
			// On créé un flux d'entré pour recevoir et un flux de sortie pour écrire.	
			out = new PrintWriter(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			//sc = new Scanner(System.in);
			
			//On lance un thread pour envoyer un message et un autre pour recevoir les messages.
			t4 = new Thread(new EmissionClientversServeur(out));
			t4.start();
			t3 = new Thread(new ReceptionClientdepuisServeur(in));
			t3.start();		    
		} 
		catch (IOException e) {
			System.err.println("Le serveur distant s'est déconnecté !");
		}
	}
}