package mt.nlp.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;


public class FileManager {

	public static  BufferedReader get_file_content(String filename) throws FileNotFoundException {
		BufferedReader config = (filename.startsWith("jar:")
				? new BufferedReader(
						new InputStreamReader(FileManager.class.getResourceAsStream(filename.substring(4))))
				: new BufferedReader(new FileReader(filename)));
		return config;
	}

}
