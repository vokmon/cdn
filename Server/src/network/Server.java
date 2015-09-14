package network;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import listener.ServerListener;

import com.sun.net.httpserver.HttpServer;


/**
 * A server program that provides lists the names of the files available on the server and 
 * download the files
 * 
 * @author Arnon  Ruangthanawes arua663
 */
public class Server {
	
	/**
	 * The default port of the server
	 */
	public static final String DEFAULT_PORT = "8000";
	
	/**
	 * The url for listing available files
	 */
	private final String LIST_FILE_URL = "/info";
	
	/**
	 * The url for getting a file
	 */
	private final String GET_FILE_URL = "/getfile";
	
	/**
	 * The server
	 */
	private HttpServer server =  null;
	
	/**
	 * The user input port number
	 */
	private int portNumber;
	
	/**
	 * The file path for the server
	 */
	private String path;
	
	/**
	 * The {@link InfoHandler}
	 */
	private InfoHandler infoHander;
	
	/**
	 * The {@link GetFileHandler}
	 */
	private GetFileHandler getFileHandler;
	
	public Server() {
		infoHander = new InfoHandler();
		getFileHandler = new GetFileHandler();
	}

	/**
	 * Start up the server
	 * 
	 * @param portNumber of the server
	 * @param path the directory for available files
	 * 
	 * @return string of server details
	 * @throws IOException occurs when the server cannot be started.
	 */
	public String startServer(int portNumber, String path) throws IOException {
		this.portNumber = portNumber;
		this.path = path;
		this.server = HttpServer.create(new InetSocketAddress(portNumber), 0);
		this.infoHander.setPath(this.path);
		this.getFileHandler.setPath(this.path);
		this.server.createContext(LIST_FILE_URL, infoHander);
		this.server.createContext(GET_FILE_URL, getFileHandler);
		this.server.setExecutor(null); // creates a default executor
		this.server.start();
	    
	    InetAddress addr= InetAddress.getLocalHost();
	    String hostname = addr.getHostName();
	    
	    String url1 = String.format("http://%s:%s%s", hostname, String.valueOf(this.portNumber), LIST_FILE_URL);
	    String url2 = String.format("http://%s:%s%s", hostname, String.valueOf(this.portNumber), GET_FILE_URL);
	    String serverDetails = String.format("Server starts.\nURL for listing file: %s\nURL for getting file: %s",url1, url2);
	    return serverDetails;
	}

	/**
	 * Stop the server
	 */
	public void stopServer() {
		if (server != null) {
			server.stop(0);
		}
	}
	
	/**
	 * Add listener to the handler
	 * 
	 * @param serverListener the listener of the server
	 */
	public void addListener(ServerListener serverListener) {
		infoHander.addListener(serverListener);
		getFileHandler.addListener(serverListener);
    }
}
