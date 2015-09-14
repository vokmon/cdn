package function;

import java.util.ArrayList;
import java.util.List;

/**
 * The Rabin function implementation for chossing boundary of the block
 * 
 * @author Arnon  Ruangthanawes arua663
 */
public class RabinHashFunction {

	private int windowSize;
	private int modular;
	private int prime;
	private int minBlockSize;
	private int maxBlockSize;
	
	/**
	 * The contructor
	 * 
	 * @param windowSize the window size of continuous byte to include
	 * @param modular the modulus M for reducing the data block
	 * @param prime the prime number
	 * @param minBlockSize the minimum block size
	 * @param maxBlockSize the maximum block size
	 */
	public RabinHashFunction(int windowSize, int modular, int prime, int minBlockSize, int maxBlockSize) {
		this.windowSize = windowSize;
		this.modular = modular;
		this.prime = prime;
		this.minBlockSize = minBlockSize;
		this.maxBlockSize = maxBlockSize;
	}

	/**
	 * Divide the content into data blocks
	 * 
	 * @param content the content to divide
	 * @return data blocks
	 */
	public List<String> createChunk(String content) {
		List<String> resultList = new ArrayList<String>();
		int start = 0;
        int end = windowSize;
        byte[] bytes = content.getBytes();
        
        int firstIndex = 0;
        int lastIndex = 0;
        while(end <= bytes.length) {
        	long result = 0;
        	for(int x = 0; x<windowSize; x++) {
        		Byte b = bytes[start+x];
        		long a = (long)(b.intValue() * Math.pow(prime, (double)(windowSize - (x +1))));
        		result = result + a;
        	}
        	result = result%modular;
        	if (result==0) {
        		lastIndex = end;
        		insertChunk(content.substring(firstIndex, lastIndex), resultList);
        		firstIndex = lastIndex;
    		}
        	start++;
        	end++;
        }
        if (lastIndex < content.length()) {
        	insertChunk(content.substring(firstIndex), resultList);
        }
        
        return resultList;
	}
	
	/**
	 * Update the datablock based on the max and min block size.
	 * @param content the content to add to the block
	 * @param resultList data blocks
	 */
	private void insertChunk(String content, List<String> resultList) {
		String temp = "";
		if (resultList != null && !resultList.isEmpty()) {
			temp = resultList.get(resultList.size()-1);
		}
		
		String temp2 = temp + content;
		String s1 = "";
		String s2 = "";
		int maxSize = maxBlockSize-1;
		if (temp2.length() > maxBlockSize) {
			s1 = temp;
			s2 = content;
		}
		else {
			if (maxSize > temp2.length()) {
				maxSize = temp2.length();
			}
			s1 = temp2;
			s2 = "";
		}
		
		if (resultList != null && !resultList.isEmpty()) {
			resultList.set(resultList.size()-1, s1);
		}else {
			resultList.add(s1);
		}
		
		if (s2.length() >0) {
			resultList.add(s2);
		}
	}
}
