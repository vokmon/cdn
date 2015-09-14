package helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The helper class for requesting file from the server.
 * 
 * @author Arnon  Ruangthanawes arua663
 *
 */
public class CacheManager {

	/**
	 * The cache file table <file name, full path of the file to keep the cache>
	 */
	private Map<String, String> cacheFiles = new HashMap<String, String>();
	
	public String getFileFromCache(String fileName) {
		return this.cacheFiles.get(fileName);
	}
	
	public void storeFileToCache(String fileName, String content) {
		this.cacheFiles.put(fileName, content);
	}
	
	/**
	 * Clear the cache
	 */
	public void clearCache() {
		this.cacheFiles.clear();
		
	}
	
	/**
	 * Return list of file name in the cache
	 * @return list of file name in the cache
	 */
	public List<String> getListOfCacheFiles() {
		List<String> fileList = new ArrayList<String>(cacheFiles.keySet());
		Collections.sort(fileList);
		return fileList;
	}
	
}
