package network;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import listener.ServerListener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


/**
 * The handler class for listing available files
 * 
 * @author Arnon  Ruangthanawes arua663
 *
 */
public class InfoHandler implements HttpHandler {

	/**
	 * The directory for listing files
	 */
	private String path;
	
	/**
	 * The initial directory
	 */
	private File initDirectory;
	
	/**
	 * The listeners
	 */
	private List<ServerListener> listeners = new ArrayList<ServerListener>();
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		List<String> list = listf(this.path);
		
		final StringWriter sw =new StringWriter();
	    final ObjectMapper mapper = new ObjectMapper();
	    mapper.writeValue(sw, list);

		String response = sw.toString();
		Headers responseHeaders = exchange.getResponseHeaders();
	    responseHeaders.set("Content-Type", "application/json");
	    responseHeaders.set("Cache-Control", "no-cache");
	      
		exchange.sendResponseHeaders(200, response.length());
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
		
		for(ServerListener listener : this.listeners) {
			listener.requestFileList(list);
		}
	}

	/**
	 * List all files name in the directory and sub directory
	 * 
	 * @param directoryName the name of the directory
	 * @return all files name in the directory and sub directory
	 */
	public List<String> listf(String directoryName) {
        File directory = new File(directoryName);

        List<String> resultList = new ArrayList<String>();

        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
            	try{
            		String path = file.getPath();
                	path = path.replace(this.initDirectory.getPath(), "");
                	path = path.replaceAll("\\\\", "/");
                	resultList.add(path);
            	}
            	catch(Exception e) {
            		e.printStackTrace();
            	}
            	
            } else if (file.isDirectory()) {
                resultList.addAll(listf(file.getAbsolutePath()));
            }
        }
        return resultList;
    } 
	
	/**
	 * Add listener to the handler
	 * @param toAdd 
	 */
	public void addListener(ServerListener toAdd) {
        listeners.add(toAdd);
    }

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
		initDirectory = new File(this.path);
	}
}
