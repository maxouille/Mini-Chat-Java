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
								//On affiche dans le chat
								System.out.println("Vous avez changé votre pseudo en "+myLogin);
								try {
									doc.insertString(doc.getLength(), "Vous avez changé son pseudo en "+myLogin+"\n", style_serveur);
								} catch (BadLocationException e) {
									System.err.println("Erreur ecriture doc client");
								}
								mes.setStyledDocument(doc);
								//On envoi le nouveau login au serveur
								PrintWriter out = new PrintWriter(socket.getOutputStream());
								out.println("NICK/"+myLogin);
								out.flush();
							}
							else {
								//On affiche dans le chat
								System.out.println(oldLogin+" a changé son pseudo en "+newLogin);
								try {
									doc.insertString(doc.getLength(), oldLogin+" a changé son pseudo en "+newLogin+"\n", style_serveur);
								} catch (BadLocationException e) {
									System.err.println("Erreur ecriture doc client");
								}
								mes.setStyledDocument(doc);
							}
						}
						else if (extract[0].equals("ban")) {
							String banned = extract[1];
							String admin = message;
							System.out.println("Ban de l'user "+ banned);
							//Si on est la personne bannie
							if(myLogin.equals(banned)) {
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
							else {
								try {
									doc.insertString(doc.getLength(), banned+ " a été banni par "+admin+"\n", style_serveur);
								} catch (BadLocationException e) {
									System.err.println("Erreur ecriture doc client");
								}
								mes.setStyledDocument(doc);
							}
						}
						else if (extract[0].equals("whoami")) {
							String[] extractMes;
							try {
								extractMes = message.split("s");
								String servaddr = extractMes[0];
								String servport = extractMes[1];
								String myaddr = extractMes[2];
								String myport = extractMes[3];
								System.out.println("whoami de l'user "+myLogin);
								try {
									doc.insertString(doc.getLength(), "Server address : "+servaddr+"\n"+"Server Port : "+servport+"\n"+"My address : "+myaddr+"\n"+"My Port : "+myport+"\n", style_serveur);
								} catch (BadLocationException e) {
									System.err.println("Erreur ecriture doc client");
								}
								mes.setStyledDocument(doc);
							}
							catch(IndexOutOfBoundsException e) {
								System.err.println("erreur try mes");
							}
						}
						else if (extract[0].equals("notadmin")) {
							try {
								doc.insertString(doc.getLength(), message+"\n", style_serveur);
							} catch (BadLocationException e) {
								System.err.println("Erreur ecriture doc client");
							}
							mes.setStyledDocument(doc);							
						}
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
	
	public void add2mes(String login, String message, StyledDocument doc, SimpleAttributeSet style_normal) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String texte_date = sdf.format(new Date());
		nickColor(login, style_normal);
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
	
	public int[] hashToColor (int hashcode) {
		String bin = Integer.toBinaryString(hashcode);
		while(bin.length()<32) {
			bin += "0";
		}	
		String r = bin.substring(0, 7);
		String g = bin.substring(8, 15);
		String b = bin.substring(16, 23);
		int[] res = new int[3];
		res[0] = Integer.parseInt(r, 2);
		res[1] = Integer.parseInt(g, 2);
		res[2] = Integer.parseInt(b, 2);
		return res;
	}
	
	public void nickColor(String login, SimpleAttributeSet style_normal) {
		//On calcule la couleur du pseudo en RGB.
		int hc = login.hashCode();
		int[] colors = hashToColor(hc);
		StyleConstants.setForeground(style_normal, new Color(colors[0], colors[1], colors[2]));
	}
}