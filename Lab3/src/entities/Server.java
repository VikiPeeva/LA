package entities;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.jlinalg.Vector;
import org.jlinalg.f2.F2;

import exceptions.ClientDoesNotExistException;
import exceptions.SessionDoesNotExistException;


public class Server {

	Intermediator intermediator;
	Map<Long, Vector<F2>> clientIdPassworrdMap;
	Map<Long, Session> clientIdSessionMap;
	long idGenerator;
	int k;
	int m;
	int successfulAuthentications;
	int triedAuthentications;
	
	public Server(Intermediator intermediator, int k, int m){
		this.clientIdPassworrdMap = new TreeMap<Long, Vector<F2>>();
		this.clientIdSessionMap = new TreeMap<Long, Session>();
		this.idGenerator = 0;
		this.k = k;
		this.m = m;
		this.intermediator = intermediator;
		this.successfulAuthentications = 0;
		this.triedAuthentications = 0;
	}
	
	public int getSuccessfulAuthentications() {
		return successfulAuthentications;
	}

	public int getTriedAuthentications() {
		return triedAuthentications;
	}

	public Client addClient(){
		
		Vector<F2> password = generateVector();
		Client c = new Client(idGenerator, password, intermediator);
		
		this.clientIdPassworrdMap.put(idGenerator, password);
		this.idGenerator++;
		
		
		return c;
	}
	
	private Vector<F2> generateVector(){
		Random random = new Random();
		F2[] array = new F2[this.k];
		
		for(int i=0;i<this.k;++i){
			int num = random.nextInt(1000);
			array[i] = F2.FACTORY.get(num);
		}
		Vector<F2> vector = new Vector<F2>(array);
		
		return vector;
	}
	
	public void login(Long clientId) throws ClientDoesNotExistException, SessionDoesNotExistException{
		if(!this.clientIdPassworrdMap.containsKey(clientId)){
			throw new ClientDoesNotExistException(String.format("Client with id: %d does not exist.", clientId));
		}
		
		Session newSession = new Session();
		Vector<F2> r = generateVector();
		newSession.setCurrentR(r);
		clientIdSessionMap.put(clientId, newSession);
		
		triedAuthentications++;
		
		intermediator.sendChallenge(clientId, 0, r);
	}

	public void sendResponse(Long clientId, int response) throws ClientDoesNotExistException, SessionDoesNotExistException {
		if(!clientIdPassworrdMap.containsKey(clientId)){
			throw new ClientDoesNotExistException(String.format("Client with id: %d does not exist.", clientId));
		}
		if(!clientIdSessionMap.containsKey(clientId)){
			throw new SessionDoesNotExistException(String.format("Session for client with id: %d does not exist", clientId));
		}
		
		Session session = clientIdSessionMap.get(clientId);
		
		System.out.println("CHALLENGE: " + session.getCurrentChallenge());
		
		if(session.getCurrentChallenge() == this.m && session.errorPercent() <= 30){
			successfulAuthentications++;
			this.intermediator.clientAuthenticated(clientId, true);
			return;
		} else if(session.getCurrentChallenge() == this.m && session.errorPercent() > 30){
			this.intermediator.clientAuthenticated(clientId, false);
			return;
		}
		
		Vector<F2> clientPassword = clientIdPassworrdMap.get(clientId);
		if (dotProduct(clientPassword, session.getCurrentR()) != response) {
			session.addWrongAnswer();
		}
		
		Vector<F2> r = generateVector();
		session.setCurrentR(r);
		session.nextChallenge();
		clientIdSessionMap.put(clientId, session);
		
		intermediator.sendChallenge(clientId, session.getCurrentChallenge(), r);
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
	
}
