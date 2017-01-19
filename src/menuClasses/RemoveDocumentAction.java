package menuClasses;

import java.io.IOException;

import dataManagement.DocsIDManager;
import ioManagementClasses.IOComponent;
import systemClasses.SystemController;

/**
 * This class executes the action of removing a document from the system
 * @author carlosgrivera
 *
 */
public class RemoveDocumentAction implements Action {

	@Override
	public void execute(Object args) {
		// TODO Auto-generated method stub
		SystemController sc = (SystemController) args;
		IOComponent io = IOComponent.getComponent();
		io.output("\nRemoving a document from the index system:\n");
		String docName = io.getInput("\nEnter the name of the document to remove: ").trim();
		String statusMSG = null;
		try{
			statusMSG = sc.removeDocument(docName);
			sc.close();
			io.output(statusMSG);
		}catch(IOException e){
				e.printStackTrace();
		}
	}
}	


