package dataManagement;

import generalClasses.P3Utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class manages the main index, this is where all the indexed
 * documents are managed.
 * @author carlosgrivera
 *
 */
public class MainIndexManager {
	private static MainIndexManager instance = null; 
	private Map<String, ArrayList<Entry<Integer, Integer>>> map; 
	private File fPath; 
	private RandomAccessFile file; 
	private boolean modified;         // to remember if modifications have been made...
	// needed to writeback to file is needed....
	
	/**
	 * Creates an instance of the MainIndexManager object
	 * @return MainIndexManager object
	 * @throws IOException input or output error
	 */
	public static MainIndexManager getInstance() throws IOException { 
		if (instance == null) 
			instance = new MainIndexManager(); 
		return instance; 
	}
	
	/**
	 * MainIndexManager constructor
	 * @throws IOException input or output error with files
	 */
	private MainIndexManager() throws IOException { 
		modified = false; 
		map = new Hashtable<>(); 
		String fName = "main_index.pp3"; //name of the main index manager file 
		fPath = new File(P3Utils.IndexDirectoryPath, fName); //path of the previous file
		if (fPath.exists()) {
			file = new RandomAccessFile(fPath, "r"); 
			readMapContentFromFile(); //reads content from a file and stores it in a map
			file.close();
		}
	}
	
	/**
	 * Read data from a file and stores it in a map 
	 * @throws IOException input or output error with files
	 */
	private void readMapContentFromFile() throws IOException {
		long fileLength = file.length(); 
		boolean completed = false; 
		while (!completed) {
			try {
				String word = P3Utils.readWord(file); //reading word from file
				ArrayList<Entry<Integer, Integer>> wordDocsList =
						new ArrayList<>();
				int docID = file.readInt();  //reading the id of the document
				int wordFreq = file.readInt(); //reading the frequency of a word from file
				while (docID != -1) { //storing data into a list of words in documents, until there are no more ids to read
					wordDocsList.add(new AbstractMap.SimpleEntry<Integer, Integer>(docID, wordFreq)); 
					docID = file.readInt(); 
					wordFreq = file.readInt(); 
				}
				map.put(word, wordDocsList);
			} catch (IOException e) {
				if (file.getFilePointer() == fileLength)
					completed = true; //there is no more data to read from file
				else
					e.printStackTrace();
			} 
		}
	}

	/**
	 * Adds the data of a new document to the main index. For each word w, 
	 * it will add a pair: (doc id, frequency of w in doc). 
	 * @param word word to be registered
	 * @param doc document to have word registered to
	 * @param frequency frequency of the word in the document
	 */
	public void registerWordInDocument(String word, int docID, int frequency) { 
		ArrayList<Entry<Integer, Integer>> wordDocsList; 
		// the key in each entry is the docID number
		// the value in each entry is the frequency of that word in 
		// the document. There can be only one entry having a particular
		// key value in this list. We can use a Map for this, but since
		// we are not assuming to large number of documents, and because
		// of the operations we do with these type of lists, no need
		// for that at the moment. The only occasions when we need to
		// get one such entry by the id, is when removing; and also
		// perhaps when adding to guarantee that there are no repetitions. 
		// However, the requirement of no repetitions need to be guarantee
		// by the remove operation and the operation that assigned a new
		// id to a new document; it needs to guarantee no two different
		// docs are assigned the same id. 
		
		wordDocsList = map.get(word); 
		Entry<Integer, Integer> newEntry = new AbstractMap.SimpleEntry<>(docID, frequency); 
		
		if (wordDocsList == null)  {
			// ADD MISSING CODE HERE FOR EXERCISE 1 (LAB)
			map.put(word, new ArrayList<Entry<Integer, Integer>>());
			map.get(word).add(newEntry);
		}
		else 
			wordDocsList.add(newEntry); 
			
		modified = true; 
	}
		
	/**
	 * Gets an iterable collection of the id of a document and the frequency 
	 * of a word given as parameter
	 * @param word word to check frequency from 
	 * @return iterable collection of entries
	 */
	public Iterable<Entry<Integer, Integer>> getDocsList(String word) { 
		ArrayList<Entry<Integer, Integer>> wordDocsList; 
		wordDocsList = map.get(word); 
		return wordDocsList;          // returns null if the word does not exist in index
	}
	
	/**
	 * Remove the particular instance (if any) of the pair (d, f) for
	 * the particular word; where d is the docID and f is the frequency
	 * of the word in that particular document. 
	 * @param word the word
	 * @param docID the document id
	 * @throws IllegalArgumentException invalid document id
	 */
	public void removeDocID(String word, int docID) 
	throws IllegalArgumentException {
		ArrayList<Entry<Integer, Integer>> wordDocsList; 
		wordDocsList = map.get(word); 		
		if (wordDocsList == null) 
			throw new IllegalArgumentException("Word " + word + " is not present in system."); 
		Entry<Integer, Integer> searchEntry = new AbstractMap.SimpleEntry<>(docID, null); 
		int docPosIndex = P3Utils.findIndex(wordDocsList, searchEntry);
		if (docPosIndex == -1) 
			throw new IllegalArgumentException("Word " + word + " is not register as part of document " + docID); 
		
		wordDocsList.remove(docPosIndex); 
		if (wordDocsList.isEmpty()) 
			map.remove(word); 
		
		modified = true; 
	}
	
	/**
	 * When the system is about to shutdown, this method needs to be executed to save 
	 * any modifications made to the main index content while in memory.
	 */
	public void close() { 
		// iterate over entries in map and write each one to file
		if (modified) {
			try {
				file = new RandomAccessFile(fPath, "rw");
				file.seek(0);
				// ADD MISSING PART HERE for Exercise 2
				for(Entry<String, ArrayList<Entry<Integer, Integer>>> e: map.entrySet()){
						P3Utils.writeWordToFile(e.getKey(), file);
						writeToDocsListToFile(e.getValue());
				}
				
				file.close(); 
			} catch (IOException e) { 
				e.printStackTrace(); 
			}
		}

	}
	
	/**
	 * Writes from a list with the document's id and frequencies of words as data into a file
	 * @param list list to be written from
	 * @throws IOException input or output error with files
	 */
	private void writeToDocsListToFile(ArrayList<Entry<Integer, Integer>> list) throws IOException {
		for (Entry<Integer, Integer> e : list) { 
			file.writeInt(e.getKey()); //id of the document
			file.writeInt(e.getValue()); //frequency of the word
		}
		file.writeInt(-1);     // pair (-1, -1) marks the end of the list....
		file.writeInt(-1);
		
	}
	
}
