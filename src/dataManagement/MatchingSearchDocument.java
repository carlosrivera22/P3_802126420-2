package dataManagement;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

/**
 * An object of this type is created for each document that 
 * matches a particular search; that is, a document that contains
 * at least one of the words in the searching list. 
 * 
 * @author pedroirivera-vega
 *
 */
public class MatchingSearchDocument {
	private DocumentIDX docIDX;    // contains data read from the idx file of the document
	private Document document;     // the corresponding document....
	//------------
	private float rank; //rank of the document
	private int numberOfWordsToSearch; //how many words the user gave to perform the search
	private Map<String, ArrayList<Long>> matchedLocationsByWord;  // locations in document
	//------------
	// the following is the list of words (from the search list)
	// that are part of the document
	private ArrayList<String> matchingWords;       // words
	private ArrayList<Long> matchingLocations;  // locations in document
	
	/**
	 * MatchingSearchDocument constructor
	 * @param docID id of the document to search on 
	 * @param numberOfWordsToSearch numbers of words to search in the document
	 * @throws IllegalArgumentException invalid id
	 * @throws IOException input or output error with files
	 */
	public MatchingSearchDocument(int docID, int numberOfWordsToSearch) throws IllegalArgumentException, IOException { 
		//--------------
		rank = 0; //rank starts at zero when the object is constructed
		this.numberOfWordsToSearch = numberOfWordsToSearch;
		//--------------
		docIDX = new DocumentIDX(docID);        // instantiates object with data from IDX file
		matchingWords = new ArrayList<>(); //list of matching words 
		matchingLocations = null; //locations that the search matched
		//this document declaration was initially null, explain later what is happening
		document = new Document(docIDX.getDocID()); 
	}
	
	
	/**
	 * Add a new word from the search list, which is identified as part of
	 * the document.
	 * @param word word that matched
	 */
	public void addMatchingWord(String word) { 
		matchingWords.add(word); 
	}
	
	/**
	 * Constructs the list of all locations in the document where one of the matching
	 * words begins. For each matching word, all its locations are included as part
	 * of this list. That list is finally sorted in increasing order.
	 */
	public void buildMatchingLocations() { 
		
		matchedLocationsByWord = new Hashtable<>(); 
		for (String word : matchingWords) {
			ArrayList<Long> list = new ArrayList<>();
			for (Integer location : docIDX.getWordLocations(word))
				list.add((long)location); 
			matchedLocationsByWord.put(word, list);
		}
		matchingLocations = new ArrayList<>();
		for(ArrayList<Long> list: matchedLocationsByWord.values() )
			for(Long location: list){
				matchingLocations.add(location);
			}
		
		
	}
	
	/**
	 * Get a copy of that locations in document that contain one of the words
	 * in the list of words to search
	 * 
	 * @return the list of locations
	 */
	public ArrayList<Long> getMatchingWordsLocations() { 
		if (matchingWords == null) 
			buildMatchingLocations(); 
		ArrayList<Long> result = new ArrayList<>(); 
		for (Long location : matchingLocations) 
			result.add(location); 
		
		return result; 
	}
	
	/**
	 * This method calculates the rank of a matched document
	 */
	public void calculateRank(){
		 int n = numberOfWordsToSearch; //total number of word that the user wants to search 
		 int r = matchingWords.size(); //number of words that want to be searched that appear on the document at least once
		 float rd = 0; //this is going to be the sum of the frequencies of each word in the matched document
		 float pd = ((float)r)/ (float)n; 
		 
		 for(Entry<String, ArrayList<Long>> e: matchedLocationsByWord.entrySet() ){
			 rd += (float)r*((float)(e.getValue().size())/document.getNumberOfWordsInDocument());
		 }
		 rank = pd + rd;//the ranking is based on the sum of rd and pd that are previously explained
	}
	
	
	/**
	 * Displays a specific number of lines from the specified document. 
	 * If the number of lines is given as 0, then que whole document is
	 * displayed. 
	 * @param nLines number of initial lines from the document to be displayed
	 * @throws IOException input or output error with files
	 */
	public void displayDocument(int nLines) throws IOException { 
		if (document == null) 
			document = new Document(docIDX.getDocID()); 
		document.displayDocumentContent(matchingLocations, nLines);
	}
	/**
	 * Gets the rank of the Matched Document
	 * @return the rank value;
	 */
	public float getRank(){
		 return rank;
	 }

}
