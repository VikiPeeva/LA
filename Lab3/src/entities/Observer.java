package entities;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jlinalg.Vector;
import org.jlinalg.f2.F2;

import exceptions.SessionDoesNotExistException;

public class Observer {

	private Map<Long, ObserverSession> clientIdSessionMap;
	private Intermediator intermediator;

	public Observer(Intermediator intermediator) {
		clientIdSessionMap = new TreeMap<Long, ObserverSession>();
		this.intermediator = intermediator;
	}
	
	public void newLogin(long clientId) {
		clientIdSessionMap.put(clientId, new ObserverSession(this, clientId));
	}

	public void newChallenge(long clientId, Vector<F2> challenge) {
		ObserverSession session = clientIdSessionMap.get(clientId);
		if (session == null) {
			session = new ObserverSession(this, clientId);
		}
		session.newChallenge(challenge);
		
		clientIdSessionMap.put(clientId, session);
	}

	public void newResponse(long clientId, int response)
			throws SessionDoesNotExistException {
		ObserverSession session = clientIdSessionMap.get(clientId);
		if (session == null) {
			throw new SessionDoesNotExistException();
		}
		session.newResponse(response);
		
		clientIdSessionMap.put(clientId, session);
	}

	public Set<Vector<F2>> findChallengesSetWithResponse(long clientId)
			throws SessionDoesNotExistException {
		ObserverSession session = clientIdSessionMap.get(clientId);
		if (session == null) {
			throw new SessionDoesNotExistException();
		}
		return session.setSpan();
	}
	
	public void passwordSolved(long clientId, Vector<F2> password, int randomChallenges) {
		intermediator.passwordSolved(clientId, password, randomChallenges);
	}
}
