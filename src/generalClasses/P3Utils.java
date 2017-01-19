package generalClasses;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Map.Entry;
/**
 * This class 
 * @author carlosgrivera
 *
 */
public class P3Utils {
	public static final int MAXFILENAMELENGTH = 20; //maximum length of the file
	public static final File IndexDirectoryPath = new File("p340354020data", "index"); 
	public static final File DocsDirectoryPath = new File("p340354020data", "docs"); 
	
	
	/**
	 * Checks if the given string is a valid file name. 
	 * @param name name of the file
	 * @return true if the name is valid, false otherwise 
	 */
	private static boolean validName(String name) { 
		//the name cannot be null or an empty string
		if (name == null || name.length()==0)
			return false; 
		
		char cc = name.charAt(0); 
		//the first character in the name must be a letter
		if (!Character.isLetter(cc) && cc != '_')
			return false; 
		
		boolean validSoFar = true; 
		//goes character by character in the name to check if each character is valid for a name
		for (int i=1; validSoFar && i < name.length(); i++) { 
			cc = name.charAt(i); 
			validSoFar = Character.isLetter(cc) || cc == '_' || 
					Character.isDigit(cc); 
		}
		return validSoFar; 
	}
	/**
	 * Checks if the given file name is valid
	 * @param name name of the file
	 * @return true if the name is valid, false otherwise
	 */
	private static boolean validFileName(String name) { 
		//name length must be shorter than 20 characters
		if (name.length() > MAXFILENAMELENGTH)
			return false; 
		return validName(name); 
	}
	
	/**
	 * Validates the name given for a document. If such name is valid, and also
	 * the file exists in the docs directory, then the corresponding File object
	 * is returned. If not, an exception is thrown. 
	 * @param fName the name of the document
	 * @return the File object that corresponds to the document's content. 
	 * @throws IllegalArgumentException if name is not valid or if file
	 * does not exist. 
	 */
	public static File validateDocumentFile(String fName) 
	throws IllegalArgumentException { 
		if (!validFileName(fName)) //checking if file has a valid name  
			throw new IllegalArgumentException("Invalid file name:" + fName); 
		File fPath = new File(DocsDirectoryPath, fName); //path of the file
		if (!fPath.exists())
			throw new IllegalArgumentException("No such file" + fPath.getAbsolutePath()); 

		return fPath; 
	}
	
	/**
	 * This method finds the index of the entry that is located inside the list given as parameter
	 * @param list list with entries
	 * @param e entry to search the index of
	 * @return the index of the entry
	 */
	public static <K,V> int findIndex(ArrayList<Entry<K,V>> list, Entry<K,V> e) { 
		for (int i=0; i<list.size(); i++)
			if (list.get(i).getKey().equals(e.getKey()))
				return i; 
		
		return -1; //returns -1 if the index for the entry is not found
	}
	
	/**
	 * This method writes a word into a random access file 
	 * @param word the word to be written
	 * @param file the file to be written on 
	 * @throws IOException input or output error with files
	 */
	public static void writeWordToFile(String word, RandomAccessFile file) 
			throws IOException 
	{
		for (int i=0; i<word.length(); i++)
			file.writeByte((byte) word.charAt(i)); 
		file.writeByte((byte) ' ');
	}

	/**
	 * This method writes an integer value into a random access file
	 * @param n the integer value
	 * @param file the file to be written on 
	 * @throws IOException input or output error with files
	 */
	public static void writeIntToFile(int n, RandomAccessFile file) throws IOException{
		file.writeInt(n);
	}
	
	/**
	 * Read next word from current file pointer in file. 
	 * @param file the random access file corresponding to the main index
	 * @return the next word
	 * @throws IOException input or output error with files 
	 */
	public static String readWord(RandomAccessFile file) throws IOException {
		String word = ""; 
		char ch = (char) file.readByte(); 
		for (int i=1; ch != ' '; i++) { 
			word += ch; 
			ch = (char) file.readByte(); 
		}
		return word; 
	}


}
