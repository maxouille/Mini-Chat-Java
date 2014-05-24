package Client;

import java.io.*;
import java.net.*;

public class Client {
	
	public static Socket socket = null;
	public static Thread t1;

	public static void main(String[] args) {
		try {
			System.out.println("Demande de connexion");
			// On créé une nouvelle socket sur le port et l'adresse passés en arguments.
			socket = new Socket("127.0.0.1"/*InetAddress.getByName("128.78.213.160")*/,2009);
			// /!\ lève une exception de type IOException si ça ne marche pas.
			System.out.println("Connexion établie avec le serveur, authentification...");
			
			// On créé un nouveau thread pour gérer la connexion du client.
			t1 = new Thread(new Connexion(socket));
			t1.start();
		} 
		catch (UnknownHostException ex) {
			System.out.println("Impossible de se connecter à l'adresse");
		} 
		catch (IOException ex) {
			System.out.println("Aucun serveur à l'écoute du port 2009");
		}
	}
}
