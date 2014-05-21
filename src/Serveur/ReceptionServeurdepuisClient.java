package Serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;


public class ReceptionServeurdepuisClient implements Runnable {

	private BufferedReader in;
	private String message = "", login = "USer";
	private Vector<PrintWriter> SocketVector = null;
	private int nbsock = 0;
	
	public ReceptionServeurdepuisClient(BufferedReader in, Vector<PrintWriter> sv){
		this.in = in;
		SocketVector = sv;
	}
	
	public void run() {
		
		while(true){
	        try {
	        	//On récupère le login de l'utilisateur
	        	login = in.readLine();
	        	//On récupère sur l'entrée le message et on l'affiche
				message = in.readLine();
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
	}
}
