package menuClasses;

/**
 * Defines the types of options that a menu contains. An object
 * of this type contains a description string (wht the menu displays
 * corresponding to that particular option) and an associated 
 * action (another object, of type Action)
 * @author pedroirivera-vega
 *
 */
public class Option {
	public static final Option EXIT = 
			new Option("Exit", new ExitAction()); 
	private String description; //the text that describes what action the option will perform if selected
	private Action action; //the action associated with the option
	
	/**
	 * The option object constructor
	 * @param description the text that describes what action the option will perform
	 * @param action the action associated with the option
	 */
	public Option(String description, Action action) { 
		this.description = description; 
		this.action = action; 
	}
	/**
	 * Gets the description of the option
	 * @return description of the option
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * Gets the action of the option 
	 * @return action of the option
	 */
	public Action getAction() {
		return action;
	}

}
