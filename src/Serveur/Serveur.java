package Serveur;

import java.io.*;
import java.net.*;

public class Serveur {
	public static ServerSocket ss = null;
	public static Thread t;

	public static void main(String[] args) {
		
		try {
			// On créé une nouvelle socket sur le port passé en argument.
			ss = new ServerSocket(2009);
			// /!\ lève une exception de type IOException si ça ne marche pas.
			System.out.println("Le serveur est à l'écoute du port "+ss.getLocalPort());
			
			// On créé un nouveau thread pour gérer les connexions des clients.
			t = new Thread(new Accepter_connexion(ss));
			t.start();
			
		} 
		catch (IOException e) {
			System.out.println("Le port 2009 est déjà utilisé !");
		}
	
	}

}
