package network;

import helper.CacheManager;
import helper.ProxyHelper;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import listener.ProxyServerListener;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * The handler class for proxy server
 * 
 * @author Arnon  Ruangthanawes
 */
public class ProxyHandler implements HttpHandler {

	/**
	 * The Control type header
	 */
	private final String CONTROL_TYPE_CACHE = "Cache-Control";
	
	/**
	 * The value of Control type; cache
	 */
	private final String CONTROL_CACHE_VALUE_CACHE = "cache";
	
	/**
	 * The Location of the origin server
	 */
	private final String LOCATION = "Location";
	
	/**
	 * The {@link ProxyHelper}
	 */
	private ProxyHelper proxyHelper;
	
	/**
	 * The {@link CacheManager}
	 */
	private CacheManager cacheManager;
	
	/**
	 * The listeners
	 */
	private List<ProxyServerListener> listeners = new ArrayList<ProxyServerListener>();
	
	/**
	 * The reqeust string for getting a file.
	 */
	private String PARAM_NAME_REQUEST_FILE = "file";
	
	/**
	 * Constructor
	 */
	public ProxyHandler() {
		proxyHelper = new ProxyHelper();
		cacheManager = new CacheManager();
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		Headers requestHeader = exchange.getRequestHeaders();
		String response = null;
		
		String serverLocation = requestHeader.getFirst(LOCATION);
		URL url = new URL(serverLocation);
		
		String fileName = null;
		String query = url.getQuery();
		if (query != null && !query.isEmpty()) {
			Map<String, String> params = queryToMap(query);
			fileName = params.get(this.PARAM_NAME_REQUEST_FILE);
			if (fileName != null && !fileName.isEmpty()) {
				for(ProxyServerListener listener : this.listeners) {
					listener.requestFile(fileName, new Date());
				}
			}
		}
		/* If there is no cache, get new file from the server */
		boolean noCache = false;
		if (!this.cacheManager.isFileInCache(fileName)) {
			noCache = true;
		}
		
		int httpCode = 200;
		/* Forward request to the server */
		
		Map<String, Object> responseFromServer = this.proxyHelper.forwardRequestToServer(url, noCache);
		httpCode = this.proxyHelper.getCode(responseFromServer);
		boolean isFileChange = httpCode != 304;
		response = this.proxyHelper.getResponse(responseFromServer);
		HttpURLConnection con = this.proxyHelper.getConnection(responseFromServer);
		
		if ((httpCode==200 && con.getHeaderField(this.CONTROL_TYPE_CACHE).equals(this.CONTROL_CACHE_VALUE_CACHE)) || httpCode==304) {
			double percentageFromCache = this.cacheManager.storeFileToCache(fileName, response, isFileChange);
			response = this.cacheManager.getFileFromCache(fileName);
			for(ProxyServerListener listener : this.listeners) {
				listener.percentageFromCache(percentageFromCache, fileName);
				listener.cacheUpdate(this.cacheManager.getListOfCacheFiles());
			}
			httpCode = 200;
		}
		
		exchange.sendResponseHeaders(httpCode, response.length());
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
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
	public void addListener(ProxyServerListener toAdd) {
        listeners.add(toAdd);
    }
	/**
	 * Clear the cache
	 */
	public void clearCache() {
		this.cacheManager.clearCache();
		for(ProxyServerListener listener : this.listeners) {
			listener.clearCache();
		}
	}
	
	/**
	 * Get content from cache
	 * @param fileName the file name
	 * @return the content from the cache
	 */
	public String getContent(String fileName) {
		return this.cacheManager.getFileFromCache(fileName);
	}
}
