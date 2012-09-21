package com.passdock;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class Passdock {
	
	/**
	 * The private token to use in the API calls.
	 */
	private String token;
	
	/**
	 * The last status code received from the service.
	 */
	private int responseCode;
	
	/**
	 * The last status message received from the service.
	 */
	private String responseMessage;
	
	/**
	 * The base URI to use for the API calls.
	 */
	private String baseURI;
	
	
	private static String DEFAULT_BASE_URI = "https://api.passdock.com/api/v1";
	
	static private final int RESPONSE_UPDATED = 200;
	static private final int RESPONSE_UNAUTHORIZED = 402;
	static private final int RESPONSE_NOT_FOUND = 401;
	static private final int RESPONSE_INTERNAL_ERROR = 500;
	static private final int RESPONSE_DELETED = 200;
	static private final int RESPONSE_CREATED = 201;
	static private final int RESPONSE_FOUND = 200;
	
	/**
	 * Constructor
	 * @param token The private token the API.
	 * @param uri The base URI for the API.
	 */
	public Passdock(String token, String uri) {
		this.token = token;
		this.baseURI = uri;
	}
	
	/**
	 * Constructor that will use the default URI for the API.
	 * @param token The private token for the API.
	 */
	public Passdock(String token) {
		
		this(token, DEFAULT_BASE_URI);
	}
	
	
	/**
	 * This will retrieve all your templates as a JSON formatted string.
	 * @return The templates as a JSON string.
	 * @throws IOException 
	 */
	public String getTemplates() throws IOException {

		String retval = null; // The templates.

		// Prepare the GET request.
		HttpGet get = null;
		try {

			// Prepare the URI and add the 'api_token' parameter
			URI uri = new URI(this.baseURI + "/templates");
			URIBuilder builder = new URIBuilder(uri);
			builder.addParameter("api_token", this.token);

			get = new HttpGet(builder.build());

		} catch (Exception e) {

			// Malformed URI, return immediately.
			e.printStackTrace();
			return null;
		}

		// Execute the GET request
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(get);

		try {

			// Set return status code and message.
			this.responseCode = response.getStatusLine().getStatusCode();
			this.responseMessage = response.getStatusLine().getReasonPhrase();

			// If the templates have been found, get them from the response.
			if (responseCode == RESPONSE_FOUND) {
				
				HttpEntity entity = response.getEntity();
				retval = EntityUtils.toString(entity);
			}

		} catch (IOException ex) {

			// IOException, connection will be automatically closed.
			throw ex;

		} catch (RuntimeException rte) {

			// Unexpected exception, abort to release resources.
			get.abort();
			throw rte;

		}

		// HttpClient instance no longer needed, shutdown it.
		client.getConnectionManager().shutdown();

		return retval;
	}
	
	/**
	 * This will retrieve the pass as a JSON formatted string.
	 * @param passID The id of the pass.
	 * @param familyID The id of the template.
	 * @return The pass as a JSON string.
	 * @throws IOException 
	 */
	public String getTemplate(int familyID) throws IOException {

		String retval = null; // The template.

		// Prepare the GET request.
		HttpGet get = null;
		try {

			// Prepare the URI and add the 'api_token' parameter
			URI uri = new URI(this.baseURI + "/templates/" + familyID);
			URIBuilder builder = new URIBuilder(uri);
			builder.addParameter("api_token", this.token);

			get = new HttpGet(builder.build());

		} catch (Exception e) {

			// Malformed URI, return immediately.
			e.printStackTrace();
			return null;
		}

		// Execute the GET request
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(get);

		try {

			// Set return status code and message.
			this.responseCode = response.getStatusLine().getStatusCode();
			this.responseMessage = response.getStatusLine().getReasonPhrase();

			// If the template has been found, get it from the response.
			if (responseCode == RESPONSE_FOUND) {
				
				HttpEntity entity = response.getEntity();
				retval = EntityUtils.toString(entity);
			}

		} catch (IOException ex) {

			// IOException, connection will be automatically closed.
			throw ex;

		} catch (RuntimeException rte) {

			// Unexpected exception, abort to release resources.
			get.abort();
			throw rte;

		}

		// HttpClient instance no longer needed, shutdown it.
		client.getConnectionManager().shutdown();

		return retval;
	}
	
	/**
	 * This will destroy a template.
	 * @param familyID The family (template) to delete.
	 * @param errors
	 * @return If the template has been deleted or not.
	 * @throws IOException If something goes wrong.
	 */
	public boolean destroyTemplate(int familyID, boolean errors) throws IOException {

		boolean retval = false; // The pass

		// Execute the DELETE request.
		HttpDelete get = null;
		try {

			// Build up the request url and add the 'api_token'.
			URI uri = new URI(this.baseURI + "/templates/" + familyID);

			URIBuilder builder = new URIBuilder(uri);
			builder.addParameter("api_token", this.token);
			builder.addParameter("errors", Boolean.toString(errors));

			get = new HttpDelete(builder.build());

		} catch (Exception e) {

			// Malformed URI, return immediately.
			e.printStackTrace();
			return false;
		}

		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(get);

		try {

			// Find out if the template has been deleted or not.
			retval = (response.getStatusLine().getStatusCode() == RESPONSE_DELETED);

			// Set status code and message.
			this.responseCode = response.getStatusLine().getStatusCode();
			this.responseMessage = response.getStatusLine().getReasonPhrase();

			// Consume the entity.
			EntityUtils.consume(response.getEntity());

		} catch (IOException ex) {

			// IOException, connection will be automatically closed.
			throw ex;

		} catch (RuntimeException rte) {

			// Unexpected exception, abort to release resources.
			get.abort();
			throw rte;

		}

		// HttpClient instance no longer needed, shutdown it.
		client.getConnectionManager().shutdown();

		return retval;

	}
	
	/**
	 * This will retrieve the passes as a JSON formatted string.
	 * @param familyID The id of the template.
	 * @return The passes as a JSON string.
	 * @throws IOException 
	 */
	public String getPasses(int familyID) throws IOException {

		String retval = null; // The pass

		// Prepare the GET request.
		HttpGet get = null;
		try {

			// Prepare the URI and add the 'api_token' parameter
			URI uri = new URI(this.baseURI + "/templates/" + familyID + "/passes");
			URIBuilder builder = new URIBuilder(uri);
			builder.addParameter("api_token", this.token);

			get = new HttpGet(builder.build());

		} catch (Exception e) {

			// Malformed URI, return immediately.
			e.printStackTrace();
			return null;
		}

		// Execute the GET request
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(get);

		try {

			// Set return status code and message.
			this.responseCode = response.getStatusLine().getStatusCode();
			this.responseMessage = response.getStatusLine().getReasonPhrase();

			// If the passes have been found, get them from the response.
			if (responseCode == RESPONSE_FOUND) {
				
				HttpEntity entity = response.getEntity();
				retval = EntityUtils.toString(entity);
			}

		} catch (IOException ex) {

			// IOException, connection will be automatically closed.
			throw ex;

		} catch (RuntimeException rte) {

			// Unexpected exception, abort to release resources.
			get.abort();
			throw rte;

		}

		// HttpClient instance no longer needed, shutdown it.
		client.getConnectionManager().shutdown();

		return retval;
	}
	
	/**
	 * This will retrieve the pass as a JSON formatted string.
	 * @param passID The id of the pass.
	 * @param familyID The id of the template.
	 * @return The pass as a JSON string.
	 * @throws IOException 
	 */
	public String getPass(int passID, int familyID) throws IOException {

		String retval = null; // The pass

		// Prepare the GET request.
		HttpGet get = null;
		try {

			// Prepare the URI and add the 'api_token' parameter
			URI uri = new URI(this.baseURI + "/templates/" + familyID + "/passes/" + passID);
			URIBuilder builder = new URIBuilder(uri);
			builder.addParameter("api_token", this.token);

			get = new HttpGet(builder.build());

		} catch (Exception e) {

			// Malformed URI, return immediately.
			e.printStackTrace();
			return null;
		}

		// Execute the GET request
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(get);

		try {

			// Set return status code and message.
			this.responseCode = response.getStatusLine().getStatusCode();
			this.responseMessage = response.getStatusLine().getReasonPhrase();

			// If the pass has been found, get it from the response.
			if (responseCode == RESPONSE_FOUND) {
				
				HttpEntity entity = response.getEntity();
				retval = EntityUtils.toString(entity);
			}

		} catch (IOException ex) {

			// IOException, connection will be automatically closed.
			throw ex;

		} catch (RuntimeException rte) {

			// Unexpected exception, abort to release resources.
			get.abort();
			throw rte;

		}

		// HttpClient instance no longer needed, shutdown it.
		client.getConnectionManager().shutdown();

		return retval;
	}
	
	/**
	 * This will destroy a pass.
	 * @param passID The pass to destroy.
	 * @param familyID The family (template) of the pass.
	 * @param errors
	 * @return
	 * @throws IOException If something goes wrong.
	 */
	public boolean destroyPass(int passID, int familyID, boolean errors) throws IOException {

		boolean retval = false; // The pass

		// Execute the DELETE request.
		HttpDelete get = null;
		try {

			// Build up the request url and add the 'api_toke' 
			// parameter. Alternatively you can just use the simple 
			// string operator '+' and get the same result.
			URI uri = new URI(this.baseURI + "/templates/" + familyID + "/passes/" + passID);

			URIBuilder builder = new URIBuilder(uri);
			builder.addParameter("api_token", this.token);
			builder.addParameter("errors", Boolean.toString(errors));

			System.out.println(builder.build());

			get = new HttpDelete(builder.build());

		} catch (Exception e) {

			// Malformed URI, return immediately.
			e.printStackTrace();
			return false;
		}

		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(get);

		try {

			// Find out if the pass has been deleted or not.
			retval = (response.getStatusLine().getStatusCode() == RESPONSE_DELETED);

			// Set status code and message.
			this.responseCode = response.getStatusLine().getStatusCode();
			this.responseMessage = response.getStatusLine().getReasonPhrase();

			// Consume the entity.
			EntityUtils.consume(response.getEntity());

		} catch (IOException ex) {

			// IOException, connection will be automatically closed.
			throw ex;

		} catch (RuntimeException rte) {

			// Unexpected exception, abort to release resources.
			get.abort();
			throw rte;

		}

		// HttpClient instance no longer needed, shutdown it.
		client.getConnectionManager().shutdown();

		return retval;

	}
	
	/**
	 * This will update the pass.
	 * @param update The updates to apply to the pass as JSON string.
	 * @param passID The ID of the pass to update.
	 * @param familyID The ID of the template of the pass to update.
	 * @param debug
	 * @param errors
	 * @return The updated pass.
	 * @throws IOException If something goes wrong.
	 */
	public String updatePass(String update, int passID, int familyID, boolean debug, boolean errors) throws IOException {

		String retval = null; // The updated pass after a successful request

		// Prepare the PUT request.
		HttpPut put = null;
		try {

			// Prepare the uri.
			URI uri = new URI(this.baseURI + "/templates/" + familyID + "/passes/" + passID);// The URL of the REST request

			// Prepare the params.
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("api_token", this.token));
			params.add(new BasicNameValuePair("pass", update));
			UrlEncodedFormEntity paramsEntity = new UrlEncodedFormEntity(params, "UTF-8");
	
			put = new HttpPut(uri);
			put.setEntity(paramsEntity);
		
		} catch (Exception e) {
			
			// Malformed uri.
			e.printStackTrace();	
			return null;
		}
				
		// Execute the PUT request.
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(put); 

		try {
			
			// Set status code and message.
			this.responseCode = response.getStatusLine().getStatusCode();
			this.responseMessage = response.getStatusLine().getReasonPhrase();

			if (responseCode == RESPONSE_UPDATED) {

				// Get the updated object JSON.
				HttpEntity responseEntity = response.getEntity();
				retval = EntityUtils.toString(responseEntity);
			}

		} catch (IOException ex) {

			// IOException, connection will be automatically closed.
			throw ex;

		} catch (RuntimeException rte) {

			// Unexpected exception, abort to release resources.
			put.abort();
			throw rte;

		}

		// HttpClient instance no longer needed, shutdown it.
		client.getConnectionManager().shutdown();

		return retval;
	}
	
	
	/**
	 * This will make a pass CREATE request to the Passdock API.
	 * @param pass A JSON string with the details of the pass.
	 * @param familyID The ID of the template to use.
	 * @param debug 
	 * @param errors
	 * @return A JSON representation of the pass on success, null otherwise.
	 * @throws IOException If something has gone wrong.
	 */
	public boolean createPass (String pass, int familyID, boolean debug, boolean errors) throws IOException {

		boolean retval = false; // The updated pass after a successful request

		// Prepare POST request.
		HttpPost post = null;
		
		try {

			// Prepare the uri.
			URI uri = new URI(this.baseURI + "/templates/" + familyID + "/passes");

			// Prepare the params.
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("api_token", this.token));
			params.add(new BasicNameValuePair("pass", pass));
			if(debug)
				params.add(new BasicNameValuePair("debug",Boolean.toString(debug)));
			if(errors)
				params.add(new BasicNameValuePair("errors",Boolean.toString(errors)));
			UrlEncodedFormEntity paramsEntity = new UrlEncodedFormEntity(params, "UTF-8");

			post = new HttpPost(uri);
			post.setEntity(paramsEntity);

		} catch (Exception e) {

			// Malformed uri.
			e.printStackTrace();
			return false;
		}

		// Execute the POST request.
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(post);
		
		try {

			// Set status code and message.
			this.responseCode = response.getStatusLine().getStatusCode();
			this.responseMessage = response.getStatusLine().getReasonPhrase();

			// Find out if the pass has been created or not.
			retval = (responseCode == RESPONSE_CREATED);

			// Consume the entity.
			EntityUtils.consume(response.getEntity());

		} catch (IOException ex) {

			// IOException, connection will be automatically closed.
			throw ex;

		} catch (RuntimeException rte) {

			// Unexpected exception, abort to release resources.
			post.abort();
			throw rte;

		}

		// HttpClient instance no longer needed, shutdown it.
		client.getConnectionManager().shutdown();

		return retval;
	}
	
	
	/**
	 * Base URI getter.
	 * @return The API base URI.
	 */
	public String baseURI() {
		return this.baseURI;
	}
	
	/**
	 * Base URI setter.
	 * @param baseURI The API base URI to use.
	 */
	public void setBaseURI(String baseURI) {
		this.baseURI = baseURI;
	}

	/**
	 * Response code getter.
	 * @return The last response status code.
	 */
	public int getResponseCode() {
		return responseCode;
	}

	/**
	 * Response message getter.
	 * @return The last response status message.
	 */
	public String getResponseMessage() {
		return responseMessage;
	}
	
	
}
