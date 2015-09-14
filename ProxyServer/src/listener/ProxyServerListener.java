package listener;

import java.util.Date;
import java.util.List;

/**
 * The listener that is fired when the proxy server processes a request
 * 
 * @author Arnon  Ruangthanawes arua663
 *
 */
public interface ProxyServerListener {

	/**
	 * The action that is fired when a file is requested
	 * 
	 * @param filePath the file path
	 * @param date the time that the user request the file
	 */
	void requestFile(String filePath, Date date);
	
	/**
	 * The action that is fired when the file is responsed from cache.
	 * @param fileName the requested file name
	 */
	void responseFromCache(String fileName);
	
	/**
	 * The action that is fired when the file is downloaded form server.
	 * @param fileName the requested file name
	 */
	void responseFromServer(String fileName);

	/**
	 * The action that is fired when the list of cached is updated.
	 * @param listOfCacheFiles the new list of files
	 */
	void cacheUpdate(List<String> listOfCacheFiles);
	
	/**
	 * The action that is fired when the cache is clear.
	 */
	void clearCache();
}
