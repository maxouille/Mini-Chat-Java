package Serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Chat_ClientServeur implements Runnable {

	private Socket socket = null;
	private Thread t5;
	private BufferedReader in = null;
	private String login = "User", message = "";
	private Vector<Couple> SocketVector = null;
	
	public Chat_ClientServeur(Socket s, String log, Vector<Couple> sv){
		socket = s;
		// On créé un flux d'entré pour recevoir et un flux de sortie pour écrire.
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			System.err.println(login +"s'est déconnecté ");
			try {
				socket.close();
				SocketVector.remove(socket);
			} catch (IOException e1) {
				System.err.println("Fermeture de la socket dans chat_clientserveur");
			}
		}
		login = log;
		SocketVector = sv;
	}
	
	public void run() {
		boolean stillhere = true;
		int nbsock = 0;
		PrintWriter out = null;
		
		/*t5 = new Thread(new ConnectedRefresh(socket, SocketVector));
		t5.start();*/
		
		while(stillhere){
	        try {
	        	//On récupère sur l'entrée le message et on l'affiche
				message = in.readLine();
				
				//Si message et login null en même temps ça veut dire que le client est parti.
				if(message == null) {
					System.out.println(login+" déconnecté");
					socket.close();
					SocketVector.remove(socket);
					stillhere = false;
					break;
				}
				
				check_admin(message);
				//On l'affiche sur la console serveur
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				String texte_date = sdf.format(new Date());
				System.out.println("[" +texte_date+"] "+ login+" > "+message);
				
				//On envoie le message à tous les clients
				for (int i = 0; i < SocketVector.size(); i++) {
					  //On l'envoie au client correspondant au PrintWriter out.
					  nbsock = i;
					  Socket s = (SocketVector.elementAt(i)).getSocket();
					  try {
						  out = new PrintWriter(s.getOutputStream());
					  }
					  catch (IOException ex) {
						  (SocketVector.elementAt(nbsock)).getSocket().close();
						  SocketVector.remove(nbsock);
					  }
					  out.println(login);
					  out.flush();
					  out.println(message);
					  out.flush();
				}
		    } 
	        catch (IOException e) {	
				System.err.println("Erreur chat_clientserveur dans run.");
				try {
					socket.close();
					System.exit(0);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
			}
		}		
	}
	
	public void check_admin(String message) {
		System.out.println(message);
		Pattern p_ban = Pattern.compile("^/ban .*$");
		Matcher m_ban = p_ban.matcher(message);
		if(m_ban.matches()) {
			System.out.println("On a matché un ban");
			// On récupère le pseudo
			String pseudo = message.substring(5);
			System.out.println(pseudo+"coucou");
			for(int i = 0; i < SocketVector.size(); i++) {
				//On cherche la socet associée au login
				System.out.println(SocketVector.elementAt(i).getLogin());
				if(SocketVector.elementAt(i).getLogin().equals(pseudo)) {
					System.out.println("on a trouvé une login qui est "+pseudo);
					try {
						System.out.println("On ferme la socket");
						SocketVector.elementAt(i).getSocket().close();
						SocketVector.remove(i);
						//CA FOUT LA MERDE ICI
					} catch (IOException e) {
						System.out.println("Erreur fermeture socket dans check_admin");
					}					
				}
			}
		}
	}	 
}
