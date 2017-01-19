package systemClasses;

import generalClasses.P3Utils;


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import menuClasses.MainMenu;
import menuClasses.Menu;
import menuClasses.Option;
import dataManagement.DocsIDManager;
import dataManagement.Document;
import dataManagement.DocumentIDX;
import dataManagement.FileUpdateManager;
import dataManagement.MainIndexManager;
import dataManagement.MatchingSearchDocument;
import dataManagement.WordInDocument;

/**
 * Main class of the system. It controls fundamental operations of the
 * system. 
 * 
 * @author pedroirivera-vega
 *
 */
public class SystemController {
	private static SystemController instance = null; 
	private MainIndexManager mim;        // manager of main index data
	private DocsIDManager didm;          // manager of documents ids in the system
	private Stack<Menu> mStack;          // stack to manage actions in menus in this system
	private FileUpdateManager fum;
	/**
	 * Returns reference of the unique instance of SystemController in the system. 
	 * @return reference to the unique instance of SystemController
	 * @throws IOException if there are problems with files
	 */
	public static SystemController getInstance() throws IOException { 
		if (instance == null) 
			instance = new SystemController(); 
		
		return instance; 
	}
	
	/**
	 * Initiates the actions of this controller based on the main menu and
	 * user's selected choices. 
	 */
	public void run() { 
		mStack.push(MainMenu.getMainMenu());      // just execute the main menu
		while(!mStack.empty()) {
			Option opt = mStack.peek().activate(); 
			opt.getAction().execute(this); 
		} 
	}
	
	/**
	 * Creates an instance of SystemController -- the only instance that
	 * can exist in the system. 
	 * 
	 * @throws IOException input or output error with files
	 */
	private SystemController() throws IOException { 
		mim = MainIndexManager.getInstance();   // initializes mim with data in main_index.pp3 file
		didm = DocsIDManager.getInstance();     // initializes didm with data in doc_ID.pp3 file
		mStack = new Stack<Menu>();      // used to control menu operations
		fum = FileUpdateManager.getInstance();
	}
		
	/**
	 * Returns reference to the stack object used to manage different
	 * states of the system. 
	 * @return reference to the stack. 
	 */
	public Stack<Menu> getMenuStack() { 
		return mStack; 
	}
		
	/**
	 * To be executed whenever the user initiates an action of 
	 * adding a new document to the system.
	 * 
	 * Tries to register a new document in the index system. 
	 * @param docName name of the file containing the text of the
	 * document to add
	 * @return Returns a string message summarizing the final
	 * result or status of the operation. 
	 * @throws IOException If there are problems with files
	 */
	public String addNewDocument(String docName) 
			throws IOException {

		File docFilePath;   // the path for the document's file
		
		// Call method in P3Utils to validate the name and file. It the file name 
		// is not valid or the corresponding file does not exist in the docs 
		// directory, it then terminates returning the message of an exception
		// with an IllegalArgumentException; in that case, it the addNewDocument
		// execution ends returning the message inside the exception object.
		try { 
			docFilePath = P3Utils.validateDocumentFile(docName); 
		} catch (IllegalArgumentException e) { 
			return e.getMessage(); 
		}

		// If passes, then the file name for the document is valid and the file exists...
		
		// Try to register the new document to the docsID structure (didm object). 
		// If that document does not exist, it assigns a new ID and returns
		// it. If the document exists (a document with same name has been
		// previously registered in the system), it returns -1. 
		int docID = didm.addDocument(docName);
		
		if (docID == -1)
			return "Document " + docName + " already exists in index."; 
		
		RandomAccessFile docFile = new RandomAccessFile(docFilePath, "r"); 
		Map<String, ArrayList<Integer>> documentWordsMap = new Hashtable<>();
		fum.addDocDate(docID, docFilePath);
	
		// Creates the map where entries are pairs (word, list of locations), 
		// where the list of locations contains the integer values of indexes
		// for the first byte of each occurrence of the word in the document 
		// file. 
		fillMapFromDocumentText(documentWordsMap, docFile); 
		docFile.close();

		// Registers the document's words in the mim object. For each such 
		// word, it registers pair (docID, f), where docID is the document ID 
		// assigned to the new document, and f = size of list of its locations
		// in the document (the frequency of the word in the document).
		registerDataInMIM(docID, documentWordsMap); 

		// Create the IDX file corresponding to the content of the new document
		// being added to the system. For each word in the document, it writes
		// the word, followed by the list of locations of that word in the
		// document. See description of idx file in project's specs. 
		saveMapToIDXFile(docID, documentWordsMap); 

		return "Document " + docName + " was successfully added."; // things worked fine			
	}
	
