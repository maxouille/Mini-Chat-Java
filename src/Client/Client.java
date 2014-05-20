package Client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.*;
import java.net.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JTextField;
import javax.swing.JSplitPane;
import javax.swing.JButton;
import javax.swing.KeyStroke;
import javax.swing.JLabel;


public class Client extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	public static Socket socket = null;
	public static Thread t1;
	private JTextField txtMessage;
	private JPanel contentPane;
	
	public Client() {
		setIconImage(Toolkit.getDefaultToolkit().getImage("/home/maxouille/Eclipse/Java/PCV/src/Chat-gris-icon.png"));
		setTitle("Mini-Chat");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100,100,450,300);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5,5,5,5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JMenuBar menuBar = new JMenuBar();
		contentPane.add(menuBar, BorderLayout.NORTH);
				
		JMenu mnConnection = new JMenu("Connection");
		menuBar.add(mnConnection);
				
		JMenuItem mntmConnect = new JMenuItem("Connect");
		mntmConnect.setAction(ConnectAction);
		mnConnection.add(mntmConnect);
				
		JMenuItem mntmDisconnect = new JMenuItem("Disconnect");
		mntmDisconnect.setAction(DisConnectAction);
		mnConnection.add(mntmDisconnect);
				
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.setAction(quitAction);
		menuBar.add(mntmExit);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerLocation(310);
		contentPane.add(splitPane, BorderLayout.SOUTH);
		
		txtMessage = new JTextField();
		txtMessage.setToolTipText("Message");
		splitPane.setLeftComponent(txtMessage);
		txtMessage.setColumns(10);
		
		JButton btnEnvoyer = new JButton("Envoyer");
		splitPane.setRightComponent(btnEnvoyer);
		
		JPanel Connected = new JPanel();
		Connected.setPreferredSize(new Dimension(100,100));
		contentPane.add(Connected, BorderLayout.WEST);
		
		JLabel lblConnected = new JLabel("Connected");
		Connected.add(lblConnected);
		
		JPanel Admin = new JPanel();
		Admin.setPreferredSize(new Dimension(100,100));
		contentPane.add(Admin, BorderLayout.EAST);
		
		JLabel lblAdmin = new JLabel("Admin");
		Admin.add(lblAdmin);
				
		JPanel Chat = new JPanel();
		contentPane.add(Chat, BorderLayout.CENTER);
	}
	
	/**
	 * Action déclenchée lorsque l'on clique sur le bouton quit ou sur l'item
	 * de menu quit.
	 */
	private final Action quitAction = new QuitAction();
	
	/**
	 * Action déclenchée lorsque l'on clique sur le bouton connect ou sur l'item
	 * de menu quit.
	 */
	private final Action ConnectAction = new ConnectAction();
	
	/**
	 * Action déclenchée lorsque l'on clique sur le bouton didconnect ou sur l'item
	 * de menu quit.
	 */
	private final Action DisConnectAction = new DisConnectAction();
	
	public static void main(String[] args) {
	
		/*
		 * On va essayer d'utiliser le look and feel du système s'il est
		 * présent sinon, on utilisera le look and feel par défaut de java
		 */
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			// UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (UnsupportedLookAndFeelException e)
		{
			e.printStackTrace();
		}

		/*
		 * Insertion de la fenêtre dans la file des évènements GUI
		 */
		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					Client frame = new Client();
					frame.setVisible(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Action pour quitter l'application
	 */
	private class QuitAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		/**
		 * Constructeur de l'action pour quitter l'application.
		 * Met en place le raccourci clavier, l'icône et la description
		 * de l'action
		 */
		public QuitAction()
		{
			putValue(NAME, "Quit");
			/*
			 * Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
			 * 	= InputEvent.CTRL_MASK on win/linux
			 *  = InputEvent.META_MASK on mac os
			 */
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q,
					Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			putValue(LARGE_ICON_KEY,
					new ImageIcon("/home/maxouille/Eclipse/Java/PCV/src/images/Quit.png"));
			putValue(SMALL_ICON,
					new ImageIcon("/home/maxouille/Eclipse/Java/PCV/src/images/Quit_small.png"));
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
			try {
				//On ferme la socket
				if (socket != null) socket.close();
			} 
			catch (IOException e1) {
				e1.printStackTrace();
			}
			//On quitte
			System.exit(NORMAL);
		}
	}
	
	/**
	 * Action pour se connecter au serveur
	 */
	private class ConnectAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		/**
		 * Constructeur de l'action pour quitter l'application.
		 * Met en place le raccourci clavier, l'icône et la description
		 * de l'action
		 */
		public ConnectAction() {
			putValue(NAME, "Connect");
			/*
			 * Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
			 * 	= InputEvent.CTRL_MASK on win/linux
			 *  = InputEvent.META_MASK on mac os
			 */
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A,
					Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			putValue(LARGE_ICON_KEY,
					new ImageIcon("/home/maxouille/Eclipse/Java/PCV/src/images/Undo.png"));
			putValue(SMALL_ICON,
					new ImageIcon("/home/maxouille/Eclipse/Java/PCV/src/images/Undo_small.png"));
			putValue(SHORT_DESCRIPTION, "Connects to the server");
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
			try {
				System.out.println("Demande de connexion");
				// On créé une nouvelle socket sur le port et l'adresse passés en arguments.
				socket = new Socket("127.0.0.1"/*InetAddress.getByName("128.78.213.160")*/,2009);
				// /!\ lève une exception de type IOException si ça ne marche pas.
				System.out.println("Connexion établie avec le serveur, authentification :");
				
				// On créé un nouveau thread pour gérer la connexion du client.
				t1 = new Thread(new Connexion(socket));
				t1.start();
			} 
			catch (UnknownHostException ex) {
				System.out.println("Impossible de se connecter à l'adresse");
			} 
			catch (IOException ex) {
				System.out.println("Aucun serveur à l'écoute du port 2009");
			}
		}
	}
	

	/**
	 * Action pour se connecter au serveur
	 */
	private class DisConnectAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		/**
		 * Constructeur de l'action pour se déconnecter du serveur
		 * Met en place le raccourci clavier, l'icône et la description
		 * de l'action
		 */
		public DisConnectAction() {
			putValue(NAME, "Disconnect");
			/*
			 * Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
			 * 	= InputEvent.CTRL_MASK on win/linux
			 *  = InputEvent.META_MASK on mac os
			 */
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E,
					Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			putValue(LARGE_ICON_KEY,
					new ImageIcon("/home/maxouille/Eclipse/Java/PCV/src/images/Delete.png"));
			putValue(SMALL_ICON,
					new ImageIcon("/home/maxouille/Eclipse/Java/PCV/src/images/Delete_small.png"));
			putValue(SHORT_DESCRIPTION, "Disconnects to the server");
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
			try {
				//On ferme la socket
				socket.close();
			} 
			catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
}
