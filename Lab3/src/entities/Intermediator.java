package entities;

import java.util.Map;
import java.util.TreeMap;

import org.jlinalg.Vector;
import org.jlinalg.f2.F2;

import exceptions.ClientDoesNotExistException;
import exceptions.SessionDoesNotExistException;

public class Intermediator {

	private Server server;
	private Map<Long, Client> clients;
	private Observer observer;
	private int sumOfRandomChallengesForObtainingPassword = 0;
	private int numberOfPasswordSolved = 0;
	private int numberOfWrongPasswordSolved = 0;
	private boolean passwordSolved;

	public Intermediator() {
		server = new Server(this, 10, 10);
		this.clients = new TreeMap<Long, Client>();
		observer = new Observer(this);
	}

	public void setServer(Server server) {
		this.server = server;
	}
	
	public Server getServer() {
		return this.server;
	}

	public int getNumberOfPasswordSolved() {
		return numberOfPasswordSolved;
	}

	public int getNumberOfWrongPasswordSolved() {
		return numberOfWrongPasswordSolved;
	}

	public void addClient() {
		Client c = server.addClient();
		clients.put(c.getId(), c);
	}

	public void sendResponseToServer(long clientId, int numChallenge,
			int response) throws ClientDoesNotExistException,
			SessionDoesNotExistException {
		/*Scanner scn = new Scanner(System.in);
		System.out
				.println("Do you want to change the response of the client(Y/N)?");
		if (scn.next().equals("Y")) {
			response = scn.nextInt();
		}
		*/
		System.out.println(String.format("send_response(%d)", response));
		
		observer.newResponse(clientId, response);
		
		if (passwordSolved)
			return;
		
		server.sendResponse(clientId, response);
		//scn.close();
	}

	public void clientAuthenticated(long clientId, boolean value) {
		Client c = clients.get(clientId);
		System.out.println(String.format("client_authenticated(%s)", value));
		c.clientAuthenticated(value);
	}

	public void login(Long clientId) throws ClientDoesNotExistException,
			SessionDoesNotExistException {
		
		Client client = clients.get(clientId);
		client.startNewSession();
		System.out.println(client);
		passwordSolved = false;
		observer.newLogin(clientId);
		server.login(clientId);
	}

	public void sendChallenge(Long clientId, int numChallenge, Vector<F2> r)
			throws ClientDoesNotExistException, SessionDoesNotExistException {
		System.out.println(String.format("send_challenge(%s)", r));
		observer.newChallenge(clientId, r);
		clients.get(clientId).sendChallenge(numChallenge, r);
	}

	public void passwordSolved(long clientId, Vector<F2> password,
			int randomChallenges) {
		
		if (passwordSolved) {
			return;
		}
		
		System.out.println(String.format("The password for client: %d, is %s",
				clientId, password.toString()));
		System.out.println(String.format("Real password for client: %d, is %s",
				clientId, clients.get(clientId).password.toString()));
		System.out.println(String.format(
				"The password was obtained after %d challenges",
				randomChallenges));
		
		if (password.equals(clients.get(clientId).password)) {
			numberOfPasswordSolved++;
		} else {
			numberOfWrongPasswordSolved++;
		}
		
		sumOfRandomChallengesForObtainingPassword += randomChallenges;

		System.out
				.println(String
						.format("Average number of random challenges for obtaining password is: %f",
								(1.0 * sumOfRandomChallengesForObtainingPassword)
										/ (numberOfPasswordSolved + numberOfWrongPasswordSolved)));
		
		passwordSolved = true;
	}
}
