package Indexing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import Classes.Path;

public class PreProcessedCorpusReader {

	private BufferedReader reader;
	private FileInputStream inputStreamCollection;
    String line;
    String lineKey;
	
	public PreProcessedCorpusReader(String type) throws IOException {
		// This constructor opens the pre-processed corpus file, Path.ResultHM1 + type
		// You can use your own version, OR download from http://crystal.exp.sis.pitt.edu:8080/iris/resource.jsp.
		// Close the file when you do not use it any more
		inputStreamCollection = new FileInputStream(Path.ResultHM1 + type);
		reader = new BufferedReader(new InputStreamReader(inputStreamCollection)); 
	}
	

	public Map<String, String> NextDocument() throws IOException {
		// read a line for docNo, put into the map with <"DOCNO", docNo>
		// read another line for the content , put into the map with <"CONTENT", content>
		if ((line = reader.readLine()) != null){
        	Map<String, String> map = new HashMap<String, String>();
        	lineKey = line; // docno as key
        	line = reader.readLine(); // turn to next line as content
        	map.put("DOCNO", lineKey);// store to map 
        	map.put("CONTENT", line);// store to map 
        	//System.out.println(lineKey+line);
        	return map;
        }else{
        	reader.close(); // close file
        	inputStreamCollection.close();
        	return null;
        }
	}

}
