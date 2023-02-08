package com.example;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.sql.DataSource;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;

public class Helper {
	
	public static Boolean initialized = false;
	public static DataSource dataSource;
	public static HashMap<String,ASIN> asin = new HashMap<String,ASIN>();
	public static boolean proxy = true;
	/*public static ExecutorService sellerCentralExecutor = Executors.newFixedThreadPool(1);
	public static ExecutorService buyBoxExecutor = Executors.newFixedThreadPool(1);
	public static ExecutorService fbaExecutor = Executors.newFixedThreadPool(1);
	public static ExecutorService asinActiveExecutor = Executors.newFixedThreadPool(1);*/
	public static ExecutorService executor;
	public static long delay = 15000;
	public static SellerCentralCall3 sellerCentral = new SellerCentralCall3();
	//public static long seconddelay = 10000;
	public static boolean isLocal = false;
	public static String testAsin = "B0BNHW2VV5";
	static {
		executor = Executors.newFixedThreadPool(1);
		isLocal = false;
		if(isLocal) {
			delay = 60003;
			proxy = false;
			//seconddelay = 0;
		}
	}
	

	public static HttpClient getAllAcceptCertsClient() {
		try {
		SSLContext context = SSLContext.getInstance("TLSv1.2");
		TrustManager[] trustManager = new TrustManager[] {
		    new X509TrustManager() {
		       public X509Certificate[] getAcceptedIssuers() {
		           return new X509Certificate[0];
		       }
		       public void checkClientTrusted(X509Certificate[] certificate, String str) {}
		       public void checkServerTrusted(X509Certificate[] certificate, String str) {}
		    }
		};
		context.init(null, trustManager, new SecureRandom());

		SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(context,
		        SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		
		HttpClient client = HttpClientBuilder.create().setSSLSocketFactory(socketFactory).build();
		
		return client;
		}catch(Exception e) {return null;}
		
	}
	
}
