package com.example;

import java.util.HashMap;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class RatingsAndStatus implements Runnable {

	private String asin;

	public RatingsAndStatus(String asin) {
		this.asin = asin;
	}

	public void run() {

		try {

			Thread.sleep(Helper.delay);
			
			String json3 = Helper.sellerCentral.getPrice(Helper.asin.get(asin).title);
			Thread.sleep(3000);
			
			if(json3!=null) {
				
				//System.out.println("Got value: " + json3);
				JSONObject obj = new JSONObject(json3);
				JSONObject asd = new JSONObject(obj.get("result").toString());
				JSONObject asinDetails = asd.getJSONObject("GetPricingData");
				Double price = asinDetails.getJSONObject("price").getDouble("currencyValue");
				Helper.asin.get(asin).update("sellingprice",price+"");
				ErrorHandler.printError("(S)Price","price success : " + asin);
				
			}else {
			ErrorHandler.printError("(E)Price","Response is null");
			}
			
			Boolean status = Helper.sellerCentral.isActive(asin);
			Thread.sleep(3000);
			if(status!=null) {
				Helper.asin.get(asin).updateActive(status);
				ErrorHandler.printError("(S)Ratings and Status", "Status success: " + asin);
			}else {
				ErrorHandler.printError("(E)Status","Response is null");
			}
			//now settin buy box to true always
			Helper.asin.get(asin).updateBuyBox(true);
/*
			Document doc = null;

			String url = "https://www.amazon.com/dp/" + asin;

			if(Helper.proxy) {

				HashMap<String,String> proxyDetails = Proxy.getProxyURL(url);

				if(proxyDetails.get("URL")==null) {
					ErrorHandler.printError("(E)Ratings and Status","Error getting url from proxy site");
					Helper.asin.get(asin).refreshRatingsAndStatus();
					return;
				}

				doc = Jsoup.connect(proxyDetails.get("URL"))
						.header("Cookie", proxyDetails.get("Cookie"))
						.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
			             .maxBodySize(0)
			             .ignoreContentType(true).ignoreHttpErrors(true).timeout(10000).execute().parse();	
			} else {

				doc = Jsoup.connect(url).ignoreContentType(true)
						.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
			             .maxBodySize(0)
			             .ignoreHttpErrors(true).timeout(10000).execute().parse();

			}

			if(doc ==null) {
				ErrorHandler.printError("(E)Ratings and Status","Error getting data. Response is null");
				Helper.asin.get(asin).refreshRatingsAndStatus();
				return;
			}
			String output = "none";
			if(doc.toString().contains("api-services-support@amazon.com")) {
				output = "AMAZON API ERROR";
				ErrorHandler.printError("(E)Ratings and Status","Amazon blocking the site: " + output);
				Helper.asin.get(asin).refreshRatingsAndStatus();
				return;
			}
			
			/*
			Element merchantInfo = doc.getElementById("merchant-info");
			if(merchantInfo ==null) {
				merchantInfo = doc.getElementById("sellerProfileTriggerId");
				output = "NEW MERCHANT INFO SELECTION";
			}
			if(merchantInfo==null) {
				ErrorHandler.printError("(E)Ratings and Status","Amazon blocking the site: " + output);
				Helper.asin.get(asin).refreshRatingsAndStatus();
				return;
			}
			if(merchantInfo.getElementsByTag("a").size()==0) {
				Helper.asin.get(asin).updateActive(false);
				return;
			}
			*/
			/*
			Element customerRatings = doc.getElementById("acrCustomerReviewText");
			if(customerRatings!=null) {
				String ratings = customerRatings.text().split(Pattern.quote(" rating"))[0];
				ratings = ratings.replace(",", "");
				Helper.asin.get(asin).setRatings(Integer.parseInt(ratings));
			}	

			//String seller = merchantInfo.getElementsByTag("a").get(0).text();
			*/
			
			
		}catch(Exception e) {
			if(!e.getMessage().contains("Read timed out") && !e.getMessage().contains("recv failed"))
				e.printStackTrace();
			ErrorHandler.printError("(E)Ratings and Status", "Error :" + e.getMessage() );
			Helper.asin.get(asin).refreshRatingsAndStatus();
		}

	}
	/*
	@Override
	public void run() {

		try {

			Thread.sleep(Helper.delay);

			runAgain();

			Helper.asin.get(asin).lastUpdated = System.currentTimeMillis();

		}catch(Exception e) {
			e.printStackTrace();
			//runAgain();
		}

	}
	 */
}
