package menuClasses;

import java.util.ArrayList;

import ioManagementClasses.IOComponent;
/**
 * This class allows the creation of a menu with a given number of options to choose from.
 * @author carlosgrivera
 *
 */
public abstract class Menu {
	private String menuString; //title of the menu
	private ArrayList<Option> options; //list of options the menu has

	/**
	 * Initializes the menu. 
	 * @param title the title of the menu
	 * @param options the list of options to be displayed by the menu.
	 *    Each option is displayed numbered 1, 2, .... That number is the
	 *    number used to determine the user's preference when a menu is 
	 *    displayed. 
	 */
	protected void InitializeMenu(String title, ArrayList<Option> options) {
		this.options = options; 
		menuString = "\n\n" + title + "\n"; 
		for (int i=0; i<options.size(); i++) { 
			Option opt = options.get(i); 
			menuString += " <" + (i+1) + "> " + opt.getDescription() + "\n"; 
		}
		menuString += "\nEnter your selection: "; 
	}

	/**
	 * Activates the menu: displays the different options, request a selection from 
	 * user, validate, and return the option corresponding to the user's selection.
	 */
	public Option activate() {
		displayOptions(); //displaying the options of the menu
		int optIndex = getSelectionFromUser(); //get the option selected by the user
		return options.get(optIndex-1);
	}
	
	/**
	 * Adds an option to the menu system
	 * @param opt option to be added
	 */
	protected void addOption(Option opt) { 
		options.add(opt); 
	}
	/**
	 * Displays the options in the menu system
	 */
	private void displayOptions() { 
		IOComponent.getComponent().output(menuString);  
	}
	
	/**
	 * Reads and validates use
	 * @return selection number in the menu
	 */
	private int getSelectionFromUser() { 
		boolean validSelection = false; 
		int selection = 0 ;   // initialize to comply with compiler
		while (!validSelection) { //keeps asking for an option until it is a valid one
			String input = IOComponent.getComponent().getInput(""); 
			try { 
			selection = Integer.parseInt(input);
			if (selection >=1 && selection <= options.size())
				validSelection = true; 
			} catch(Exception e) { 
				// neglect that a non integer value has been entered
				// just write a message
			}
			if (!validSelection) { 
				IOComponent.getComponent().output("***ERROR: Enter an int value in range 1 to "
						+ options.size() + ".\n"); 
				displayOptions(); 
			}
		}
		return  selection; 
	}

}
