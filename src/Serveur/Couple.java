package Serveur;

import java.io.Serializable;
import java.net.Socket;

public class Couple implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Socket socket;
	private String login;
	
	public Couple (Socket s, String l) {
		socket = s;
		login = l;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((login == null) ? 0 : login.hashCode());
		result = prime * result + ((socket == null) ? 0 : socket.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Couple other = (Couple) obj;
		//Si login null -> erreur
		if (login == null) {
			return false;
		}
		else {
			//SI login != -> pas pareil
			if (login != other.login) {
				return false;
			}
			else {
				//Si login ok et sockets pas pareil -> pas bon
				if (!socket.equals(other.socket)) {
					return false;
				}
				else return true;
			}
		}
	}
	
	

}
