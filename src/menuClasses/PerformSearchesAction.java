package menuClasses;

import ioManagementClasses.IOComponent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import dataManagement.MatchingSearchDocument;
import generalClasses.RankingComparator;
import systemClasses.SystemController;
/**
 * This class is used to perform the action of searching in the system 
 * @author carlosgrivera
 *
 */
public class PerformSearchesAction implements Action {
	private static IOComponent io = IOComponent.getComponent(); 

	@Override
	public void execute(Object arg) {
		SystemController sc = (SystemController) arg; 
		String answer = "y"; 
		while (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")) {//acceptable answers include y for yes or simply yes,Yes or YES.
			io.output("\nSearching Based on Words:\n"); 
			String words = io.getInput("\nEnter words to search for (separate by spaces): "); 
			Map<Integer, MatchingSearchDocument> matchingDocuments = null;
			try {
				StringTokenizer wordsTokens = new StringTokenizer(words); 
				ArrayList<String> wordsList = constructListOfSearchWords(wordsTokens); //constructing a list with words to search 
				matchingDocuments = sc.search(wordsList); //gets the documents that match the search
				if (matchingDocuments.isEmpty()) 
					io.output("No document matches this search.");//if there are no document that matched the search 
				else 
					processMatchingDocuments(matchingDocuments); //initializes the process of the search in the matching documents
			} catch (IOException e) {
				e.printStackTrace();
			}
			answer = io.getInput("\n\n*** Do you want to perform another search: (y/n)? ");//asking to perform another search, if yes it will execute the action again 
		}
	}

	/**
	 * This method initializes the process of the search in 
	 * the matching documents
	 * @param matchingDocuments documents that matched the search
	 * @throws IOException input or output error with IOComponent
	 */
	private void processMatchingDocuments(Map<Integer, MatchingSearchDocument> matchingDocuments) 
			throws IOException 
	{
		ArrayList<MatchingSearchDocument> rankedDocuments = 
				rankMatchingDocuments(matchingDocuments); 
		displayHeaderLinesMatchingDocuments(rankedDocuments); 
		String answer = "y"; 
		while (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")) { 
			int docIndex = io.getInputInteger("\n\n---Please, enter the number of document to see: "); 
			if (docIndex < 1 || docIndex > rankedDocuments.size()) 
				io.output("Invalid index number: " + docIndex);
			else {
				io.output("\n+++++Content of document ranked: " + docIndex + " +++++ \n\n");
				rankedDocuments.get(docIndex-1).displayDocument(0);
			}
			answer = io.getInput("\n\n*** Do you want to display another document: (y/n)? "); 
		}
	}

	/**
	 * This method displays then header of the matching documents
	 * @param rankedDocuments ranked documents that matched the search 
	 * @throws IOException input or output error with IOComponent
	 */
	private void displayHeaderLinesMatchingDocuments(
			ArrayList<MatchingSearchDocument> rankedDocuments) throws IOException {
		
		for (int i=0; i<rankedDocuments.size(); i++) { 
			io.output("\n\n****DOCUMENT " + (i+1) + "****\n");
			rankedDocuments.get(i).displayDocument(3);
		}
		
	}

	/**
	 * This method ranks the matching documents
	 * @param matchingDocuments documents to be ranked
	 * @return list of the documents sorted according to rank 
	 * @throws IOException input or output error with IOComponent
	 */
	private ArrayList<MatchingSearchDocument> rankMatchingDocuments(
			Map<Integer, MatchingSearchDocument> matchingDocuments) throws IOException {
		Comparator<MatchingSearchDocument> c = new RankingComparator();
		
		
		ArrayList<MatchingSearchDocument> rankedDocuments = new ArrayList<>(); 
		
		for (Entry<Integer, MatchingSearchDocument> e : matchingDocuments.entrySet()){  
			rankedDocuments.add(e.getValue()); //adds the matching documents to a list, unsorted or unranked at this point
		}
		
		//Calculates the rank 
		for(MatchingSearchDocument e: rankedDocuments){
			e.calculateRank();
		}
		
		// To use the sorting method given by array list we must create a Comparator that compares the
		// ranks of each document, must create a RankingComparator
		
		//sorting the matched documents according to rank 
		rankedDocuments.sort(c);
	
		return rankedDocuments; 
	}
	
	/**
	 * Constructs a list of the words to search 
	 * @param wordsTokens components of the words to search 
	 * @return list of words to search 
	 */
	private ArrayList<String> constructListOfSearchWords(
			StringTokenizer wordsTokens) {
		ArrayList<String> uniqueWordsList = new ArrayList<>(); 
		while (wordsTokens.hasMoreTokens()) { 
			String word = wordsTokens.nextToken(); 
			if (!uniqueWordsList.contains(word))
				uniqueWordsList.add(word); 
		} 

		return uniqueWordsList;
	}
}