	/**
	 * This method is used to remove a document from the system 
	 * @param docName name of the document
	 * @return Message describing the success of the removal of the document
	 * @throws IOException input or output error with files
	 */
	public String removeDocument(String docName) throws IOException{
		
		int docID = didm.getDocId(docName);
		if(docID == 0)
			return "Document " + docName + " does not exist in index.";//there is no document to remove from the system if it is not in the document id manager 
		didm.removeDocID(docID); //removes the document from the document id manager 
	
		try{
			DocumentIDX idx = new DocumentIDX(docID); 
			
			for(String w: idx.getWords() )
				mim.removeDocID(w, docID); //remove each word from main index manager
			fum.removeDocDate(docID);	//remove from index list
			removeIDXFile(docID); //removes the idx file
			return "Document " + docName + " was succesfully removed.\n";
		}catch(IOException e){
			e.printStackTrace();
		}
		return "Document " + docName + " was successfully removed.";
	}

	/**
	 * Registers all data of the new document in mim structure. For each word in the document, 
	 * there will be a pair (docID, frequency) that will be added. 
	 * @param docID the id of the new document being added
	 * @param documentWordsMap content of the document in a map with entries:
	 *          (word, list of locations)
	 */
	private void registerDataInMIM(int docID, Map<String, ArrayList<Integer>> documentWordsMap) {
		//ADD MISSING CODE HERE	(Exercise 4)
		
		for(Entry<String, ArrayList<Integer>> e : documentWordsMap.entrySet()){
			mim.registerWordInDocument(e.getKey(), docID, e.getValue().size());//registers the words in the main index manager
		}
	
		
	}
	
	/**
	 * This method removes the idx file from the system 
	 * @param docID id of the document
	 * @throws IllegalArgumentException invalid id
	 * @throws IOException input or output error with files
	 */
	private void removeIDXFile(int docID) throws IllegalArgumentException, IOException{
		String fname = makeIDXName(docID); //making the idx file name given the id
		File idxFilePath = new File(P3Utils.IndexDirectoryPath, fname); //path of the idx file
		if (idxFilePath.exists()) {
			idxFilePath.delete();//deleting the document from the idx path if it exists
		}else{
			throw new IllegalArgumentException("INTERNAL ERROR: idx file does not exist for docid = " + docID);
		}
	}
	/**
	 * Generates the idx file that corresponds to the new document. 
	 * @param docID id of the new document
	 * @param documentWordsMap map containing the words in the document, and, for each word, 
	 * the list of locations where it appears in the document
	 * @throws IOException if there are problems with file: file format, etc. 
	 */
	private void saveMapToIDXFile(int docID, Map<String, ArrayList<Integer>> documentWordsMap) 
	throws IOException 
	{
		String fName = makeIDXName(docID); //making the idx file name given the id
		File idxFilePath = new File(P3Utils.IndexDirectoryPath, fName); //path of the idx file
		if (!idxFilePath.exists()) {
			RandomAccessFile idxFile = new RandomAccessFile(idxFilePath, "rw"); 
			for (Entry<String, ArrayList<Integer>> e: documentWordsMap.entrySet()) { 
				String word = e.getKey(); 
				P3Utils.writeWordToFile(word, idxFile);//writing word to the idx file
				for (Integer location : e.getValue())
					idxFile.writeInt(location);
				idxFile.writeInt(-1);            // marks the end of the list in the file
			}
			
			idxFile.close();
		}
		else 
			throw new IllegalArgumentException("INTERNAL ERROR: An idx file exist for docid = " + docID); 	
	}

