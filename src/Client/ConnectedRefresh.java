package Client;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Vector;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import Serveur.Couple;

public class ConnectedRefresh implements Runnable {

	private Socket socket;
	private JTextPane connectedPane;
	private ObjectInputStream in;
	private Vector<Couple> socketVector =null;
	
	public ConnectedRefresh(Socket s, JTextPane cp) {
		socket = s;
		connectedPane = cp;
		try {
			in = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			System.out.println("Can't initialize in");
		}
	}
	

	public void run () {
		
		while (true) {
			
			connectedPane.setText("");
			
			try {
				socketVector = (Vector<Couple>) in.readObject();
			} 
			catch (ClassNotFoundException | IOException e1) {
				System.out.println("Erreur lecture socketvector client");
			}
			
			//On set un style normal.
			SimpleAttributeSet style_normal = new SimpleAttributeSet();
			StyleConstants.setFontFamily(style_normal, "Calibri");
			StyleConstants.setFontSize(style_normal, 10);
			
			//On set un style client.
			SimpleAttributeSet style_client = new SimpleAttributeSet();
			StyleConstants.setFontFamily(style_client, "Calibri");
			StyleConstants.setFontSize(style_client, 10);
			StyleConstants.setForeground(style_client, Color.GREEN);
				
			/*
			 * Récupération du style du document 
			 */
			StyledDocument doc = connectedPane.getStyledDocument();
			
			
			for(int i = 0; i < socketVector.size(); i++) {
				/*
				 * Insertion d'une chaine de caractères dans le document
				 */
				try {
					doc.insertString(doc.getLength(), "[ x ]"+socketVector.elementAt(i).getLogin(), style_client);
				} catch (BadLocationException e) {
					System.out.println("Erreur ecriture connectedPane");
				}
			}
			
			connectedPane.setStyledDocument(doc);
		}
	}
}
