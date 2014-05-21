package Client;

import java.net.*;
import java.awt.Toolkit;
import java.io.*;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class Connexion extends JFrame implements Runnable {

	private static final long serialVersionUID = 1L;

	private Socket socket = null;
	public static Thread t2;
	public static String login = null, pass = null, message1 = null, message2 = null, message3 = null;
	private PrintWriter out = null;
	private BufferedReader in = null;
	private boolean connect = false;
	private JPanel contentPane;
	private JTextField Name;
	private JLabel Password;
	private JTextField Pass;
	private JFrame connexion = null;
	
	public Connexion(Socket s){
		socket = s;
		
		connexion = new JFrame();
		
		connexion.setIconImage(Toolkit.getDefaultToolkit().getImage("/home/maxouille/Eclipse/Java/PCV/src/Chat-gris-icon.png"));
		connexion.setTitle("Mini-Chat - Connexion");
		connexion.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		connexion.setLocationRelativeTo(null);
		connexion.setSize(400,100);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5,5,5,5));
		connexion.setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JLabel Pseudo = new JLabel("Pseudo :");
		GridBagConstraints gbc_Pseudo = new GridBagConstraints();
		gbc_Pseudo.gridheight = 2;
		gbc_Pseudo.insets = new Insets(0, 0, 5, 5);
		gbc_Pseudo.gridx = 0;
		gbc_Pseudo.gridy = 0;
		contentPane.add(Pseudo, gbc_Pseudo);
		
		Name = new JTextField();
		GridBagConstraints gbc_Name = new GridBagConstraints();
		gbc_Name.gridheight = 2;
		gbc_Name.insets = new Insets(0, 0, 5, 0);
		gbc_Name.fill = GridBagConstraints.HORIZONTAL;
		gbc_Name.gridx = 1;
		gbc_Name.gridy = 0;
		contentPane.add(Name, gbc_Name);
		Name.setColumns(10);
		
		Password = new JLabel("Password :");
		GridBagConstraints gbc_Password = new GridBagConstraints();
		gbc_Password.gridheight = 2;
		gbc_Password.insets = new Insets(0, 0, 0, 5);
		gbc_Password.gridx = 0;
		gbc_Password.gridy = 3;
		contentPane.add(Password, gbc_Password);
		
		Pass = new JTextField();
		Pass.addActionListener(new OkAction());
		
		GridBagConstraints gbc_Pass = new GridBagConstraints();
		gbc_Pass.gridheight = 2;
		gbc_Pass.fill = GridBagConstraints.HORIZONTAL;
		gbc_Pass.gridx = 1;
		gbc_Pass.gridy = 3;
		contentPane.add(Pass, gbc_Pass);
		Pass.setColumns(10);
		
		connexion.setVisible(true);
	}
	
	public void run() {
		
		try {
			// On créé un flux d'entré pour recevoir et un flux de sortie pour écrire.	
			out = new PrintWriter(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));	
		} 
		catch (IOException e) {
			System.err.println("Le serveur ne répond plus.");
			try {
				socket.close();
			} catch (IOException e1) {
				System.err.println("La socket est déjà fermée.");
			}
		}
	}
	
	/**
	 * Action pour valider son mdp
	 */
	private class OkAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		/**
		 * Constructeur de l'action pour quitter l'application.
		 * Met en place le raccourci clavier, l'icône et la description
		 * de l'action
		 */
		public OkAction() {
			putValue(NAME, "Connect");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
					Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			putValue(LARGE_ICON_KEY,
					new ImageIcon("/home/maxouille/Eclipse/Java/PCV/src/images/About.png"));
			putValue(SMALL_ICON,
					new ImageIcon("/home/maxouille/Eclipse/Java/PCV/src/images/About_small.png"));
			putValue(SHORT_DESCRIPTION, "Quits the application");
		}

		/**
		 * Opérations réalisées par l'action
		 * @param e l'évènement déclenchant l'action. Peut provenir d'un bouton
		 *            ou d'un item de menu
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			/*
			 * Action à effectuer lorsque l'action "quit" est cliquée :
			 * sortir avec un System.exit() (pas très propre, mais fonctionne)
			 */
			System.out.println("pass récupéré");
			login = Name.getText();
			pass = Pass.getText();
			
			if(!connect ){
				//On envoie le login au serveur
				out.println(login);
				out.flush();
				//On envoie le pass au serveur
				out.println(pass);
				out.flush();
				//Si ce qu'on recoit du serveur est "connecte"
				try {
					if(in.readLine().equals("connecte")){ 
						System.out.println("On lance le chat");
						//On lance un thread qui s'occupe du chat en lui-même
						t2 = new Thread(new Chat_ClientServeur(socket, login));
						t2.start();
						if (connexion != null) {
							connexion.setVisible(false);
							connexion.dispose();
						}
					}
					else {
						System.err.println("Vos informations sont incorrectes "); 
					}
				} 
				catch (IOException e1) {
					System.out.println("Erreur connexion serveur");
					try {
						socket.close();
					} catch (IOException e2) {
						System.err.println("La socket est déjà fermée.");
					}
				}
			}// fin du !connect
		}	
	}
}
