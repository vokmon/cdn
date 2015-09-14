package listener;

import java.util.List;

/**
 * The listener that is fired when the server processes a request
 * 
 * @author Arnon  Ruangthanawes arua663
 *
 */
public interface ServerListener {
	
	/**
	 * The action that is fired when listing available  file on the server
	 * 
	 * @param fileList the list of the available files on the server
	 */
	void requestFileList(List<String> fileList);
	
	/**
	 * The action that is fired when a file is requested
	 * 
	 * @param filePath the file path
	 */
	void requestFile(String filePath);
}
