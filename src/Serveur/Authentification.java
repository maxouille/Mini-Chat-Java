package Serveur;

import java.net.*;
import java.util.Scanner;
import java.io.*;

public class Authentification implements Runnable {

	private Socket socket;
	private PrintWriter out = null;
	private BufferedReader in = null;
	private String login = "User", pass =  "";
	private boolean authentifier = false;
	private Thread t2;
	private static volatile CoupleVector SocketVector = null;
	
	
	public Authentification(Socket s, CoupleVector sv) {
		 socket = s;
		 SocketVector = sv;
	}
	
	public void run() {
	
		try {
			
			// On créé un flux d'entré pour recevoir et un flux de sortie pour écrire.
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream());
			
			while(!authentifier){	
				//On récupère ce qu'il a écrit
				login = in.readLine();
				
				//On récupère ce qu'il a écrit
				pass = in.readLine();
	
				// On check le login/pass
				if(isValid(login, pass)){
					//On ajoute la socket au vector contenant les sockets de tous les membres
					SocketVector.add(new Couple(socket, login));
					//On envoie au client "connecte"
					out.println("connecte");
					out.flush();
					System.out.println(login +" vient de se connecter.");
					authentifier = true;	
				}
				// Si pas authentifié
				else {
					out.println("erreur"); 
					out.flush();
				}
			}
			
			//On créé un nouveau thread qui exécutera le chat en lui même.
			t2 = new Thread(new Chat_Serveur(socket,login, SocketVector));
			t2.start();	
		} 
		catch (IOException e) {
			System.err.println(login+" ne répond pas !");
			try {
				socket.close();
			} catch (IOException e1) {
				System.err.println("Fermeture de la socket dans identification");
			}
		}
	}
	
	private boolean isValid(String login, String pass) {
		
		boolean connexion = false;
		try {
			Scanner sc = new Scanner(new File("zero.txt"));
			
			//On parcours l'itérateur jusqu'à trouver un login pass correct
			while(sc.hasNext()){
				if(sc.nextLine().equals(login+" "+pass)){
              	  connexion=true;
				  break;
				}
            }
			sc.close();
		} 
		catch (FileNotFoundException e) {	
			System.err.println("Le fichier n'existe pas !");
		}
		return connexion;
	}
}