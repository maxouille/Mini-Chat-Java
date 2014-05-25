package Client;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class ReceptionMessages extends Thread implements Runnable {

	private BufferedReader in;
	private Socket socket;
	private static volatile JTextPane mes;
	private String message = ""; 
	private static volatile String myLogin;
	private String login;
	private static volatile JLabel txtLogin;
	private static volatile JFrame chat;
	
	public ReceptionMessages(Socket s, JTextPane m, JLabel labelLog, String log, JFrame c) {
		socket = s;
		txtLogin = labelLog;
		myLogin = log;
		mes = m;
		chat = c;
		try {
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		} catch (IOException e) {
			try {
				socket.close();
			} catch (IOException e1) {
				System.err.println("Erreur fermeture socket catch création in");
			}
		}
	}
	
	public void run() {
		boolean stillhere = true;
		
		//On set un style normal.
		SimpleAttributeSet style_normal = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style_normal, "Calibri");
		StyleConstants.setFontSize(style_normal, 10);
		
		//On set un style serveur.
		SimpleAttributeSet style_serveur = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style_serveur, "Calibri");
		StyleConstants.setFontSize(style_serveur, 10);
		StyleConstants.setForeground(style_serveur, Color.RED);
		
		String saveLogin = "";
		while(stillhere){
	        try {
				// Récupération du style du document 
				StyledDocument doc = mes.getStyledDocument();
				
				//On stocke l'ancien login
				//saveLogin = login;
	        	//On récupère le login envoyé par le serveur
	        	login = in.readLine();
		        // On récupère le message envoyé par le serveur
				message = in.readLine();
				
				//Si message et login null en même temps ça veut dire que le client est parti.
				if(message == null) {
					deconnexion(doc, style_serveur);
					stillhere = false;
					break;
				}
				
				/* Si le login qu'on reçoit commence par /nick */
				else {
					String[] extract;
					try {
						extract = login.split("/");
						if(extract[0].equals("nick")) {
							 /* On sait que c'est un changement de pseudo donc on va récupéré :
							 * login -> "nick/"+ancien login
							 * message -> nouveau login
							 */
							String newLogin = message;
							String oldLogin = extract[1];
							System.out.println("New login : "+newLogin);
							//System.out.println("login stocké : "+oldLogin+" ancien login envoyé : " + oldLogin);
							//Si on est la personne qui a changé de nick
							if(oldLogin.equals(myLogin)) {
								System.out.println("On a matché un ancien login");
								//On change le JLabel en bas à gauche
								txtLogin.setText("");
								txtLogin.setText(" "+newLogin+" : ");
								myLogin = newLogin;
								//On envoi le nouveau login au serveur
								PrintWriter out = new PrintWriter(socket.getOutputStream());
								out.println("NICK/"+myLogin);
								out.flush();
							}
						}
						else if (extract[0].equals("ban")) {
							System.out.println("ban de l'user "+message);
							//Si on est la personne bannie
							if(myLogin.equals(message)) {
								//On cache le chat
								chat.setVisible(false);
								chat.dispose();
								//On envoie au serveur qu'on a quit (pour qu'il remove le couple dans socketserver
								//On envoi le nouveau login au serveur
								PrintWriter out = new PrintWriter(socket.getOutputStream());
								out.println("BAN/"+myLogin);
								out.flush();
								//On quit
								socket.close();
								System.exit(0);
							}
						}
						//ELSEIF WHOAMI
						else {
							add2mes(login, message, doc, style_normal);
						}
					}
					catch (IndexOutOfBoundsException e) {
						System.err.println("erreur");
						add2mes(login, message, doc, style_normal);
					}
				} //FIN DU ELSE MESSAGE==NULL
	        }//FIN TRY
	        catch (IOException e) {	
	        	System.err.println("Fermeture socket dans ReceptionMessages");
	        	try {
					socket.close();
					System.exit(0);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
	        }
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		for(int i = 0; i < login.length(); i++) {
			result = prime * result + ((new String(""+login.charAt(i)+"")).hashCode());
		}
		
		return result;
	}
	
	public int[] hashToColor (int hashcode) {
		System.out.println(hashcode);
		String hc = new String(""+hashcode+"");
		System.out.println(hc);
		String r = hc.substring(0, 2);
		String g = hc.substring(3, 5);
		String b = hc.substring(6, 8);
		int[] res = new int[3];
		res[0] = Integer.parseInt(r);
		res[1] = Integer.parseInt(g);
		res[2] = Integer.parseInt(b);
		return res;
	}
	
	public void nickColor() {
		//On calcule la couleur du pseudo en RGB.
		//int hc = hashCode();
		//int[] colors = hashToColor(hc);
		//StyleConstants.setForeground(style_normal, new Color(colors[0], colors[1], colors[2]));
	}
	
	public void add2mes(String login, String message, StyledDocument doc, SimpleAttributeSet style_normal) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String texte_date = sdf.format(new Date());
		
		try {
			doc.insertString(doc.getLength(), "[" +texte_date+"] "+ login+" > "+message+"\n", style_normal);
		} catch (BadLocationException e) {
			System.out.println("Erreur ecriture mes");
		}
		System.out.println("[" +texte_date+"] "+ login+" > "+message);
		mes.setStyledDocument(doc);
	}
	
	public void deconnexion (StyledDocument doc, SimpleAttributeSet style_serveur) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String texte_date = sdf.format(new Date());
		
		try {
			doc.insertString(doc.getLength(), "[" +texte_date+"] Serveur > Ping TimeOut ...\n", style_serveur);
		} catch (BadLocationException e1) {
			System.err.println("Erreur add to doc deconnexion");
		}
		System.out.println("[" +texte_date+"] Serveur > Ping TimeOut ...");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			System.out.println("Can't sleep");
		}
		try {
			doc.insertString(doc.getLength(), "[" +texte_date+"] Serveur > Déconnexion.\n", style_serveur);
		} catch (BadLocationException e1) {
			System.err.println("Erreur add to doc deconnexion");
		}
		System.out.println("[" +texte_date+"] Serveur > Déconnexion");
		mes.setStyledDocument(doc);
		try {
			socket.close();
		} catch (IOException e1) {
			System.err.println("Erreur close socket deconnexion");
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.out.println("Can't sleep");
		}
		System.exit(-1);
	}
	
}