	/**
	 * This method executes and important part of the process of adding a new document
	 * to the index: the part of extracting the words from the new document and each one
	 * of the locations (byte indexes) where the word appears in the document. 
	 * Reads the content in document's file. For each different word it ends with 
	 * one entry whose key is the word itself, and the value is the list of locations
	 * of that word in the document. That content is saved in a map collection, in 
	 * entries in which the word is the key and the list of locations is its value.
	 * 
	 * @param documentWordsMap the map to be created
	 * @param docFile the file where the document's content is located.
	 */
	private void fillMapFromDocumentText(Map<String, ArrayList<Integer>> documentWordsMap, 
										 RandomAccessFile docFile) 
	{	
		
		Document document = new Document(docFile); 
		for (WordInDocument wid : document) {
			ArrayList<Integer> wordLocsList = documentWordsMap.get(wid.getWord());	
			// ADD MISSING CODE HERE (Exercise 4)
			if(wordLocsList == null){//creating a list of locations of a word since it does not exist
				wordLocsList = new ArrayList<>();
				wordLocsList.add((int)wid.getLocation());//adding the location of the word in document to the locations list
				documentWordsMap.put(wid.getWord(), wordLocsList);
			}else{
				wordLocsList.add((int)wid.getLocation());//the list of locations exist, so it just adds the location of the word in document
			}
			
		}
		
		
		
		
	}

	/** 
	 * Initiates the search for words in a given list. 
	 * @param wtSearchList the list of words to search
	 * @return A map whose entries are of the form: 
	 *    key = docID, value = list of locations in the particular
	 *    document where one of the words in the search list
	 *    begins (really, the index of its first byte in the file)
	 * @throws IOException input or output error with files
	 * @throws IllegalArgumentException 
	 */
	public Map<Integer, MatchingSearchDocument> search(ArrayList<String> wtSearchList) 
	throws IOException { 
		Map<Integer, MatchingSearchDocument> matchingDocuments =
				new Hashtable<>(); 
		
		// mim is the MainIndexMaganer
		// fills the map of matching documents with as many entries as documents containing at least one
		// of the words in the search list. For each such document, it adds an entry composed of
		// (docID, SearchMatchingDocument object). The search matching document corresponding to 
		// a particular documents in the map will also contain a list of all the words in the search
		// list that it contains. All words as treated as in lower case 
		for (String word : wtSearchList) { 
			Iterable<Entry<Integer, Integer>> docAndWFEntry = 
					mim.getDocsList(word.toLowerCase());  // pairs (d, f) -- one for each doc containing word
			if (docAndWFEntry != null)
				for (Entry<Integer, Integer> docFreqPair : docAndWFEntry)
					addToMatchingDocumentsMap(matchingDocuments, docFreqPair.getKey(), word.toLowerCase(), wtSearchList.size()); 
		}

		for (Entry<Integer, MatchingSearchDocument> e : matchingDocuments.entrySet()) {
			MatchingSearchDocument smd = e.getValue(); 
			smd.buildMatchingLocations();
		}
		
		return matchingDocuments; 	
	}

	/**
	 * To the collection of matching documents add entry for each matching
	 * document, which includes entries consisting of the docID and a
	 * corresponding object of type SearchMatchingDocument. This object
	 * will contain the list of locations in the document that correspond
	 * to the first character in any occurrence of any of the search words
	 * that have been identified as belonging to the document. 
	 * @param mdm Collection of matching documents, initially, an empty map.
	 * @param docID the id number for the matching document
	 * @param word the given word
	 * @throws IllegalArgumentException invalid id
	 * @throws IOException input or output error with files
	 */
	private void addToMatchingDocumentsMap(
			Map<Integer, MatchingSearchDocument> mdm,
			int docID, String word, int numberOfWordsToSearch) 
	throws IllegalArgumentException, IOException {
		MatchingSearchDocument docMD = mdm.get(docID); 
		if (docMD == null) {//if there is no matching document, it creates a new matching search document and adds it to the map of matching documents along with the document's id
			docMD = new MatchingSearchDocument(docID, numberOfWordsToSearch); 
			mdm.put(docID, docMD); 
		}
		docMD.addMatchingWord(word);
	}
	
