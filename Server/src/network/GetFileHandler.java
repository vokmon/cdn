package network;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import listener.ServerListener;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * 
 * @author Arnon  Ruangthanawes arua663
 */
public class GetFileHandler implements HttpHandler {

	/**
	 * The directory for listing files
	 */
	private String path;

	/**
	 * The reqeust string for getting a file.
	 */
	private String PARAM_NAME_REQUEST_FILE = "file";
	
	/**
	 * The delimeter
	 */
	private String DELIMETER = File.separator;
	
	/**
	 * The listeners
	 */
	private List<ServerListener> listeners = new ArrayList<ServerListener>();

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String st = exchange.getRequestURI().getQuery();
		Map<String, String> params = queryToMap(st);
		
		String fileName = params.get(this.PARAM_NAME_REQUEST_FILE);
		String filePath = path + DELIMETER + fileName;
		
		File file = new File(filePath);
		
		if (!file.exists()) {
			String response = "File "+fileName+ " not found.";
			Headers responseHeaders = exchange.getResponseHeaders();
		    responseHeaders.set("Cache-Control", "no-cache");
			exchange.sendResponseHeaders(404, response.length());
			OutputStream os = exchange.getResponseBody();
			os.write(response.getBytes());
			os.close();
		} else {
			Headers responseHeaders = exchange.getResponseHeaders();
		    responseHeaders.set("Cache-Control", "cache");
			exchange.sendResponseHeaders(200, file.length());       
		    OutputStream os = exchange.getResponseBody();
		    FileInputStream fs = new FileInputStream(file);
		    final byte[] buffer = new byte[1024];
		    int count = 0;
		    while ((count = fs.read(buffer)) >= 0) {
		        os.write(buffer, 0, count);
		    }
		    os.flush();
		    fs.close();
		    os.close();
		}
	    
		
		for(ServerListener listener : this.listeners) {
			listener.requestFile(fileName);
		}
	}

	/**
	 * Returns the url parameters in a map
	 * 
	 * @param query the query string
	 * @return map of query string and value
	 */
	public Map<String, String> queryToMap(String query) {
		Map<String, String> result = new HashMap<String, String>();
		for (String param : query.split("&")) {
			String pair[] = param.split("=");
			if (pair.length > 1) {
				result.put(pair[0], pair[1]);
			} else {
				result.put(pair[0], "");
			}
		}
		return result;
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
	}
}
