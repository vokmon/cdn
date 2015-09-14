package helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * The helper class for requesting file from the server.
 * 
 * @author Arnon  Ruangthanawes arua663
 *
 */
public class ProxyHelper {

	/**
	 * The Control type header
	 */
	private final String CONTROL_TYPE_CACHE = "Cache-Control";
	
	/**
	 * The value of Control type; no-cache
	 */
	private final String CONTROL_CACHE_VALUE_NO_CACHE = "no-cache";
	
	/**
	 * Forward to request to the origin server
	 * 
	 * @param url the url of the server to forward to
	 * @return The result string coming back from the server
	 * @throws IOException occurs when the proxy cannot forward the request to the server
	 */
	public Map<String, Object> forwardRequestToServer(URL url, boolean noCache) throws IOException {
		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");
		if (noCache) {
			con.setRequestProperty(CONTROL_TYPE_CACHE, CONTROL_CACHE_VALUE_NO_CACHE);
		}
		
		try {
			int respondCode = con.getResponseCode();
			if (respondCode == 304) {
				return this.constructResponse(respondCode,"", con);
			}
			if (respondCode == 404) {
				return this.constructResponse(respondCode, 
						String.format("404 file not found from http://%s:%s/%s", 
								url.getHost(), 
								String.valueOf(url.getPort()), 
								url.getPath()), con);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return this.constructResponse(444, 
					String.format("Cannot connect to the server %s:%s", 
							url.getHost(), 
							String.valueOf(url.getPort())), con);
		}
		
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine).append(System.getProperty("line.separator"));
		}
		in.close();
		
		return constructResponse(200, response.toString(), con);
	}
	
	/**
	 * Create the response object with code, and the response from the server
	 * 
	 * @param code the http code
	 * @param response the response from the server
	 * @param con the {@link HttpURLConnection}
	 * @return the response object with code, and the response from the server
	 */
	private Map<String, Object> constructResponse(int code, String response, HttpURLConnection con) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", code);
		map.put("response", response);
		map.put("connection", con);
		return map;
	}
	
	/**
	 * Get http code from the response map
	 * @param map the reponse map
	 * @return http code from the response map
	 */
	public int getCode(Map<String, Object> map) {
		return (int)map.get("code");
	}
	
	/**
	 * Get response body
	 * @param map the response map
	 * @return response body
	 */
	public String getResponse(Map<String, Object> map) {
		return (String)map.get("response");
	}
	
	/**
	 * Get the {@link HttpURLConnection}
	 * @param map the response map
	 * @return the {@link HttpURLConnection}
	 */
	public HttpURLConnection getConnection(Map<String, Object> map) {
		return (HttpURLConnection)map.get("connection");
	}
}
