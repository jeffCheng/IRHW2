package Indexing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import Classes.Path;


public class MyIndexReader {
	//Suggest you to write very efficient code here, otherwise, your memory cannot hold our corpus...
	String collectionIndexPath;
	String docIdPath;
	BufferedReader indexDocIdReader; // read indexDocNo
	Map<Integer, String> indexDocIdGroup ; // the map store id & docno
	BufferedReader indexReader; // read index
	String line;
	String lineIndex;
	int postinglist[][];
	
	public MyIndexReader( String type ) throws IOException {
		//read the index files you generated in task 1
		//remember to close them when you finish using them
		//use appropriate structure to store your index
		if(type.equals("trecweb")){
			collectionIndexPath = Path.IndexWebDir + "index";
			docIdPath = Path.IndexWebDir + "indexdocno";
		}else{
			collectionIndexPath = Path.IndexTextDir + "index";
			docIdPath = Path.IndexTextDir + "indexdocno";
		}
		//read docId & docNum index
		indexDocIdReader = new BufferedReader(new InputStreamReader(new FileInputStream(docIdPath)));
		indexDocIdGroup = new HashMap<Integer, String>();
		while ((line = indexDocIdReader.readLine()) != null) {
			String indexDocId[] = line.split(","); // split line by ","
			indexDocIdGroup.put(Integer.parseInt(indexDocId[0]), indexDocId[1]); // put into  map
		}
		indexDocIdReader.close(); // close reader
	}
	
	//get the non-negative integer dociId for the requested docNo
	//If the requested docno does not exist in the index, return -1
	public int GetDocid( String docno ) {
		for (Map.Entry<Integer, String> indexDocNo : indexDocIdGroup.entrySet()) {// get all
			if (indexDocNo.getValue() == docno) {
				return indexDocNo.getKey();
			}
		}
		return -1;
	}

	// Retrieve the docno for the integer docid
	public String GetDocno( int docid ) {
		String docno = indexDocIdGroup.get(docid);// return document number
		return docno;
	}
	
	/**
	 * Get the posting list for the requested token.
	 * 
	 * The posting list records the documents' docids the token appears and corresponding frequencies of the term, such as:
	 *  
	 *  [docid]		[freq]
	 *  1			3
	 *  5			7
	 *  9			1
	 *  13			9
	 * 
	 * ...
	 * 
	 * In the returned 2-dimension array, the first dimension is for each document, and the second dimension records the docid and frequency.
	 * 
	 * For example:
	 * array[0][0] records the docid of the first document the token appears.
	 * array[0][1] records the frequency of the token in the documents with docid = array[0][0]
	 * ...
	 * 
	 * NOTE that the returned posting list array should be ranked by docid from the smallest to the largest. 
	 * 
	 * @param token
	 * @return
	 */
	public int[][] GetPostingList( String token ) throws IOException {
		return postinglist;
	}

	// Return the number of documents that contains the token.
	public int GetDocFreq( String token ) throws IOException {
		indexReader = new BufferedReader(new InputStreamReader(new FileInputStream(collectionIndexPath)));
		int flag = 0; //contains terms and then read times 
		Map<Integer, Integer> posting = new HashMap<Integer, Integer>();
		while((lineIndex = indexReader.readLine()) != null){
			if (lineIndex.equals(token)) { 
				flag = 1;
			} else if (lineIndex.indexOf(",") == -1) {
				flag = 0;
			} else if (flag == 1) {
				String match_token[] = lineIndex.split(",");
				//docNum, frequency of term
				posting.put(Integer.parseInt(match_token[0]), Integer.parseInt(match_token[1]));
			}
		}
		int postinglistSize = posting.size();
		postinglist = new int[postinglistSize][2]; 
		Map<Integer, Integer> sortedMap = new TreeMap<Integer, Integer>(posting);
		if (postinglistSize != 0) {
			int i = 0;
			for (int key : sortedMap.keySet()) { 
				postinglist[i][0] = key;
				postinglist[i][1] = sortedMap.get(key);
				i++;
			}
			return postinglistSize;
		} else {
			return 0;
		}
	}
	
	// Return the total number of times the token appears in the collection.
	public long GetCollectionFreq( String token ) throws IOException {
		if (postinglist.length != 0) {
			int ctf = 0;
			for (int k = 0; k < postinglist.length; k++) {
				ctf = ctf + postinglist[k][1]; // sum of the frequency
			}
			return ctf;
		}
		return 0;
	}
	
	public void Close() throws IOException {
		indexReader.close();
	}
	
}