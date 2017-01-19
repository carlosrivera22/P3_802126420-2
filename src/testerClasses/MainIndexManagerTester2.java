package testerClasses;

import java.io.IOException;
import java.util.Map;

import dataManagement.MainIndexManager;
/**
 * tests main index manager 
 * @author carlosgrivera
 *
 */
public class MainIndexManagerTester2 {

	private static MainIndexManager mim; 

	public static void main(String[] args) throws IOException {		
		mim = MainIndexManager.getInstance();  	
		
		showWordDocs("orlando"); 
		showWordDocs("pedro"); 
		showWordDocs("juan"); 
		
		mim.close();
	}

	private static void showWordDocs(String word) { 
		System.out.println("Word " + word + " is in the following docs with frequency given: "); 
		try { 
			for (Map.Entry<Integer, Integer> e : mim.getDocsList(word)) 
				System.out.println(e);
		} catch (IllegalArgumentException e) { 
			System.out.println(e.getMessage()); 
		}
	}
}