	/**
	 * Closes the SystemController object. Must be done when exiting the system. 
	 */
	public void close() { 
		mim.close(); 
		didm.close();
		fum.close();
	}

	/**
	 * Makes a valid name of the idx file corresponding to document with given ID. 
	 * @param docID the id of the document. 
	 * @return the name 
	 */
	public static String makeIDXName(int docID) { 
		String s = String.format("idx_%05d.pp3", docID);
		return s; 
	}
	
	/**
	 * This method displays all the documents information, it is useful when the user uses
	 * the '*' character to select all documents
	 */
	private void displayAllDocumentsInfo(){
		ArrayList<String> list = didm.getDocListOfNames();
		//no documents in index
		if(list==null){
			System.out.println("There are no documents in index.");//displays if there are no documents in index
		}else{
			//go over all documents and print the status
			for(String str: list){
				File file = new File(P3Utils.DocsDirectoryPath, str);
				int id = didm.getDocId(str);
				//EN ESTA PARTE VAS A VERIFICAR SI EL DOCUMENTO FUE MODIFICADO
				if(fum.isUpdated(id, file.lastModified()))
					System.out.println("Document: " + str + " is updated!");
				else
					System.out.println("Document: " + str + " is outdated!");
				
			
			}
		}
	}
	
	/**
	 * Displays the information of a single document, whose name is given as parameter
	 * @param document name of the document
	 */
	private void displaySingleDocumentInfo(String document){
		int id = didm.getDocId(document);
		//id can't be zero
		if(id == 0){
			//if the document is not in index it will check if it is not added or simply does not exist
			System.out.println(isFileAdded(P3Utils.DocsDirectoryPath, document));
		}else{
			//print info of file
			File file = new File(P3Utils.DocsDirectoryPath, document);
			if(fum.isUpdated(id, file.lastModified()))
				System.out.println("\nDocument: " + document + " is updated!");
			else
				System.out.println("\nDocument: " + document + " is outdated!");
		}
	}
	
	/**
	 * This method checks if the file is not added or it simply does not exist
	 * @param docFilePath path of the file to check
	 * @param fileName name of the file to check 
	 * @return message indicating if the file was not added or does not exist
	 */
	private String isFileAdded(File docFilePath, String fileName){
		File[] listOfFiles = docFilePath.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {//iterating through all the files found in the file path 
		      if (listOfFiles[i].isFile()) {//checking if the file is a file and not a directory
		    	  int id = didm.getDocId(fileName);//id of the document in index i that are in the file path in a list of documents
		    	  	if(id == 0 && fileName.equals(listOfFiles[i].getName())){
		    	  	//if file is not in index, but exists, it displays the following message
		    	  		return "\nDocument: " + listOfFiles[i].getName() + " is not added!";
		    	  	}
		      } 
		   }
		return "\nDocument: " + fileName + " does not exist in docs directory!";
	}
	
	/**
	 * This method is used to show all files that have not been added to the system.
	 * Useful when the option of '*' is selected.
	 * @param docFilePath path of the documents directory
	 */
	private void showNotAddedFiles(File docFilePath){
		File[] listOfFiles = docFilePath.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {//iterating through all the files found in the file path 
		      if (listOfFiles[i].isFile()) {//checking if the file is a file and not a directory 
		    	  int id = didm.getDocId(listOfFiles[i].getName());//id of the document in index i that are in the file path in a list of documents
		    	  	if(id == 0){
		    	  		//if file is not in index it displays the following message
		    	  		System.out.println("Document: " + listOfFiles[i].getName() + " is not added!");
		    	  	}
		      } 
		   }
	}
	
	/**
	 * This method displays the document or documents information 
	 * @param document name of the document or * that refers to all the documents
	 */
	public void displayDocumentsInfo(String document){
		if(document.equals("*")){
			System.out.println();
			showNotAddedFiles(P3Utils.DocsDirectoryPath);//showing the files that have not been added to the system 
			displayAllDocumentsInfo();//displaying the information of all the files that have been added to the system
		}
		else{
			displaySingleDocumentInfo(document);//displaying information of the single selected document
		}
	}
	
	
	
	
	
}
