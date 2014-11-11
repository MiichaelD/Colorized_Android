package com.webs.itmexicali.colorized.util;
//http://stackoverflow.com/questions/2793150/how-to-use-java-net-urlconnection-to-fire-and-handle-http-requests

import java.util.Scanner;
import java.net.*;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import android.util.Log;

public class ServerConn{

	//
	public static final int metNONE = 0, metGET = 1, metPOST = 2;

	public static final String charset = "UTF-8";

	//default connection timeout - 2 secs
	public static int defConTimeout = 2000;


	public static void main(String args[]){
		Scanner in=new Scanner(System.in);
		System.out.print("Write a message to the server: ");
		String url="http://mexwks1067/lasertag/reg_event.php";
		try{
			String query = String.format("param1=%s", URLEncoder.encode(in.nextLine(), charset));
			System.out.println("Server's GET response: " +getResponse(Connect(metGET,url,query)));
			System.out.println("Server's POST response: "+getResponse(Connect(metPOST,url,query)));
		}catch (Exception e){			System.out.println(e.getMessage());	}
		finally{in.close();}

	}



	/** create a new connection
	 * @params int mMethod:	0 for no query, 1 for metod GET, 2 for metod POST
	 * @params string mUrl:	string containing the url
	 * @params string query:	string containing the query
	 * @return an HttpURLConnection or null if there is no network available	 */	
	public static HttpURLConnection Connect(int mMethod, String mUrl, String query)throws Exception{

		if ( !mUrl.startsWith("http://"))
			mUrl = "http://" + mUrl;
			
		Log.v("srvConn","Opening Connection("+mMethod+") to url: "+mUrl+"?"+query);
			
		
		//query = URLEncoder.encode(query,"UTF-8");
		
		HttpURLConnection conn = null;
		//set the type of request
		switch(mMethod){
		case metGET:
			if (query != null)
				conn = (HttpURLConnection) new URL(mUrl+"?"+query).openConnection();
			else
				conn = (HttpURLConnection) new URL(mUrl).openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(defConTimeout);
			break;
		case metPOST:
			conn = (HttpURLConnection) new URL(mUrl).openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true); // this set POST method
			conn.setConnectTimeout(defConTimeout);
			OutputStream output=null;
			try{//write the query
				output = conn.getOutputStream();
				output.write(query.getBytes(charset));
				output.close();
			}catch(IOException ex){	System.out.println(ex.getMessage());}
			break;
		default:
			conn = (HttpURLConnection) new URL(mUrl).openConnection();
			conn.setUseCaches(true);
			conn.setConnectTimeout(defConTimeout);
			break;
		}
		return conn;
	}
	

	/**Get a response from a httpURLconnection as String
	 * @param conn an opened connection
	 * @return the servers response or null if no connection exists
	 * @throws Exception	 */
	public static String getResponse(HttpURLConnection conn)throws Exception{
		if ( conn == null )
			return null;
		
		StringBuffer result = new StringBuffer("");
		Scanner reader;	
		//get the response and append it 
		reader = new Scanner(conn.getInputStream());
		while (reader.hasNextLine()) {
			result.append(reader.nextLine());
		}
		reader.close();
		conn.disconnect();
		conn = null;
		
		return result.toString();
	}


	/**Connect to a server by its URL using GET method, obviously the URL
	 * should have the necessary query parameters included.
	 * @param mUrl servers URL
	 * @return a string containing the servers response or null if no network is available
	 * @throws Exception
	 */
	public static String getResponse(String mUrl) throws Exception	{
		
		HttpURLConnection conn;
		Scanner rd;
		StringBuilder response=new StringBuilder();
		try {
			conn = (HttpURLConnection) new URL(mUrl).openConnection();
			conn.setRequestMethod("GET");
			rd = new Scanner(conn.getInputStream());
			while (rd.hasNextLine()) {
				response.append(rd.nextLine());
			}
			rd.close();
		} catch (Exception e) {
			throw e;
		}
		return response.toString();
	}



	public static void printConnProps(HttpURLConnection conn)throws IOException{
		System.out.println("method: "+conn.getRequestMethod());
		System.out.println("response code: "+conn.getResponseCode());
		System.out.println("response Message: "+conn.getResponseMessage());
		System.out.println("content type: "+conn.getContentType());
		System.out.println("content length: "+conn.getContentLength());
		System.out.println("content: "+(String)conn.getContent().toString());
		System.out.println("header field: "+conn.getHeaderFields());
		System.out.println("Url: "+conn.getURL());
		System.out.println("connection: "+(String)conn.toString());
		System.out.println("\n");
	}


/**
 * This method is for practice and test only, it has no real functionality
 * @param methodType
 * @param Info
 * @return
 */
	@SuppressWarnings("unused")
	private static String ApacheREST(int methodType, String Info){

		HttpClient httpClient = new DefaultHttpClient();

		switch(methodType){
		case 0:
			HttpPost post = new HttpPost("http://10.0.2.2:2731/Api/Clientes/Cliente");
			post.setHeader("content-type", "application/json");
			try{
				JSONObject dato = new JSONObject();//build request JSON
				dato.put("info", Info);
				StringEntity entity = new StringEntity(dato.toString());
				post.setEntity(entity);// we add it to the post request
				HttpResponse resp = httpClient.execute(post);//get server response
				String respStr = EntityUtils.toString(resp.getEntity());
				return respStr;
			}
			catch(Exception ex){Log.e("ServicioRest","Error!", ex);	}
			break;
		case 1:
			HttpPut put = new HttpPut("http://10.0.2.2:2731/Api/Clientes/Cliente");
			put.setHeader("content-type", "application/json");
			try{
				//Construimos el objeto cliente en formato JSON
				JSONObject dato = new JSONObject();

				dato.put("Info", Info);
				StringEntity entity = new StringEntity(dato.toString());
				put.setEntity(entity);

				HttpResponse resp = httpClient.execute(put);
				String respStr = EntityUtils.toString(resp.getEntity());
				return respStr;
			}
			catch(Exception ex){Log.e("ServicioRest","Error!", ex);	}
			break;
		case 2:
			 
			HttpDelete del = new HttpDelete("http://10.0.2.2:2731/Api/Clientes/Cliente/12");
			del.setHeader("content-type", "application/json");
			try{
			   HttpResponse resp = httpClient.execute(del);
			   String respStr = EntityUtils.toString(resp.getEntity());
			   return respStr;
			}
			catch(Exception ex){Log.e("ServicioRest","Error!", ex);	}
			break;
			
		case 3:
			HttpGet get = new HttpGet("http://10.0.2.2:2731/Api/Clientes/Cliente/15");
		 	get.setHeader("content-type", "application/json");		 
		 	try{
		        HttpResponse resp = httpClient.execute(get);
		        String respStr = EntityUtils.toString(resp.getEntity());		 
		        JSONObject respJSON = new JSONObject(respStr);
		        int idCli = respJSON.getInt("Id");
		        String nombCli = respJSON.getString("Nombre");
		        int telefCli = respJSON.getInt("Telefono");
		 	}
			catch(Exception ex){   Log.e("ServicioRest","Error!", ex);	}
			break;
		}
		return null;
	}
}