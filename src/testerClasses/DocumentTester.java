package testerClasses;

import generalClasses.P3Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import dataManagement.Document;
import dataManagement.WordInDocument;
/**
 * Tests document
 * @author carlosgrivera
 *
 */
public class DocumentTester {

	public static void main(String[] args) throws IOException {
		String fName = "doc1"; 
		File fPath = P3Utils.validateDocumentFile(fName); 

		RandomAccessFile file = new RandomAccessFile(fPath, "rw"); 
		
		Document doc = new Document(file); 
		
		/**/
		for (WordInDocument w : doc) 
			System.out.println(w); 
		/**/
		
    	file.close();
	}

}
