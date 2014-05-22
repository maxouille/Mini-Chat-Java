package Client;

import java.net.*;
import java.awt.Toolkit;
import java.io.*;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JButton;

public class Connexion extends JFrame implements Runnable {

	private static final long serialVersionUID = 1L;

	private Socket socket = null;
	private Thread t2;
	private String login = "User";
	private char[] pass = null;
	private PrintWriter out = null;
	private BufferedReader in = null;
	private boolean connect = false;
	private JPanel contentPane;
	private JTextField Name;
	private JLabel Password;
	private JPasswordField Pass;
	private JFrame connexion = null;
	private JButton btnConnexion;
	
	public Connexion(Socket s){
		
		socket = s;
		
		connexion = new JFrame();
		
		connexion.setIconImage(Toolkit.getDefaultToolkit().getImage("/home/maxouille/Eclipse/Java/PCV/src/Chat-gris-icon.png"));
		connexion.setTitle("Mini-Chat - Connexion");
		connexion.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		connexion.setLocationRelativeTo(null);
		connexion.setSize(new Dimension(400, 120));
		
		contentPane = new JPanel();
		contentPane.setPreferredSize(connexion.getPreferredSize());
		contentPane.setBorder(new EmptyBorder(5,5,5,5));
		connexion.setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{62, 114, 79, 114, 0};
		gbl_contentPane.rowHeights = new int[]{19, 25, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JLabel Pseudo = new JLabel("Pseudo :");
		GridBagConstraints gbc_Pseudo = new GridBagConstraints();
		gbc_Pseudo.anchor = GridBagConstraints.WEST;
		gbc_Pseudo.insets = new Insets(0, 0, 5, 5);
		gbc_Pseudo.gridx = 0;
		gbc_Pseudo.gridy = 0;
		contentPane.add(Pseudo, gbc_Pseudo);
		
		Name = new JTextField();
		GridBagConstraints gbc_Name = new GridBagConstraints();
		gbc_Name.gridx = 1;
		gbc_Name.gridy = 0;
		Name.setColumns(10);
		contentPane.add(Name, gbc_Name);
		
		Pass = new JPasswordField();
		Pass.addActionListener(new OkAction());
		GridBagConstraints gbc_Pass = new GridBagConstraints();
		gbc_Pass.gridx = 1;
		gbc_Pass.gridy = 1;
		Pass.setColumns(10);
		contentPane.add(Pass, gbc_Pass);
		
		
		Password = new JLabel("Password :");
		GridBagConstraints gbc_Password = new GridBagConstraints();
		gbc_Password.anchor = GridBagConstraints.WEST;
		gbc_Password.gridx = 0;
		gbc_Password.gridy = 1;
		contentPane.add(Password, gbc_Password);
		
		btnConnexion = new JButton("Connexion");
		btnConnexion.addActionListener(new OkAction());
		GridBagConstraints gbc_btnConnexion = new GridBagConstraints();
		gbc_btnConnexion.gridwidth = 2;
		gbc_btnConnexion.anchor = GridBagConstraints.NORTHEAST;
		gbc_btnConnexion.gridheight = 3;
		gbc_btnConnexion.gridx = 2;
		gbc_btnConnexion.gridy = 0;
		
		contentPane.add(btnConnexion, gbc_btnConnexion);
		
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
			pass = Pass.getPassword();
			
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
