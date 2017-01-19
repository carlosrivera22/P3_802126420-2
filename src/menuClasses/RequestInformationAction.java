package menuClasses;

import ioManagementClasses.IOComponent;
import systemClasses.SystemController;

import java.io.IOException;

import dataManagement.DocsIDManager;
/**
 * This class executes the action of requesting information about a document in the system 
 * @author carlosgrivera
 *
 */
public class RequestInformationAction implements Action {
	private static IOComponent io = IOComponent.getComponent(); 
	
	@Override
	public void execute(Object args){
		
		SystemController sc = (SystemController) args;
		String document = io.getInput("\nEnter the name of the document to see it's information.\nYou can use '*' to see the info of all documents: ");
		sc.displayDocumentsInfo(document);
		sc.close();
		
	}

}
