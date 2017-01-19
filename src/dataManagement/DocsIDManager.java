package dataManagement;

import generalClasses.P3Utils;
import ioManagementClasses.IOComponent;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
/**
 * This class manages the id's of the documents in the system.
 * @author carlosgrivera
 *
 */
public class DocsIDManager {
	
	public static final int NAMELENGTH = 20; //maximum length of the document's name
	private static final int RECSIZE = NAMELENGTH+4;   // 20+4
	private static DocsIDManager instance = null; 
	private static IOComponent io = IOComponent.getComponent(); 
	private ArrayList<String> docNamesList; // list of the names of the documents
	private File fPath; //path of a file
	private RandomAccessFile file; 
	private boolean modified;         // to remember if modifications have been made...
	// needed to write back to file is needed....
	
	/**
	 * Creates an instance of the DocsIDmanager object.  
	 * @return instance of DocsIDManager
	 * @throws IOException input or output error
	 */
	public static DocsIDManager getInstance() throws IOException { 
		if (instance == null) 
			instance = new DocsIDManager(); 
		return instance; 
	}
	
	/**
	 * Private DocsIDManager constructor
	 * @throws IOException input or output error with files
	 */
	private DocsIDManager() throws IOException { 
		modified = false; 
		String fName = "docs_ID.pp3"; 
		//creating the file path for file docs_ID.pp3
		fPath = new File(P3Utils.IndexDirectoryPath, fName); 
		//if file path exists it will fill the docNamesList with empty strings
		if (fPath.exists()) {
			file = new RandomAccessFile(fPath, "r"); 
			int listSize = (int) (file.length()/RECSIZE);
			docNamesList = new ArrayList<String>(listSize); 
			// fill list with empty strings...
			for (int i=0; i<listSize; i++)
				docNamesList.add(""); 
			readListContentFromFile(); 
			file.close();
		}
		//if file path does not exist then docNamesList will be an empty array list
		else 
			docNamesList = new ArrayList<>(); 
	}

	/**
	 * Reads data from a file containing the name of a document
	 * and its id, and set them in the docNamesList. 
	 * @throws IOException input or output error with files
	 */
	private void readListContentFromFile() throws IOException {
		long fileLength = file.length(); 
		boolean completed = false; 
		while (!completed) {         // this can also be based on docNamesList.size()..,
			try {
				String name = readName(); //reading name from file
				int docID = file.readInt(); // reading id (integer value) from file
				docNamesList.set(docID-1, name);   // docID can't be zero
			} catch (IOException e) {
				
				if (file.getFilePointer() == fileLength)
					completed = true; //there is no more data to read from file
				else
					e.printStackTrace();
			} 
		}
	}

	/**
	 * Adds the name of a new document to the docs list. . 
	 * @param name the name of the new document
	 * @return -1 if the document exists already in the system; 
	 * decided by comparing names. Returns i>-1 if the assignment
	 * was successful. In that case, the value returned is the 
	 * id number for the new document,
	 */
	public int addDocument(String name) { 
		int newID = -1; 
		for (int i=0; i<docNamesList.size(); i++) {
			if (newID == -1 && docNamesList.get(i).equals(""))
				newID = i+1;    // zero can't be a doc id
			else if (docNamesList.get(i).equals(name))
				return -1;      // the document exists
		}
		if (newID == -1) {
			docNamesList.add(name); //if document does not exist it can be added to list
			newID = docNamesList.size();   // zero can't be a doc id
		}
		else 
			docNamesList.set(newID-1, name); //if the id exist but does not refers to any document it sets the document's name to that id
		
		modified = true; 
		
		return newID; 
		
	}
	/**
	 * Gets the id that refers to the document's name given as a parameter
	 * @param docName document's name
	 * @return id of the document
	 */
	public int getDocId(String docName){
		return docNamesList.indexOf(docName) + 1;
	}

	/**
	 * Removes the name the documents names list by replacing the name value with
	 * an empty string
	 * @param docID id that refers to the document's name 
	 * @throws IllegalArgumentException invalid id
	 */
	public void removeDocID(int docID) 
	throws IllegalArgumentException {
		try { 
			docNamesList.set(docID-1, "");
		} catch (IndexOutOfBoundsException e) {
			throw new IllegalArgumentException("Invalid docID: " + docID); 
		}
		modified = true; 
	}
	
	/**
	 * Closes the DocsIDManager by first writing to the random access file
	 * if the DocsIDManager has been modified. 
	 */
	public void close() { 
		// iterate over entries in map and write each one to file
		if (modified) {
			try {
				file = new RandomAccessFile(fPath, "rw");
				file.seek(0);
				for (int i=0; i<docNamesList.size(); i++) {
					writeNameToFile(docNamesList.get(i)); 
					file.writeInt(i+1);
				}
				file.setLength(file.getFilePointer());    // truncate the length of the file
				file.close(); 
			} catch (IOException e) { 
				e.printStackTrace(); 
			}
		}

	}
	
	/**
	 * Gets the document's name that is identified by the id given as parameter
	 * @param docID id of the document 
	 * @return document's name
	 * @throws IllegalArgumentException invalid id
	 */
	public String getDocName(int docID) throws IllegalArgumentException { 
		try { 
			return docNamesList.get(docID-1);
		} catch (IndexOutOfBoundsException e) { 
			throw new IllegalArgumentException("No indexed document has id = " + docID); 
		}
	}

	/**
	 * Gets a list of the documents names, that are not emtpy strings, in the docNamesList
	 * @return list of the documents names
	 */
	public ArrayList<String> getDocListOfNames(){
		ArrayList<String> list = new ArrayList<>();
		for(String str: docNamesList){
			//the name should not be an empty string, if it is not then it is added to the list
			if(!str.equals("")){
				list.add(str);
			}
		}
		//if the list has no names then it returns null 
		if(list.size() < 1){
			return null;
		}
		return list;
	}
	
	/**
	 * This method will write a document's name to a file
	 * @param name document's name
	 * @throws IOException input or output error with files
	 */
	private void writeNameToFile(String name) throws IOException {
		for (int i=0; i<name.length(); i++)
			file.writeByte((byte) name.charAt(i)); 
		//the document's name should not be longer than NAMELENGTH given 
		for (int i=name.length(); i < NAMELENGTH; i++)
		    file.writeByte((byte) ' ');         // fill with blanks remaining bytes...
	}

	/**
	 * Read next name from current file pointer in file. 
	 * @param file the random access file corresponding to the main index
	 * @return the next word
	 * @throws IOException input or output error with files
	 */
	private String readName() throws IOException {
		String name = ""; 
		char ch; 
		int i=0;
		while (i < NAMELENGTH) { 
			ch = (char) file.readByte();
			i++;
			if (ch != ' ')
				name += ch; 
			else
				while (i < NAMELENGTH) {   // just position FP to next int
					file.readByte(); 
					i++; 
				}
		}
		return name;    // returns "" if the name is just spaces
	}
	
	// for testing purposes
	/**
	 * Shows the registered documents that are in the system
	 */
	public void showRegisteredDocs() { 
		for (int i=0; i< this.docNamesList.size(); i++) { 
			io.output("( name = " + docNamesList.get(i) 
					+ "  ID = " + (i+1) + ")\n");
		}
	}
	
}
