package Serveur;

import java.io.*;
import java.net.*;

public class Serveur {
	public static ServerSocket ss = null;
	private static Thread t;
	public static volatile CoupleVector SocketVector = null;
	
	public static void main(String[] args) {
		CoupleVector SocketVector = new CoupleVector();
		try {
			// On créé une nouvelle socket sur le port passé en argument.
			ss = new ServerSocket(2009);
			// /!\ lève une exception de type IOException si ça ne marche pas.
			System.out.println("Le serveur est à l'écoute du port "+ss.getLocalPort());
			
			// On créé un nouveau thread pour gérer les connexions des clients.
			/*t = new Thread(new Accepter_connexion(ss, SocketVector));
			t.start();*/
			
			//Tant que true
			while(true){
				//instruction bloquante : tant qu'il n'y a pas de client on reste bloqué
				Socket socket = ss.accept();
				//Il y a un client d'arrivé
				System.out.println("Un client veut se connecter");
				
				//On créé un nouveau Thread pour l'authentification.
				t = new Thread(new Authentification(socket, SocketVector));
				t.start();
			}
			
		} 
		catch (IOException e) {
			System.out.println("Le port 2009 est déjà utilisé !");
		}
	
	}

}
