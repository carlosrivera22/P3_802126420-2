package ioManagementClasses;

import java.util.Scanner;

/**
 * Singlenton classs to manage I/O
 * @author pirvos
 *
 */
public class IOComponent {
	private static final  IOComponent 	COMPONENT = new IOComponent(); 
	private Scanner sc; 
	/**
	 * Constructor for the input/output component
	 */
	private IOComponent() { 
		sc=new Scanner(System.in);
	}
	
	/**
	 * Gets the input/output component
	 * @return input/output component
	 */
	public static IOComponent getComponent() { 
		return COMPONENT; 
	}
	/**
	 * This method display a message to ask for some input
	 * @param msg message to display
	 * @return the input given by the user
	 */
	public String getInput(String msg) { 
		System.out.print(msg); 
		return sc.nextLine(); 
	}
	
	/**
	 * This method display a message 
	 * @param line message to be displayed
	 */
	public void output(String line) { 
		System.out.print(line);
	}
	/**
	 * This method displays a messaged and expects an integer input from the user
	 * @param msg message to display
	 * @return integer input from the user 
	 */
	public int getInputInteger(String msg) { 
		int value = 0;    // value to read
		boolean repite; 
		do { 
		    repite = false; 
			try {
				value = Integer.parseInt(this.getInput(msg)); //integer value from user
			} catch(Exception e) { 
				repite = true; 
				System.out.println("  ... invalid integer --");
			}
		} while (repite); //keeps asking for an integer value from the user if the previous input was not an integer

		return value; 
	}
}
