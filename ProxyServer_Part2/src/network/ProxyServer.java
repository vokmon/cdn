package network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import listener.ProxyServerListener;

import com.sun.net.httpserver.HttpServer;

/**
 * A proxy server that will forward request to the server
 * 
 * @author Arnon  Ruangthanawes arua663
 */
public class ProxyServer {
	
	/**
	 * The default port of the server
	 */
	public static final String DEFAULT_PORT = "8001";
	
	/**
	 * The server
	 */
	private HttpServer server =  null;
	
	/**
	 * The user input port number
	 */
	private int portNumber;
	
	/**
	 * The {@link ProxyHandler}
	 */
	private ProxyHandler proxyHandler;
	
	
	/**
	 * Constructor
	 */
	public ProxyServer() {
		this.proxyHandler = new ProxyHandler();
	}

	/**
	 * Start up the server
	 * 
	 * @param portNumber of the server
	 * 
	 * @return string of server details
	 * @throws IOException occurs when the server cannot be started.
	 */
	public String startServer(int portNumber) throws IOException {
		this.portNumber = portNumber;
		this.server = HttpServer.create(new InetSocketAddress(portNumber), 0);
		this.server.createContext("/", this.proxyHandler);
		this.server.setExecutor(null); // creates a default executor
		this.server.start();
	    
	    InetAddress addr= InetAddress.getLocalHost();
	    String hostname = addr.getHostName();
	    
	    String url = String.format("http://%s:%s", hostname, String.valueOf(this.portNumber));
	    String serverDetails = String.format("Proxy server starts with url %s",url);
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
	 * Clear the cache
	 */
	public void clearCache() {
		this.proxyHandler.clearCache();
	}
	
	/**
	 * Get content from cache
	 * @param fileName the file name
	 * @return the content from the cache
	 */
	public String getContent(String fileName) {
		return this.proxyHandler.getContent(fileName);
	}
	
	/**
	 * Add listener to the handler
	 * 
	 * @param serverListener the listener of the server
	 */
	public void addListener(ProxyServerListener serverListener) {
		proxyHandler.addListener(serverListener);
    }
}
