package menuClasses;

import systemClasses.SystemController;
/**
 * This class executes the action of exiting the system
 * @author carlosgrivera
 *
 */
public class ExitAction implements Action {

	@Override
	public void execute(Object arg) {
		SystemController sc = (SystemController) arg; 
		sc.getMenuStack().pop(); 
	}

}
