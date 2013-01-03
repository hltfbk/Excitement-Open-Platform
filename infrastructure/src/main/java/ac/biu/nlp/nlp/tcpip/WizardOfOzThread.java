package ac.biu.nlp.nlp.tcpip;

import java.util.Scanner;

/**
 * A server that asks a local 'wizard' what to tell the user.
 * @author erelsgl
 * @date 17/02/2011
 */
public class WizardOfOzThread extends QueryServerThread {
	private static Scanner scanner = new Scanner(System.in); 

	@Override public String query(String input) {
		System.out.println("The user said '"+input+"'. What should I say?");
    	return scanner.nextLine();
	}

	public static void main(String[] args) {
    	int port = 5000;
    	new QueryServer("Wizard Of Oz Server", "1.0", "Erel Segal")
    		.start(port, WizardOfOzThread.class);
	}
}
