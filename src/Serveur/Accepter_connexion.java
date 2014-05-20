package Serveur;

import java.io.*;
import java.net.*;

//Passé à un thread donc implémente la classe Runnable
public class Accepter_connexion implements Runnable{

	private ServerSocket socketserver = null;
	private Socket socket = null;

	public Thread t1;
	
	public Accepter_connexion (ServerSocket ss){
		socketserver = ss;
	}
	
	public void run() {
		
		try {
			//Tant que true
			while(true){
			//instruction bloquante : tant qu'il n'y a pas de client on reste bloqué
			socket = socketserver.accept();
			//Il y a un client d'arrivé
			System.out.println("Un zéro veut se connecter  ");
			
			//On créé un nouveau Thread pour l'authentification.
			t1 = new Thread(new Authentification(socket));
			t1.start();
			
			}
		} 
		//levée par accept()
		catch (IOException e) { 
			System.err.println("Erreur serveur");
		}
		
	}
}