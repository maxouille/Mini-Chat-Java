package Serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;


public class ReceptionServeurdepuisClient implements Runnable {

	private BufferedReader in;
	private static String message = null, login = null;
	private Vector<PrintWriter> SocketVector = null;
	private boolean sended = false;
	
	public ReceptionServeurdepuisClient(BufferedReader in, String login, Vector<PrintWriter> sv){
		this.in = in;
		this.login = login;
		SocketVector = sv;
	}
	
	public void run() {
		
		while(true){
	        try {
	        	//On récupère sur l'entrée le message et on l'affiche
				message = in.readLine();
				//On l'affiche sur la console servuer
				System.out.println(login+" : "+message);
				//TODO Il faut renvoyer le message à tous les clients
				//Il faut appeler EmisionServeurversClients sur tout le vector de socket
				for (int i = 0; i < SocketVector.size(); i++) {
					  //On l'envoie au client correspondant au PrintWriter out.
					  SocketVector.elementAt(i).println(message);
					  SocketVector.elementAt(i).flush();
				}
		    } 
	        catch (IOException e) {	
				e.printStackTrace();
			}
		}
	}
	
	public String getMessage() {
		return message;
	}

}
