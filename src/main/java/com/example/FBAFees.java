package com.example;

import java.util.HashMap;
import java.util.Random;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class FBAFees implements Runnable{
	private String asin;
	Random rand = new Random(); //instance of random class
    int upperbound = 90000;

	public FBAFees(String asin) {
		
		this.asin = asin;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(Helper.delay);
			//if(!Helper.isLocal)
			//	Thread.sleep(rand.nextInt(upperbound)+Helper.delay);
			/*
			String url = "https://sellercentral.amazon.com/fba/profitabilitycalculator/getafnfee?profitcalcToken=0";
			
			JSONObject jsonBody = new JSONObject();
			jsonBody.put("productInfoMapping", new JSONObject(Helper.asin.get(asin).dimensionjson));
			jsonBody.put("afnPriceStr", 0);
			jsonBody.put("currency", "USD");
			jsonBody.put("marketPlaceId", "ATVPDKIKX0DER");
			jsonBody.put("mfnPriceStr", Double.parseDouble(Helper.asin.get(asin).sellingprice));
			String shipping = Helper.asin.get(asin).shipping;
			if(shipping==null||shipping.isEmpty()) shipping = "0";
			jsonBody.put("mfnShippingPriceStr", Double.parseDouble(shipping));
			
			Document doc1 = null;

			if(Helper.proxy) {
				HashMap<String,String> proxyDetails = Proxy.getProxyURL(url);

				if(proxyDetails.get("URL")==null) {
					ErrorHandler.printError("(E)Dimensions","Error getting url from proxy site");
					Helper.asin.get(asin).refreshFBAfees();
					return;
				}
				
				doc1 = Jsoup.connect(proxyDetails.get("URL"))
						.header("Cookie", proxyDetails.get("Cookie"))
						.userAgent("Chrome")
						.header("Accept", "*")
						.header("Cache-Control", "no-cache")
						.header("Host", "sellercentral.amazon.com")
						.header("Accept-Encoding", "zip, deflate")
						.header("Content-Type", "application/json")
						.ignoreContentType(true)
						.method(Method.POST)
						.requestBody(jsonBody.toString())
						.ignoreHttpErrors(true).timeout(10000).execute().parse();	
			}else {
				doc1 = Jsoup.connect(url).userAgent("Chrome")
						.header("Accept", "*")
						.header("Cache-Control", "no-cache")
						.header("Host", "sellercentral.amazon.com")
						.header("Accept-Encoding", "zip, deflate")
						.header("Content-Type", "application/json")
						.ignoreContentType(true)
						.method(Method.POST)
						.requestBody(jsonBody.toString())
						.ignoreHttpErrors(true).timeout(10000).execute().parse();	
			}

			if(doc1 ==null) {
				ErrorHandler.printError("(E)FBA","Error getting response. response is null");
				Helper.asin.get(asin).refreshFBAfees();
				return;
			}
			
			String output = doc1.getElementsByTag("body").text();

			if(!output.startsWith("{")) {
				output = doc1.toString();
				if(output.contains("processedDate")) {
					output = output.split(Pattern.quote("<body>"))[1].split(Pattern.quote("</body>"))[0].substring(1);
				}
			}
			
			if(!output.startsWith("{")) {
				if(output.toString().contains("Unsupported action")) {
					ErrorHandler.printError("(E)FBA","Output is not json : "+ "amazon unsopported action");
				} else {
					ErrorHandler.printError("(E)FBA","Output is not json : "+ output);
				}
				Helper.asin.get(asin).refreshFBAfees();
				return;
			}
			
			ErrorHandler.printError("(S)FBA","fba success : " + asin);
*/
			
			String output = Helper.sellerCentral.getFBAFees(Helper.asin.get(asin).asin,Helper.asin.get(asin).fbafee,Helper.asin.get(asin).title);
			Thread.sleep(3000);
			
			if(output==null) {
				ErrorHandler.printError("(E)FBA","Response is null");
				Helper.asin.get(asin).refreshFBAfees();
			}
				
			//System.out.println("Got value for fbafees: " + output);
			ErrorHandler.printError("(S)FBA","seller central success : " + asin);

			
			Helper.asin.get(asin).updateFBAFees(output);
			Thread.sleep(3000);
			
			}catch(Exception e) {
				if(!e.getMessage().contains("Read timed out"))
					e.printStackTrace();
				ErrorHandler.printError("(E)FBA","Error: " + e.getMessage());
				Helper.asin.get(asin).refreshFBAfees();
			}
		
	}

}
