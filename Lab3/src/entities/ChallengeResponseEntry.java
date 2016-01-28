package entities;

import java.util.Map.Entry;

import org.jlinalg.Vector;
import org.jlinalg.f2.F2;

public class ChallengeResponseEntry implements Entry<Vector<F2>, F2> {
	
	private Vector<F2> challenge;
	private F2 response;

	public ChallengeResponseEntry(Vector<F2> key, F2 value) {
		this.challenge = key;
		this.response = value;
	}

	@Override
	public Vector<F2> getKey() {
		return challenge;
	}

	@Override
	public F2 getValue() {
		return response;
	}

	@Override
	public F2 setValue(F2 value) {
		response = value;
		return response;
	}

}
