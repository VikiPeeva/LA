package entities;

import java.util.Random;

import org.jlinalg.Vector;
import org.jlinalg.f2.F2;

import exceptions.ClientDoesNotExistException;
import exceptions.SessionDoesNotExistException;


public class Client {

	Long id;
	Vector<F2> password;
	Intermediator intermediator;
	int wrongAnswers;
	
	public Client(Long id, Vector<F2> password, Intermediator intermediator){
		this.id = id;
		this.password = password;
		this.intermediator = intermediator;
	}
	
	@Override
	public String toString() {
		return String.format("Client with id: %d and password: %s", id, password.toString());
	}

	public Long getId() {
		return id;
	}

	public void sendChallenge(int numChallenge, Vector<F2> r) throws ClientDoesNotExistException, SessionDoesNotExistException {
		sendResponseWithPossibilityForError(numChallenge, r);
	}
	
	private void sendResponseWithPossibilityForError(int numChallenge, Vector<F2> r) throws ClientDoesNotExistException, SessionDoesNotExistException {
		Random random = new Random();
		int count = random.nextInt(100);
		int response = dotProduct(r, password);
		if(count < 70 || (wrongAnswers + 1) * 1.0 > 0.3 * intermediator.getServer().m) {
			intermediator.sendResponseToServer(this.id, numChallenge, response);
		} else {
			wrongAnswers++;
			intermediator.sendResponseToServer(this.id, numChallenge, response);
		}
	}

	private int dotProduct(Vector<F2> one, Vector<F2> two){
		int scalar = 0;
		for(int i=1;i<=Math.min(one.length(), two.length());++i){
			if(one.getEntry(i).multiply(two.getEntry(i)).equals(F2.ONE)){
				scalar++;
			}
		}
		
		return scalar;
	}

	public void clientAuthenticated(boolean value) {
		
	}

	public void startNewSession() {
		wrongAnswers = 0;
	}


}
