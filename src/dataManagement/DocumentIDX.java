package dataManagement;

import generalClasses.P3Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import systemClasses.SystemController;

/**
 * Map that contains the data in memory of the IDX file corresponding to
 * a particular document in the index. An object of this type is created
 * for any document that matches a particular search. 
 * 
 * @author pedroirivera-vega
 *
 */
public class DocumentIDX {
	private Map<String, ArrayList<Integer>> wordLocationsMap = new Hashtable<>(); 

	// for the moment, only needed for testing purposes...
	private int docID; 
	
	private int docNumberOfWords;    // total number of words registered 
	                                 // the particular document
	
	/**
	 * Initializes this instance with current content of the particular idx file
	 * that corresponds to the identified document.
	 * @param docID id of the document
	 * @throws IOException if file format does not match expected format
	 * @throws IllegalArgumentException if docID does not match any existing
	 *         idx file in the system
	 */
	public DocumentIDX(int docID) throws IOException, IllegalArgumentException { 
		this.docID = docID; 
		String fName = SystemController.makeIDXName(docID); //making the name of the file that is stored in the system 
		File idxFilePath = new File(P3Utils.IndexDirectoryPath, fName); //path of the idx file 
		if (idxFilePath.exists()) { 
			RandomAccessFile idxFile = new RandomAccessFile(idxFilePath, "r"); 
			loadMapContentFromIDXFile(idxFile); 
			idxFile.close();
		}
		else 
			throw new IllegalArgumentException("No document exist for id = " + docID); //if there is no document for the given id 
	
	}

	/**
	 * This method get the data from an idx file and stores it into a map
	 * @param idxFile file to load data from
	 * @throws IOException input or output error with files
	 */
	private void loadMapContentFromIDXFile(RandomAccessFile idxFile) throws IOException {
		long fileLength = idxFile.length(); 
		boolean completed = false; 
		docNumberOfWords = 0; 
		while (!completed) {
			try {
				String word = P3Utils.readWord(idxFile); //reading data from idx file
				ArrayList<Integer> wordLocationsList =
						new ArrayList<>();
				int docID = idxFile.readInt(); //reading id of the document from idx file
				while (docID != -1) { 
					wordLocationsList.add(docID); //add the id to a list
					docID = idxFile.readInt();  //keeps reading id's from the idx file until there are no more to read
				}
				wordLocationsMap.put(word, wordLocationsList); //puts the word and a list of its locations on a map
				docNumberOfWords += wordLocationsList.size(); //the number of words the document has is the total of the locations lists sizes 
			} catch (IOException e) {
				if (idxFile.getFilePointer() == fileLength)
					completed = true; //done reading where there is no more data to read
				else
					e.printStackTrace();
			} 
		}
	}
	
	/**
	 * Gets the total number of registered documents
	 * @return number of registered documents
	 */
	public int numberOfRegisteredWords() { 
		return docNumberOfWords; 
	}
	
	/**
	 * Gets the document's id 
	 * @return document's id 
	 */
	public int getDocID() { 
		return docID; 
	}

	/**
	 * Gets an iterable collection of the locations of a word in a document given as parameter
	 * @param word word to search the locations of
	 * @return iterable collection of locations
	 */
	public Iterable<Integer> getWordLocations(String word) { 
		ArrayList<Integer> locationsList = new ArrayList<>(); //list of locations
		ArrayList<Integer> tempList = wordLocationsMap.get(word); 
		
		if (tempList != null) 
			for (Integer location : tempList) {
				locationsList.add(location); 
		} 
		
		return locationsList; 
	}
	
	/**
	 * Gets an iterable collection of the words in the map 
	 * @return iterable collection of words in a map
	 */
	public Iterable<String> getWords(){
		ArrayList<String> list = new ArrayList<>();
		for(String str: wordLocationsMap.keySet()){
			list.add(str);
		}
		return list;
	}
}
