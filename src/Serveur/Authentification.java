package Serveur;

import java.net.*;
import java.util.Scanner;
import java.io.*;

public class Authentification implements Runnable {

	private Socket socket;
	private PrintWriter out = null;
	private BufferedReader in = null;
	private String login = "zero", pass =  null;
	public boolean authentifier = false;
	public Thread t2;
	
	public Authentification(Socket s){
		 socket = s;
	}
	
	public void run() {
	
		try {
			
			// On créé un flux d'entré pour recevoir et un flux de sortie pour écrire.
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream());
			
			while(!authentifier){	
				// On envoie au client
				out.println("Entrez votre login :");
				out.flush();
				//On récupère ce qu'il a écrit
				login = in.readLine();
				
				//On demande au client
				out.println("Entrez votre mot de passe :");
				out.flush();
				//On récupère ce qu'il a écrit
				pass = in.readLine();
	
				// On check le login/pass
				if(isValid(login, pass)){
					//On envoie au client "connecte"
					out.println("connecte");
					out.flush();
					System.out.println(login +" vient de se connecter ");
					authentifier = true;	
				}
				// Si pas authentifié
				else {
					out.println("erreur"); 
					out.flush();
				}
			}
			
			//On créé un nouveau thread qui exécutera le chat en lui même.
			t2 = new Thread(new Chat_ClientServeur(socket,login));
			t2.start();	
		} 
		catch (IOException e) {
			System.err.println(login+" ne répond pas !");
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