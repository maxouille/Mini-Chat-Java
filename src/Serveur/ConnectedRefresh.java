package Serveur;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

public class ConnectedRefresh implements Runnable {

	private Socket socket;
	private CoupleVector socketVector;
	private ObjectOutputStream out;
	
	public ConnectedRefresh( Socket s, CoupleVector sv) {
		socket = s;
		socketVector = sv;
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
		} 
		catch (IOException e) {
			System.out.println("Can't create outputstream");
		}
	}
	
	public void run () {
		// On envoie au client la liste de tous les clients connectés.
		while(true) {
			try {
				out.writeObject(socketVector);
				out.flush();
			} 
			catch (IOException e) {
				System.out.println("Erreur envoie socketvector connectedrefresh serveur");
			}
		}
		
	}
}
