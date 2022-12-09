package com.example;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Proxy {

	public static List<String> server = new ArrayList<String>();

	private static void addServers() {
		server.add("us1");
		server.add("us2");
		server.add("us3");
		server.add("us4");
		server.add("us5");
		server.add("us6");
		server.add("us7");
		server.add("us8");
		server.add("us9");
		server.add("us10");
		server.add("us11");
		server.add("us12");
		server.add("us13");
		server.add("us14");
		server.add("us15");
		server.add("eu1");
		server.add("eu2");
		server.add("eu3");
		server.add("eu4");
		server.add("eu5");
		server.add("eu6");
		server.add("eu7");
		server.add("eu8");
		server.add("eu9");
		server.add("eu10");
	}

	public static HashMap<String,String> getProxyURL(String URL){
		//return getProxyURLServer1(URL);
		return getProxyURLServer2(URL);
	}

	public static HashMap<String, String> getProxyURLServer1(String URL) {



		if(server.size()==0)
			addServers();
		DataOutputStream wr = null;
		String server = getServer();
		System.out.println(server + "-Calling URL: " + URL );
		//server = "eu1";
		try {

			URL url = new URL("https://" + server + ".proxysite.com/includes/process.php?action=update");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
			con.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.9,ms;q=0.8");
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.109 Safari/537.36");


			String formData = "server-option=" + server + "&d"+"="+encode(URL)+"&allowCookies=on";
			//System.out.println(formData);
			byte[] postData = formData.getBytes(StandardCharsets.UTF_8);
			con.setFixedLengthStreamingMode(postData.length);
			con.setDoOutput(true);
			wr = new DataOutputStream(con.getOutputStream());
			con.connect();
			wr.write(postData);
			wr.flush();
			wr.close();
			/*System.out.println(con.getHeaderFields());

			System.out.println("Cookie:" + con.getHeaderField("Set-Cookie"));
			System.out.println("Proxy URL: " + con.getHeaderField("Location"));*/
			HashMap<String,String> proxyDetails = new HashMap<String,String>();

			proxyDetails.put("Cookie", con.getHeaderField("Set-Cookie"));
			proxyDetails.put("URL", con.getHeaderField("Location"));

			return proxyDetails;

		} catch(IOException exception) {
			exception.printStackTrace();
			ErrorHandler.printError("Proxy","Error in Proxy: " + server);
			return null;
		} finally {
			if(wr!=null)
				try {
					wr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}

	public static HashMap<String, String> getProxyURLServer2(String URL) {

		DataOutputStream wr = null;
		
		System.out.println("hidemyass" + "-Calling URL: " + URL );
		//server = "eu1";
		try {

			URL url = new URL("https://www.hidemyass-freeproxy.com/process/en-in");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
			con.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.9,ms;q=0.8");
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.109 Safari/537.36");

			String formData = "form[url]="+encode(URL)+"&form[dataCenter]=random&terms-agreed=1";
			//System.out.println(formData);
			byte[] postData = formData.getBytes(StandardCharsets.UTF_8);
			con.setFixedLengthStreamingMode(postData.length);
			con.setDoOutput(true);
			wr = new DataOutputStream(con.getOutputStream());
			con.connect();
			wr.write(postData);
			wr.flush();
			wr.close();
			/*System.out.println(con.getHeaderFields());

			System.out.println("Cookie:" + con.getHeaderField("Set-Cookie"));
			System.out.println("Proxy URL: " + con.getHeaderField("Location"));*/
			HashMap<String,String> proxyDetails = new HashMap<String,String>();

			proxyDetails.put("Cookie", con.getHeaderField("Set-Cookie"));
			proxyDetails.put("URL", "https://www.hidemyass-freeproxy.com" + con.getHeaderField("Location"));

			return proxyDetails;

		} catch(IOException exception) {
			exception.printStackTrace();
			ErrorHandler.printError("Proxy","Error in Proxy: " + server);
			return null;
		} finally {
			if(wr!=null)
				try {
					wr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}

	private static String getServer() {
		int i = ThreadLocalRandom.current().nextInt(0, server.size());
		return server.get(i);
	}

	private static String encode(String url)  
	{  
		try {  
			String encodeURL=URLEncoder.encode( url, "UTF-8" );  
			return encodeURL;  
		} catch (UnsupportedEncodingException e) {  
			e.printStackTrace();
			return null;  
		}  
	}

}
