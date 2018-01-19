package Indexing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import Classes.Path;

public class MyIndexWriter {
	//Suggest you to write very efficient code here, otherwise, your memory cannot hold our corpus...
	String filePath ="";
	Map<Integer, String> docNumIdGroup = new HashMap<Integer, String>(); // set int num on docId 
	Map<String, HashMap<Integer, Integer>> collectionIndex  = new HashMap<String, HashMap<Integer, Integer>>(); // index structure
	int docNum = 1;
	int tempNum = 1;
    BufferedWriter docIdWriter; 
    BufferedWriter collectionIndexWriter; 
	BufferedReader tempIndexReader;// read for temporary index file
	
	public MyIndexWriter(String type) throws IOException {
		// This constructor should initiate the FileWriter to output your index files
		// remember to close files if you finish writing the index
		//store the index by type 
		if(type.equals("trecweb")){
			//File indexWebDir = new File(Path.IndexWebDir);
			File indexWebDir = new File(Path.IndexWebDir);
			indexWebDir.mkdir(); // create new folder
			filePath = Path.IndexWebDir; // set path for web or text
		}else{
			File indexTextDir = new File(Path.IndexTextDir);
			indexTextDir.mkdir();
			filePath = Path.IndexTextDir;	
		}
		// set writer path
		docIdWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath + "indexDocNo")));
		collectionIndexWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath + "index")));
	}
	
	public void IndexADocument(String docno, String content) throws IOException {
		// you are strongly suggested to build the index by installments
		// you need to assign the new non-negative integer docId to each document, which will be used in MyIndexReader
		docNumIdGroup.put(docNum, docno); // assign the new non-negative integer docId to each document
		String[] words = content.split(" "); //split each content 
		for(String word : words){
			if (!collectionIndex.containsKey(word)) { 
           	 HashMap<Integer, Integer> subIndex = new HashMap<Integer, Integer>(); // create new map to store sub index 
             subIndex.put(docNum, 1);  
             collectionIndex.put(word, subIndex);  // put to the main collection index
            } else {  
           	 HashMap<Integer, Integer> subIndex = collectionIndex.get(word); // if exists
           	 if (subIndex.containsKey(docNum)) {  // check subIndex
                    subIndex.put(docNum, subIndex.get(docNum)+1);  
                } else {  
                    subIndex.put(docNum, 1);  
                }  
            }
		}
		docNum++; // next document
		if (docNum % 30000 == 0){ 
        	for (int key:docNumIdGroup.keySet()){ // write docID to docID file.
        		docIdWriter.write(key +","+ docNumIdGroup.get(key) +"\n");
        	}
        	//create a temp file to store collection index
     		BufferedWriter tempWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath +"temp_index"+tempNum)));
     		for (String term:collectionIndex.keySet()){  
     			tempWriter.write(term +"\n");
     			HashMap<Integer, Integer> subIndex = collectionIndex.get(term);
     			for (int termDocNum:subIndex.keySet()){
     				tempWriter.write(termDocNum +","+ subIndex.get(termDocNum) +"\n");
     			}
        	}
     		tempNum++;
     		docNumIdGroup.clear(); //clear() because ID wrote to the file.
     		collectionIndex.clear();//clear() because ID wrote to the file.
     		tempWriter.close();
         }
	}
	
	public void Close() throws IOException {
		// close the index writer, and you should output all the buffered content (if any).
		// if you write your index into several files, you need to fuse them here.
		
		// write the rest of docId
		for (int key:docNumIdGroup.keySet()){ 
			docIdWriter.write(key +","+ docNumIdGroup.get(key) +"\n");
    	}
		// write the rest of index
 		BufferedWriter tempWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath +"temp_index"+tempNum)));
 		
 		for (String term:collectionIndex.keySet()){  
 			tempWriter.write(term +"\n");
 			HashMap<Integer, Integer> subIndex = collectionIndex.get(term);
 			for (int termDocNum:subIndex.keySet()){
 				tempWriter.write(termDocNum +","+ subIndex.get(termDocNum) +"\n");
 			}
    	}
 		docNumIdGroup.clear(); 
 		collectionIndex.clear();
 		tempWriter.close();
	 	//fuse the temporary index file to one file
 		for (int a=1; a<=tempNum;a++){
 			// get the temporary index file
 			tempIndexReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath+"temp_index"+a)));
 			String line;
 			while ((line = tempIndexReader.readLine()) != null){
 				collectionIndexWriter.write(line+"\n");
 			}
 			tempIndexReader.close(); 
			File file = new File(filePath +"temp_index"+a); 
			file.delete(); // delete the temporary index file
 		}
 		docIdWriter.close(); 
 		collectionIndexWriter.close();
	}
}
