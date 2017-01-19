/**
 * 
 */
package dataManagement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Map.Entry;

import generalClasses.P3Utils;

/**
 * Similar to DocsIDManager class, this class manages the dates of the 
 * documents, to know if they are updated or outdated.
 * @author carlosgrivera
 *
 */
public class FileUpdateManager {
	private ArrayList<Long> docDatesList; //list of dates
	private File fPath; //path of the file or directory 
	private RandomAccessFile file; 
	private boolean updated;  //status of the document 
	private static FileUpdateManager instance = null; 
	private static final int SIZE = 12; 
	
	/**
	 * Creates an instance of the FileUpdateManager object
	 * @return FileUpdateManager object
	 * @throws IOException input or output error 
	 */
	public static FileUpdateManager getInstance() throws IOException { 
		if (instance == null) 
			instance = new FileUpdateManager(); 
		return instance; 
	}
	
	/**
	 * Constructor of the FileUpdateManager object
	 * @throws IOException input or output error with files
	 */
	private FileUpdateManager() throws IOException { 
		updated = false; 
		String fName = "dates.pp3"; //name of the file containg the dates
		fPath = new File(P3Utils.IndexDirectoryPath, fName); //path of dates file
		if (fPath.exists()) {
			file = new RandomAccessFile(fPath, "r"); 
			long listSize = file.length()/SIZE;
			docDatesList = new ArrayList<>((int)(listSize));
			for (int i=0; i<listSize; i++)
				docDatesList.add((long) -1); //fills the list with null values
			readListContentFromFile(); //reads a file's data to add to a list
			file.close();
		}
		else 
			docDatesList = new ArrayList<>(); //if path does not exist it creates an empty list
	}
	
	/**
	 * Reads data from a file containing the date of a document
	 * and its id, and set them in the docDatesList. 
	 * @throws IOException input or output error with files
	 */
	private void readListContentFromFile() throws IOException {
		long fileLength = file.length(); 
		boolean completed = false; 
		while (!completed) {         // this can also be based on docNamesList.size()..,
			try {
				
				int docID = file.readInt(); //reading id from file 
				long date = readDate(); // reading date from file
				docDatesList.set(docID-1, date);   // docID can't be zero
			} catch (IOException e) {
				if (file.getFilePointer() == fileLength)
					completed = true; //there is no more data to read from file
				else
					e.printStackTrace();
			} 
		}
	}
	
	/**
	 * Read a long value from a file that represents the date
	 * @return date in file
	 * @throws IOException input or output error with files
	 */
	private long readDate() throws IOException{
		return file.readLong();
	}
	
	//similar to the method in main index manager 
	/**
	 * Closes the FileUpdateManager by checking if the file is updated 
	 */
	public void close(){
		// iterate over entries in map and write each one to file
				if (updated) {
					try {
						file = new RandomAccessFile(fPath, "rw");
						file.seek(0);
						// ADD MISSING PART HERE for Exercise 2
						
						for (int i=0; i<docDatesList.size(); i++) {
								//P3Utils.writeWordToFile(e.getKey(), file);
								P3Utils.writeIntToFile(i+1,file);
								//writeToDocsListToFile(e.getValue());
								writeDatesListToFile(docDatesList, i);
						}
						file.setLength(file.getFilePointer());
						file.close(); 
					} catch (IOException e) { 
						e.printStackTrace(); 
					}
				}
				
	}
	
	/**
	 * Removes the date from the dates list
	 * @param docID id of the document
	 */
	public void removeDocDate(int docID) {
		docDatesList.set(docID-1, (long) -1);
		updated = true;
	}

	/**
	 * Add a date to the document's file, when it was last modified
	 * @param docID id of the document
	 * @param file file to check
	 */
	public void addDocDate(int docID, File file){
		int n = docDatesList.size()-1;
		if(docID-1 >= n)
			docDatesList.add(file.lastModified());
		else
			docDatesList.set(docID-1,file.lastModified());
		updated = true;
		
	}
	
	/**
	 * Writes data from list to a file
	 * @param list list with data
	 * @param index index of the list data
	 * @throws IOException input or output error with files
	 */
	private void writeDatesListToFile(ArrayList<Long> list, int index) throws IOException{
		file.writeLong(list.get(index));
	}
	
	/**
	 * Checks if the document is updated.
	 * @param docID id of the document
	 * @param date the date in the doc
	 * @return true if the file is updated, false otherwise
	 */
	public boolean isUpdated(int docID, long date){
		if(docDatesList.get(docID-1).equals(date))
			return true;
		else
			return false;
	}
}
