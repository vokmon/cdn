package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import exception.ServerConnectionException;

/**
 * The client class for connecting to server
 * 
 * @author Arnon  Ruangthanawes
 */
public class Client {
	
	/**
	 * The default url of the server that provides the service.
	 */
    public static final String DEFAULT_SERVER_URL = "http://localhost:8000";
    
    /**
     * The default url of the proxy server that caches the requested files
     */
    public static final String DEFAULT_PROXY_URL = "http://localhost:8001";
    
    /**
     * The context path for getting the list of available files
     */
    private final String LIST_FILE_URL = "info";
    
    /**
     * The context path for requesting a file
     */
    private final String GET_FILE_URL = "getfile";
    
    /**
     * The parameter name for getting a file
     */
    private final String PARAM_NAME_REQUEST_FILE = "file";
    
    /**
     * The http header for cache control type
     */
    private final String CONTROL_TYPE_CACHE = "Cache-Control";
    
    /**
     * The http header for application
     */
    private final String APPLICATION = "application";
    
    /**
     * The control cache value for cache
     */
    private final String CONTROL_CACHE_VALUE_CACHE = "cache";
    
    /**
     * The control cache value for no-cache
     */
    private final String CONTROL_CACHE_VALUE_NO_CACHE = "no_cache";
    
    /**
     * The application url encoded
     */
    private final String APPLICATION_URL_ENCODED = "x-www-form-urlencoded";
    
    /**
     * The http header for redirected location
     */
    private final String LOCATION = "Location";
    
    /**
     * The actual server url
     */
    private String serverUrl = DEFAULT_SERVER_URL;
    
    /**
     * The actual proxy url
     */
    private String proxyUrl = DEFAULT_PROXY_URL;

    /**
     * Request a file by file name
     * 
     * @param fileName The file name
     * @return the content of requested file
     * 
     * @throws IOException when the content of the file cannot be retrieved.
     * @throws ServerConnectionException when an error occurs while connecting to the server
     */
    public String getFile(String fileName) throws IOException, ServerConnectionException {
        URL url = new URL(this.proxyUrl);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty(CONTROL_TYPE_CACHE, CONTROL_CACHE_VALUE_CACHE);
        con.setRequestProperty(APPLICATION, APPLICATION_URL_ENCODED);
        fileName = fileName.replace(" ", "%20");
        String serverUrlString = String.format("%s/%s?%s=%s", this.serverUrl, GET_FILE_URL, this.PARAM_NAME_REQUEST_FILE, fileName);
        con.setRequestProperty("Location", serverUrlString);
        String content = this.sendRequest(con);
        return content;
    }

    /**
     * Get the list of available files from the server
     * 
     * @return list of available files from the server
     * @throws IOException when the content of the file cannot be retrieved.
     * @throws ServerConnectionException when an error occurs while connecting to the server
     */
    @SuppressWarnings("unchecked")
	public List<String> getAvailableFiles() throws IOException, ServerConnectionException {
        URL url = new URL(this.proxyUrl);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty(CONTROL_TYPE_CACHE, CONTROL_CACHE_VALUE_NO_CACHE);
        con.setRequestProperty(LOCATION, String.valueOf(this.serverUrl) + "/" + LIST_FILE_URL);
        String content = this.sendRequest(con);
        ObjectMapper mapper = new ObjectMapper();
        List<String> fileList = ( List<String>)mapper.readValue(content, new TypeReference<List<String>>(){});
        return fileList;
    }

    /**
     * Forward the request to the proved server
     * 
     * @param con the {@link HttpURLConnection}
     * 
     * @return the response content from the server 
     * @throws IOException when the content of the file cannot be retrieved.
     * @throws ServerConnectionException when an error occurs while connecting to the server
     */
    private String sendRequest(HttpURLConnection con) throws ServerConnectionException, IOException {
        try {
            int respondCode = con.getResponseCode();
            if (respondCode != 200) {
                String content = this.getResponseContent(con.getErrorStream());
                throw new ServerConnectionException(content);
            }
        }
        catch (ServerConnectionException e) {
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new ServerConnectionException(String.format("Cannot connect to the proxy server %s", this.proxyUrl), (Throwable)e);
        }
        String content = this.getResponseContent(con.getInputStream());
        return content;
    }

    /**
     * The helper method for reading the content from {@link InputStream} and converting to string
     * 
     * @param inputStream {@link InputStream} from the response
     * @return the content of the response
     * @throws IOException when the content of the response cannot be retrieved.
     */
    private String getResponseContent(InputStream inputStream) throws IOException {
        String inputLine;
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine).append(System.lineSeparator());
        }
        in.close();
        return response.toString();
    }

    public String getServerUrl() {
        return this.serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getProxyUrl() {
        return this.proxyUrl;
    }

    public void setProxyUrl(String proxyUrl) {
        this.proxyUrl = proxyUrl;
    }
}