package Client;

import java.net.*;
import java.util.Scanner;
import java.io.*;

public class Connexion implements Runnable {

	private Socket socket = null;
	public static Thread t2;
	public static String login = null, pass = null, message1 = null, message2 = null, message3 = null;
	private PrintWriter out = null;
	private BufferedReader in = null;
	private Scanner sc = null;
	private boolean connect = false;
	
	public Connexion(Socket s){
		socket = s;
	}
	
	public void run() {
		
		try {
			// On créé un flux d'entré pour recevoir et un flux de sortie pour écrire.	
			out = new PrintWriter(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));	
			//On créé un scanner sur l'entrée standard.
			sc = new Scanner(System.in);
	
			// tant qu'on est pas connecté
			while(!connect ){
				
				//On écrit sur la sortie standard ce qu'on recoit du serveur 
				// Ici "Entrez votre login :"
				System.out.println(in.readLine());
				// On met dans login le login que l'on vient de taper.
				login = sc.nextLine();
				//On envoie le login au serveur
				out.println(login);
				out.flush();
				
				//On affiche ce qu'on recoit du serveur
				//Ici : "Entrez votre mot de passe :"
				System.out.println(in.readLine());
				//On met dans pass le mot de passe que l'ont vient d'écrire.
				pass = sc.nextLine();
				//On l'envoie au serveur
				out.println(pass);
				out.flush();
				
				//Si ce qu'on recoit du serveur est "connecte"
				if(in.readLine().equals("connecte")){
					System.out.println("Je suis connecté "); 
					connect = true;
				}
				else {
					System.err.println("Vos informations sont incorrectes "); 
				}
			}
			//On lance un thread qui s'occupe du chat en lui-même
			t2 = new Thread(new Chat_ClientServeur(socket));
			t2.start();
		
		} 
		catch (IOException e) {
			System.err.println("Le serveur ne répond plus ");
		}
	}
}
