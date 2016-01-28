import java.util.Random;
import java.util.Scanner;

import entities.Intermediator;
import entities.Server;
import exceptions.ClientDoesNotExistException;
import exceptions.SessionDoesNotExistException;

public class Demo {

	public static void main(String[] args) {
		
		Scanner scn = new Scanner(System.in);
		
		int n = 20;
		int m = 6;
		int k = 5;

		// TODO: Implement user input for number of clients n, degree k and
		// number of challenges m.
		
		Intermediator intermediator = new Intermediator();
		intermediator.setServer(new Server(intermediator, k, m));

		for(int i=0;i<n;++i){
			intermediator.addClient();
		}
		
		System.out.println("Simulate protocol num:");
		int answer = scn.nextInt();
		int count = 0;
		while(count < answer){
			Random random = new Random();
			long clientId = random.nextInt(n);
			
			try{
				intermediator.login(clientId);
			} catch(ClientDoesNotExistException e){
				e.printStackTrace();
			} catch(SessionDoesNotExistException e){
				e.printStackTrace();
			}
			
			count++;
			if(count == answer) {
				System.out.println(String.format("Number of clients successfuly authenticated: %d", intermediator.getServer().getSuccessfulAuthentications()));
				System.out.println(String.format("Number of clients unsuccessfuly authenticated: %d", answer - intermediator.getServer().getSuccessfulAuthentications()));
				System.out.println(String.format("Number of clients with password solved: %d", intermediator.getNumberOfPasswordSolved()));
				System.out.println(String.format("Number of clients with wrong password solved: %d", intermediator.getNumberOfWrongPasswordSolved()));
			}
		}
		
		scn.close();
	}
	
	
}
