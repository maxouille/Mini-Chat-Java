package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class ReceptionClientdepuisServeur extends Thread implements Runnable {

	private BufferedReader in;
	private static JTextPane mes;
	private String message = "", login = "User";
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public ReceptionClientdepuisServeur(BufferedReader in, JTextPane m){
		this.in = in;
		mes = m;
	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public void run() {
		
		while(true){
	        try {
	        	//mes = Container.getInstance().getMes();
	        	//On récupère le login envoyé par le serveur
	        	login = in.readLine();
		        // On récupère le message envoyé par le serveur
				message = in.readLine();
				//On l'affiche
				
				//On calcule la couleur du pseudo en RGB.
				//int hc = hashCode();
				//int[] colors = hashToColor(hc);
				
				//On set un style normal.
				SimpleAttributeSet style_normal = new SimpleAttributeSet();
				StyleConstants.setFontFamily(style_normal, "Calibri");
				StyleConstants.setFontSize(style_normal, 10);
				//StyleConstants.setForeground(style_normal, new Color(colors[0], colors[1], colors[2]));
				
				/*
				 * Récupération du style du document 
				 */
				StyledDocument doc = mes.getStyledDocument();
				
				/*
				 * Insertion d'une chaine de caractères dans le document
				 * insertString :
				 * 	position de départ dans le document (doc.getLength ajoute à la fin
				 *  texte à ajouter
				 *  style pour le texte à ajouter
				 */
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				String texte_date = sdf.format(new Date());
				
				doc.insertString(doc.getLength(), "[" +texte_date+"] "+ login+" > "+message+"\n", style_normal);
				System.out.println("[" +texte_date+"] "+ login+" > "+message);
				mes.setStyledDocument(doc);
		    } 
	        catch (IOException e) {	
				e.printStackTrace();
	        } 
	        catch (BadLocationException e) {
				e.printStackTrace();
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
	
}
