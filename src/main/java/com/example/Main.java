/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.sql.DataSource;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@SuppressWarnings("deprecation")
@RestController
@SpringBootApplication
public class Main {

	@Value("${spring.datasource.url}")
	private String dbUrl;

	@Autowired
	private DataSource dataSource;
	
	private static DecimalFormat df2 = new DecimalFormat("0.##");

	private ScheduledTask taskScheduler;
	private static Timer refresher;

	public static void main(String[] args) throws Exception {
		SpringApplication application = new SpringApplication(Main.class);
		Helper.sellerCentral.initialize();
		
		Properties properties = new Properties();
		properties.put("spring.servlet.multipart.max-file-size", "30MB");
		properties.put("spring.servlet.multipart.max-request-size", "35MB");
		application.setDefaultProperties(properties);
		
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
            public void checkClientTrusted(X509Certificate[] certs, String authType) { }
            public void checkServerTrusted(X509Certificate[] certs, String authType) { }

        } };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) { return true; }
        };
        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

		application.run(args);

		if(!Helper.isLocal) {
			Jsoup.connect("https://pure-basin-71429.herokuapp.com/start").ignoreContentType(true).ignoreHttpErrors(true).timeout(10000).execute();
			Mailer.sendMail("App started", "App Started", "ma.manoj@gmail.com");
			Mailer.sendMail("App started", "App Started", "ramshan2008@gmail.com");
		}
		else {
			Jsoup.connect("http://localhost:5000/start").ignoreContentType(true).ignoreHttpErrors(true).timeout(10000).execute();
		}
	}

	@PostMapping("/upload")
	String uploadToLocalFileSystem(@RequestParam("file") MultipartFile file) {

		try {
			@SuppressWarnings("resource")
			java.util.Scanner scanner = new java.util.Scanner(file.getInputStream(),"UTF-8").useDelimiter("/A");
			String theString = scanner.hasNext() ? scanner.next() : "";
			Importer imp = new Importer(theString);
			return imp.updateToDatabase() + " orders inserted";
		} catch (IOException e) {
			e.printStackTrace();
			return "upload error " + e.getMessage();
		}

	}

	@RequestMapping("/start")
	String start() {
		initialize();
		long delay =0;
		for (Map.Entry<String, ASIN> entry : Helper.asin.entrySet()) {
			entry.getValue().startMonitor(delay);
			delay = delay + 45000;
		}
		taskScheduler = new ScheduledTask();
		refresher = new Timer();
		refresher.schedule(taskScheduler , 0, Helper.delay);
		return "started";
	}

	public void initialize() {

		if(Helper.initialized)
			return;

		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM amazonasin ORDER BY title ASC");
			while (rs.next()) {
				System.out.println("LOADING ASIN: " + rs.getString("asin"));
				if(!Helper.asin.containsKey(rs.getString("asin"))) {
					if(Helper.isLocal && Helper.testAsin.compareToIgnoreCase(rs.getString("asin"))==0) {
						Helper.asin.put(rs.getString("asin"), new ASIN(rs.getString("asin")));
					} else if(Helper.isLocal==false){
						Helper.asin.put(rs.getString("asin"), new ASIN(rs.getString("asin")));
					}
				}
			}
			List<String> list = new ArrayList<>(Helper.asin.keySet());
			Collections.shuffle(list);
			HashMap<String, ASIN> shuffleMap = new LinkedHashMap<>();
			list.forEach(k->shuffleMap.put(k, Helper.asin.get(k)));
			if(Helper.isLocal)
			{
				shuffleMap.clear();
				shuffleMap.put(Helper.testAsin, Helper.asin.get(Helper.testAsin));
				//shuffleMap.put("B07JP4XQ4J", Helper.asin.get("B07JP4XQ4J"));
			}
			Helper.asin = shuffleMap;
			Helper.initialized = true;
		}
		catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(connection!=null)
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}

	public class ScheduledTask extends TimerTask {
		public void run() {
			try {
				@SuppressWarnings({ "resource" })
				HttpClient client = Helper.getAllAcceptCertsClient();
				String url = "https://pure-basin-71429.herokuapp.com/getStatus";
				HttpGet request = new HttpGet(url);
				request.addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
				request.addHeader("accept-encoding", "chunked");
				request.addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
				request.addHeader("accept-language", "en-US,en;q=0.9,ms;q=0.8");
				request.addHeader("cache-control", "no-cache");
				request.addHeader("pragma", "no-cache");
				request.addHeader("upgrade-insecure-requests", "1");
				
				HttpResponse response = client.execute(request);
				response.getStatusLine();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	@RequestMapping("/asinValidator")
	String asinValidator() {
		return "index";
	}

	@RequestMapping(value = "/addASIN/{asin}", method = RequestMethod.POST, produces = "application/json")
	String addAsin(@PathVariable String asin) {

		Helper.asin.put(asin, new ASIN(asin));
		return getCurrentValues().toString();
	}
	
	@RequestMapping(value = "/getOrders/{sku}/{startdate}/{enddate}", method = RequestMethod.GET, produces = "application/json")
	String getOrders(@PathVariable String sku,@PathVariable String startdate,@PathVariable String enddate) {

		String asin = getAsinFromSKU(sku);
		if(asin==null)
			return "[]";
		
		return Helper.asin.get(asin).getOrders(startdate,enddate);
				
	}

	@RequestMapping(value = "/toggleMail/{asin}", method = RequestMethod.POST, produces = "application/json")
	String toggleMail(@PathVariable String asin) {

		Helper.asin.get(asin).toggleMail();
		return getCurrentValues().toString();
	}

	@RequestMapping(value = "/updateASIN", method = RequestMethod.PUT, produces = "application/json")
	String updateAsin(@RequestBody String body) {
		try {
			JSONObject asinDetails = new JSONObject(body);
			if(asinDetails.getString("asin").compareTo("all")==0) {
				for (Map.Entry<String, ASIN> entry : Helper.asin.entrySet()) {
					entry.getValue().update(asinDetails.getString("inputkey"), asinDetails.getString("inputvalue").replaceAll("^\"|\"$", ""));
				}
				return getCurrentValues().toString();
			}
			else if(asinDetails.getString("asin").contains(",")) {
				String[] asins = asinDetails.getString("asin").split(Pattern.quote(","));
				for(int i=0;i<asins.length;i++) {
					Helper.asin.get(asins[i].toString()).update(asinDetails.getString("inputkey"), asinDetails.getString("inputvalue").replaceAll("^\"|\"$", ""));
				}
				return getCurrentValues().toString();
			}
			else if(asinDetails.getString("inputkey").compareToIgnoreCase("newreviews")==0) {
				Helper.asin.get(asinDetails.getString("asin")).resetReviewCounter();
				return getCurrentValues().toString();
			}

			Helper.asin.get(asinDetails.getString("asin")).update(asinDetails.getString("inputkey"), asinDetails.getString("inputvalue").replaceAll("^\"|\"$", ""));
			return getCurrentValues().toString();
		}catch(Exception e) {e.printStackTrace();return null;}
	}

	@RequestMapping(value = "/updateASINDetails", method = RequestMethod.PUT, produces = "application/json")
	String updateAsinDetails(@RequestBody String body) {

		try {
			JSONObject asinDetails = new JSONObject(body);
			Helper.asin.get(asinDetails.getString("asin")).update("title", asinDetails.getString("title"));
			Helper.asin.get(asinDetails.getString("asin")).update("expectedlength", asinDetails.getString("expectedlength"));
			Helper.asin.get(asinDetails.getString("asin")).update("expectedwidth", asinDetails.getString("expectedwidth"));
			Helper.asin.get(asinDetails.getString("asin")).update("expectedheight", asinDetails.getString("expectedheight"));
			Helper.asin.get(asinDetails.getString("asin")).update("expectedweight", asinDetails.getString("expectedweight"));
			Helper.asin.get(asinDetails.getString("asin")).update("shipping", asinDetails.getString("shipping"));
			Helper.asin.get(asinDetails.getString("asin")).update("sellingprice", asinDetails.getString("sellingprice"));
			Helper.asin.get(asinDetails.getString("asin")).update("productcost", asinDetails.getString("productcost"));
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		return getCurrentValues().toString();

	}
	
	@RequestMapping(value = "/updateUBC", method = RequestMethod.PUT, produces = "application/json")
	String updateUBC(@RequestBody String body) {

		try {
			JSONObject asinDetails = new JSONObject(body);
			Helper.asin.get(asinDetails.getString("asin")).update("ubc", asinDetails.getString("ubc"));
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		return getCurrentValues().toString();

	}

	@RequestMapping(value = "/deleteASIN/{asin}", method = RequestMethod.DELETE, produces = "application/json")
	String deleteAsin(@PathVariable String asin) {
		try {
			Helper.asin.get(asin).delete();
			Helper.asin.remove(asin);
			return getCurrentValues().toString();
		}catch(Exception e) {e.printStackTrace();return null;}
	}
	
	@RequestMapping(value = "/setotp/{otp}", method = RequestMethod.PUT, produces = "application/json")
	String setotp(@PathVariable String otp) {
		try {
			String output = Helper.sellerCentral.setOTP(otp);
			return output;
		}catch(Exception e) {e.printStackTrace();return e.getMessage();}
	}
	
	@RequestMapping(value = "/setverifiedEmail", method = RequestMethod.PUT, produces = "application/json")
	Boolean setverifiedEmail() {
		try {
			return Helper.sellerCentral.checkLoggedIn();
		}catch(Exception e) {e.printStackTrace();return false;}
	}
	
	@RequestMapping(value = "/login/{username}/{password}", method = RequestMethod.PUT, produces = "application/json")
	String setlogin(@PathVariable String username, @PathVariable String password) {
		try {
			String output = Helper.sellerCentral.login(username,password);
			return output;
		}catch(Exception e) {e.printStackTrace();return e.getMessage();}
	}
	
	@RequestMapping(value = "/updateUnits/{asin}/{units}", method = RequestMethod.PUT, produces = "application/json")
	Boolean updateIncomingUnits(@PathVariable String asin,@PathVariable String units) {
		try {
			Helper.asin.get(asin).update("incomingunits",units);
			return true;
		}catch(Exception e) {e.printStackTrace();return false;}
	}


	@RequestMapping(value = "/getStatus", method = RequestMethod.GET, produces = "application/json")
	String getStatus() {
		try {
			initialize();
			return getCurrentValues().toString();
		}catch(Exception e) {e.printStackTrace();return null;}
	}
	/*
	@RequestMapping(value = "/refresh", method = RequestMethod.GET, produces = "application/json")
	String refresh() {
		try {
		for (Map.Entry<String, ASIN> entry : Helper.asin.entrySet()) {
		    entry.getValue().refreshFromAmazon();
		}
		return getCurrentValues().toString();
		}catch(Exception e) {e.printStackTrace();return null;}
	}
	 */


	private JSONObject getCurrentValues() {

		JSONObject data = new JSONObject();
		try {
			JSONArray values = new JSONArray();
			Double incomingVolume = 0D;
			Double availableVolume = 0D;
			Double totalProfit = 0D;
			int asinCountForProfit = 0;
			List<JSONObject> asinObjectsActive = new ArrayList<JSONObject>();
			List<JSONObject> asinObjectsInactive = new ArrayList<JSONObject>();
			for (Map.Entry<String, ASIN> entry : Helper.asin.entrySet()) {
				if(entry.getValue().lastpulled==null)
					continue;
				if(entry.getValue().active)
					asinObjectsActive.add(entry.getValue().getDetails());
				else
					asinObjectsInactive.add(entry.getValue().getDetails());
				incomingVolume = incomingVolume + Double.parseDouble(entry.getValue().incomingvolume);
				availableVolume = availableVolume + Double.parseDouble(entry.getValue().availablevolume);
				Double profit = Double.parseDouble(entry.getValue().profitpercentage);
				if(profit>-5) {
					totalProfit = totalProfit + profit;
					asinCountForProfit++;
				}
			}
			asinObjectsActive.sort(new SKUSorter());
			asinObjectsInactive.sort(new SKUSorter());
			for(int i=0;i<asinObjectsActive.size();i++) {
				values.put(asinObjectsActive.get(i));
			}
			for(int i=0;i<asinObjectsInactive.size();i++) {
				values.put(asinObjectsInactive.get(i));
			}
			data.put("data", values);
			data.put("totalincomingvolume", df2.format(incomingVolume)+"");
			data.put("totalavailablevolume", df2.format(availableVolume)+"");
			if(asinCountForProfit>0)
				data.put("totalprofitPercentage", df2.format(totalProfit/asinCountForProfit)+"");
			else
				data.put("totalprofitPercentage", "0");
		}catch(Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	public class SKUSorter implements Comparator<JSONObject> {
		@Override
		public int compare(JSONObject x, JSONObject y) {

			return stringCompare(x.get("title").toString(),y.get("title").toString());

		}

		public int stringCompare(String str1, String str2) 
		{ 

			int l1 = str1.length(); 
			int l2 = str2.length(); 

			for (int i = 0; i < l1 && i < l2; i++) { 
				int str1_ch = (int)str1.charAt(i); 
				int str2_ch = (int)str2.charAt(i); 

				if (str1_ch == str2_ch) { 
					continue; 
				} 
				else { 
					return str1_ch - str2_ch; 
				} 
			} 

			if (l1 < l2) { 
				return l1 - l2; 
			} 
			else if (l1 > l2) { 
				return l1 - l2; 
			} 

			else { 
				return 0; 
			} 
		} 

	}
	
	private String getAsinFromSKU(String sku) {
		String asin = null;
		Connection connection = null;
		try { 
			connection = Helper.dataSource.getConnection();
			PreparedStatement stmt = connection.prepareStatement("SELECT asin from amazonasin where title = ?");
			stmt.setString(1, sku);
			
			ResultSet rs = stmt.executeQuery();
			
			
			while(rs.next()) {
				asin = rs.getString(1);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(connection!=null)
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return asin;
	}


	@Bean
	public DataSource dataSource() throws SQLException {
		try {
			if (Helper.isLocal) {
				HikariConfig config = new HikariConfig();
				config.setJdbcUrl("jdbc:postgresql://ec2-3-231-253-230.compute-1.amazonaws.com:5432/deis3do1da01lk?sslmode=require");
				config.setUsername("urhonccktzcrrl");
				config.setPassword("5706b60e0b1703ba312d453ee862ab3bb24090a12ee4b4b6442e672b4e37e775");
				config.setDriverClassName("org.postgresql.Driver");
				//config.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");
				Helper.dataSource = new HikariDataSource(config);
				return Helper.dataSource;
			} else {
				HikariConfig config = new HikariConfig();
				config.setJdbcUrl(dbUrl);
				//config.setDriverClassName("org.postgresql.ds.PGSimpleDataSource");
				Helper.dataSource = new HikariDataSource(config);
				return Helper.dataSource;
			}
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
