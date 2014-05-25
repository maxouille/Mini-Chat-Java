package Serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Chat_Serveur implements Runnable {

	private Socket socket = null;
	private Thread t5;
	private BufferedReader in = null;
	private PrintWriter out = null;
	private String login = "User", message = "";
	private static volatile CoupleVector SocketVector = null;
	
	public Chat_Serveur(Socket s, String log, CoupleVector sv){
		socket = s;
		login = log;
		SocketVector = sv;
		
		// On créé un flux d'entré pour recevoir et un flux de sortie pour écrire.
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream());
		} catch (IOException e) {
			System.err.println(login +"s'est déconnecté ");
			try {
				socket.close();
				SocketVector.remove(socket);
			} catch (IOException e1) {
				System.err.println("Fermeture de la socket dans chat_clientserveur");
			}
		}
	}
	
	public void run() {
		boolean stillhere = true;
		
		/*t5 = new Thread(new ConnectedRefresh(socket, SocketVector));
		t5.start();*/
		
		while(stillhere){
	        try {
	        	//On récupère sur l'entrée le message et on l'affiche
				message = in.readLine();
				
				//Si message et login null en même temps ça veut dire que le client est parti.
				if(message == null) {
					System.out.println(login+" déconnecté");
					//On ferme la socket
					socket.close();
					//On la supprime du socketVector
					SocketVector.remove(socket);
					//On sort de la boucle
					stillhere = false;
					break;
				}
				
				/* Si on reçoit un message commencant par /NICK
				 * C'est un message de confirmation du changement
				 * de login 
				 */
				else {
					String[] extract;
					try {
						/* On sépare le message en fonction des / */
						extract = message.split("/");
						if (extract[0].equals("NICK")) {	
							//On modifie le couple dans le socketVector
							//Ici, login est encore l'ancien login
							for(int i = 0; i < SocketVector.size(); i++) {
								//Si on trouve le couple correspondant
								if(SocketVector.elementAt(i).getLogin().equals(login)) {
									SocketVector.elementAt(i).setLogin(extract[1]);
									break;
								}
							}
							//On modifie le login associée à la socket.
							login = extract[1];
							
						}
						else if (extract[0].equals("BAN")) {
							//ON FERME LA SOCKET
							System.out.println("ban de l'user "+extract[1]);
							for(int i = 0; i < SocketVector.size(); i++) {
								//Si on trouve le couple correspondant
								if(SocketVector.elementAt(i).getLogin().equals(extract[1])) {
									SocketVector.remove(i);
									System.out.println("Socket removed for user "+extract[1]);
									break;
								}
							}
						}					
						else {
							boolean checked = check_admin(message);
							//Si c'est pas une commande admin on affiche le message classique.
							if(!checked) {
								//On l'affiche sur la console serveur
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
								String texte_date = sdf.format(new Date());
								System.out.println("[" +texte_date+"] "+ login+" > "+message);
								//On envoie le message à tous les clients
								sendAll(login, message);
							}
						}
					}
					catch (IndexOutOfBoundsException e) {
						System.out.println("Erreur split serveur");
					}
				}
		    } 
	        catch (IOException e) {	
				System.err.println("Erreur chat_serveur dans run.");
				try {
					socket.close();
				} catch (IOException e1) {
					System.out.println("Erreur de fermeture de la socket dans chat_clientserveur");
				}
			}
		}		
	}
	
	public boolean check_admin(String message) {
		//On regarde si le message commence par un /
		if (match("^/.*$", message)) {
			//Si c'est /nick
			if(match("^/nick .*$", message)) {
				//On récupère le nouveau pseudo
				String newPseudo = message.substring(6);
				System.out.println("New login : "+newPseudo);
				//On envoie à tout le monde l'ancien login et le nouveau login
				for(int i = 0; i < SocketVector.size(); i++) {
					//On cherche la socet associée au login
					if(SocketVector.elementAt(i).getSocket().equals(socket)) {
						String oldLogin = SocketVector.elementAt(i).getLogin();
						System.out.println("OldLogin found : " + oldLogin);
						SocketVector.elementAt(i).setLogin(newPseudo);
						System.out.println("New login set to "+newPseudo);
						//On envoie à tout le monde
						sendAll("nick/"+oldLogin, newPseudo);
					}
				}
			}
			//Si c'est /ban
			if (match("^/ban .*$", message)) {
				System.out.println("On a matché un ban");
				/* On check si on est admin
				 * c'est à dire qu'on est le premier connecté 
				 * donc à l'emplacement SocketVector[0];
				 */
				if(isAdmin()) {
					// On récupère le pseudo
					String pseudo = message.substring(5);
					System.out.println("Pseudo à ban : "+pseudo);
					sendAll("ban/"+pseudo, SocketVector.elementAt(0).getLogin());
				}
				else {
					out.println("notadmin/"+login);
					out.flush();
					out.println("Vous n'avez pas les droits d'administrateur");
					out.flush();
				}
			}
			//Si c'est un /whoami
			if (match("^/whoami$", message)) {
				System.out.println("On match whoami");
				System.out.println("Pseudo pour whoami : "+login);
				InetAddress servaddr = socket.getInetAddress();
				int servport = socket.getPort();
				InetAddress myaddr = socket.getLocalAddress();
				int myport = socket.getLocalPort();
				System.out.println("Send message : "+servaddr+"s"+servport+"s"+myaddr+"s"+myport);
				out.println("whoami/");
				out.flush();
				out.println(servaddr+"s"+servport+"s"+myaddr+"s"+myport);
				out.flush();
			}
			return true;
		}//fin du match avec /
		return false;
}	 
	
	public void sendAll(String login, String message) {
		PrintWriter out2 = null;
		
		for (int i = 0; i < SocketVector.size(); i++) {
			  //On l'envoie au client correspondant au PrintWriter out.
			  int nbsock = 0;
			  Socket s = (SocketVector.elementAt(i)).getSocket();
			  try {
				  out2 = new PrintWriter(s.getOutputStream());
			  }
			  catch (IOException ex) {
				  try {
					(SocketVector.elementAt(nbsock)).getSocket().close();
				} catch (IOException e) {
					System.out.println("Erreur sendAll close socket");
				}
				  SocketVector.remove(nbsock);
			  }
			  if(isAdmin(login)) {
				  out2.println("@"+login);
			  }
			  else {
				  out2.println(login);
			  }
			  out2.flush();
			  out2.println(message);
			  out2.flush();
		}
	}
	public boolean isAdmin () {
		return SocketVector.elementAt(0).getLogin().equals(login);
	}
	
	public boolean isAdmin(String log) {
		return SocketVector.elementAt(0).getLogin().equals(log);
	}
	
	public boolean match(String regex, String message) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(message);
		return m.matches();
	}
}