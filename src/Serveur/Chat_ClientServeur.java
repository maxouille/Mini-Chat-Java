package Serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;


public class Chat_ClientServeur implements Runnable {

	private Socket socket = null;
	private BufferedReader in = null;
	private String login = "User";
	private Thread t3;
	private Vector<PrintWriter> SocketVector = null;
	
	public Chat_ClientServeur(Socket s, String log, Vector<PrintWriter> sv){
		socket = s;
		login = log;
		SocketVector = sv;
	}
	
	public void run() {
		
		try {
			// On créé un flux d'entré pour recevoir et un flux de sortie pour écrire.
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			int nbsock = 0;
			while(true){
		        try {
		        	//On récupère le login de l'utilisateur
		        	login = in.readLine();
		        	//On récupère sur l'entrée le message et on l'affiche
					String message = in.readLine();
					//On l'affiche sur la console serveur
					System.out.println(login+" : "+message);
					//Il faut appeler EmisionServeurversClients sur tout le vector de socket
					for (int i = 0; i < SocketVector.size(); i++) {
						  //On l'envoie au client correspondant au PrintWriter out.
						  nbsock = i;
						  SocketVector.elementAt(i).println(login);
						  SocketVector.elementAt(i).flush();
						  SocketVector.elementAt(i).println(message);
						  SocketVector.elementAt(i).flush();
					}
			    } 
		        catch (IOException e) {	
					System.err.println("Erreur socket fermée.");
					SocketVector.elementAt(nbsock).close();
					SocketVector.remove(nbsock);
					try {
						in.close();
					} catch (IOException e1) {
						System.err.println("in pas fermé");
					}
				}
			}
			//On créé un nouveau Thread pour la reception de messages
			//t3 = new Thread(new ReceptionServeurdepuisClient(in, SocketVector));
			//t3.start();
		
		} 
		catch (IOException e) {
			System.err.println(login +"s'est déconnecté ");
		}
	}
}