//else if(extract[0].equals("nick2")) {
/* On sait que c'est un changement de pseudo donc on va récupéré :
* login -> "nick2/"+ancien login
* message -> nouveau login
*/
//Si on est la personne qui a changé de nick
//ICI oldlogin vaut déjà le nouveau pseudo car on revient pour la deuxieme fois.
/*	if(extract[1].equals(oldlogin)) {
	//On affiche dans le chat
	System.out.println(oldlogin+" a changé son pseudo en "+extract[1]);
	try {
		doc.insertString(doc.getLength(), extract[1]+" a changé son pseudo en "+message+"\n", style_serveur);
	} catch (BadLocationException e) {
		System.err.println("Erreur ecriture doc client");
	}
	mes.setStyledDocument(doc);
}
}*/
/*else if(extract[0].equals("you")) {
/* On sait que c'est un changement de pseudo donc on va récupéré :
* login -> "nick2/"+ancien login
* message -> nouveau login
*/
//Si on est la personne qui a changé de nick
//ICI oldlogin vaut déjà le nouveau pseudo car on revient pour la deuxieme fois.
/*if(extract[1].equals(oldlogin)) {
	//On affiche dans le chat
	try {
		doc.insertString(doc.getLength(), "Vous avez changé votre pseudo en "+message+"\n", style_serveur);
	} catch (BadLocationException e) {
		System.err.println("Erreur ecriture doc client");
	}
	System.out.println("Vous avez changé votre pseudo en "+extract[1]+"\n");
	mes.setStyledDocument(doc);
}*/
//}
