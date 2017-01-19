package menuClasses;

import java.util.ArrayList;
/**
 * This class contains the main menu of the system 
 * @author carlosgrivera
 *
 */
public class MainMenu extends Menu {
	private static final MainMenu MM = new MainMenu(); 
	/**
	 * Main Menu Constructor
	 */
	private MainMenu() { 
		super(); 
		String title; //title of the menu
		ArrayList<Option> options = new ArrayList<Option>(); //list of options 
		title = "Main Menu"; 
		options.add(new Option("Add a new document", new AddDocumentAction())); //option to add a document to the system.
		options.add(new Option("Remove a document", new RemoveDocumentAction())); //option to remove a document from the system.
		options.add(new Option("Request information about a document", new RequestInformationAction())); //option to request information from documents in the system
		options.add(new Option("Perform searches based on words", new PerformSearchesAction())); //option to perform searches in documents based on words.
		options.add(Option.EXIT); //option to exit the system

		super.InitializeMenu(title, options); //initializes the menu with all its options
	}
	
	/**
	 * gets the main menu object
	 * @return main manu
	 */
	public static MainMenu getMainMenu() { 
		return MM; 
	}
}
