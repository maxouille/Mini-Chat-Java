package Serveur;

import java.net.Socket;
import java.util.Vector;

public class CoupleVector {

	private Vector<Couple> coupleVector;

	public CoupleVector () {
		coupleVector = new Vector<Couple> ();
	}
	
	public Vector<Couple> add (Couple c) {
		coupleVector.add(c);
		return coupleVector;
	}
	
	public Vector<Couple> remove (Couple c) {
		coupleVector.remove(c);
		return coupleVector;
	}
	
	public Vector<Couple> remove (Socket s) {
		for (int i = 0 ; i < coupleVector.size(); i++) {
			if(coupleVector.elementAt(i).getSocket().equals(s)) {
				coupleVector.remove(i);
				break;
			}
		}
		return coupleVector;
	}
	
	public Vector<Couple> remove (int i) {
		coupleVector.remove(i);
		return coupleVector;
	}
	
	public int size () {
		return coupleVector.size();
	}
	
	public Couple elementAt(int i) {
		return coupleVector.elementAt(i);
	}
}
