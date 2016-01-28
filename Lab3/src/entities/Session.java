package entities;

import org.jlinalg.Vector;
import org.jlinalg.f2.F2;

public class Session {

	private int currentChallenge;
	private Vector<F2> currentR;
	private int wrongAnswers;
	
	public Session(){
		this.currentChallenge = 0;
		this.wrongAnswers = 0;
	}

	public void setCurrentR(Vector<F2> currentR) {
		this.currentR = currentR;
	}

	public int getCurrentChallenge() {
		return currentChallenge;
	}

	public Vector<F2> getCurrentR() {
		return currentR;
	}

	public int getWrongAnswers() {
		return wrongAnswers;
	}
	
	public void addWrongAnswer(){
		this.wrongAnswers++;
	}

	public void nextChallenge() {
		this.currentChallenge++;
	}
	
	public double errorPercent() {
		return wrongAnswers * 100.0 / currentChallenge;
	}
}
