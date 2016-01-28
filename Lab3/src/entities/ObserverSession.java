package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jlinalg.LinSysSolver;
import org.jlinalg.Matrix;
import org.jlinalg.Vector;
import org.jlinalg.f2.F2;

public class ObserverSession {

	private long clientId;

	private List<Vector<F2>> challenges;
	private List<F2> responses;

	private List<Entry<Vector<F2>, F2>> linearlyIndependentChallenges;

	private Observer observer;

	//private Set<Vector<F2>> span;

	private int randomChallenges;

	public ObserverSession(Observer observer, long clientId) {
		this.challenges = new ArrayList<Vector<F2>>();
		this.responses = new ArrayList<F2>();

		this.linearlyIndependentChallenges = new ArrayList<Entry<Vector<F2>, F2>>();

		this.observer = observer;
		this.randomChallenges = 0;
		this.clientId = clientId;
	}

	public void newChallenge(Vector<F2> challenge) {
		challenges.add(challenge);
		randomChallenges++;
	}

	private F2 convertIntegerInF2(int num) {
		if (num % 2 == 0) {
			return F2.ZERO;
		}
		return F2.ONE;
	}

	public void newResponse(int response) {
		responses.add(convertIntegerInF2(response));

		if (randomChallenges > 1
				&& setSpan(linearlyIndependentChallenges).contains(
						challenges.get(challenges.size() - 1))) {

		} else {
			linearlyIndependentChallenges.add(new ChallengeResponseEntry(
					challenges.get(challenges.size() - 1),
					convertIntegerInF2(response)));
		}

		Vector<F2> password = solvePassword();

		if (password != null) {
			observer.passwordSolved(clientId, password, randomChallenges);
		}
	}

	private Vector<F2> solvePassword() {

		if (!isSolvable()) {
			return null;
		}

		Matrix<F2> coeffMatrix = getCoeffMatrix();

		Vector<F2> freeCoeff = getFreeCoeff();

		return LinSysSolver.solve(coeffMatrix, freeCoeff);

	}

	private Vector<F2> getFreeCoeff() {
		F2[] arrayResponses = new F2[linearlyIndependentChallenges.size()];
		
		for (int i=0;i<linearlyIndependentChallenges.size();++i) {
			arrayResponses[i] = linearlyIndependentChallenges.get(i).getValue();
		}
		
		return new Vector<F2>(arrayResponses);
	}

	private Matrix<F2> getCoeffMatrix() {
		Vector<F2>[] arrayChallenges = new Vector[linearlyIndependentChallenges.size()];
		
		for (int i=0;i<linearlyIndependentChallenges.size();++i) {
			arrayChallenges[i] = linearlyIndependentChallenges.get(i).getKey();
		}
		
		return new Matrix<F2>(arrayChallenges);
	}

	private boolean isSolvable() {
		if (linearlyIndependentChallenges.size() < challenges.get(0).length()) {
			return false;
		}

		return true;
	}

	public Set<Vector<F2>> setSpan(List<Entry<Vector<F2>, F2>> set) {
		Map<Vector<F2>, F2> challengeAndResponseSpan = new HashMap<Vector<F2>, F2>();

		int[] coeff = new int[set.size()];

		enumarate(0, set, coeff, challengeAndResponseSpan);

		return challengeAndResponseSpan.keySet();
	}

	private void enumarate(int k, List<Entry<Vector<F2>, F2>> set, int[] coeff,
			Map<Vector<F2>, F2> span) {
		if (k == coeff.length) {
			process(set, coeff, span);
			return;
		}

		coeff[k] = 0;
		enumarate(k + 1, set, coeff, span);

		coeff[k] = 1;
		enumarate(k + 1, set, coeff, span);
	}

	private void process(List<Entry<Vector<F2>, F2>> set, int[] coeff,
			Map<Vector<F2>, F2> span) {

		F2 response = F2.ZERO;
		Vector<F2> newCombination = set.get(0).getKey().gt(F2.ONE);
		
		for (int i = 0; i < coeff.length; ++i) {

			System.out.println(String.format("i: %d, value: %d, vector: %s", i, coeff[i], set.get(i).getKey()));
			
			if (coeff[i] == 1) {
				newCombination.addReplace(set.get(i).getKey());
				response = response.add(set.get(i).getValue());
			}

		}

		System.out.println(newCombination);
		span.put(newCombination, response);
		System.out.println(span.size());
	}

	public Set<Vector<F2>> setSpan() {
		return setSpan(linearlyIndependentChallenges);
	}
}
