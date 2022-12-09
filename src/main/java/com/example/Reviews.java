package com.example;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Reviews implements Runnable {

	private String asin;

	public Reviews(String asin) {
		this.asin = asin;
	}

	public void run() {

		try {

			Thread.sleep(Helper.delay);

			Document doc = null;
			String url = "https://www.amazon.com/product-reviews/" + asin;
			if(Helper.proxy) {

				HashMap<String,String> proxyDetails = Proxy.getProxyURL(url);

				if(proxyDetails.get("URL")==null) {
					ErrorHandler.printError("(E)Reviews","Error getting url from proxy site");
					Helper.asin.get(asin).refreshReviews();
					return;
				}

				doc = Jsoup.connect(proxyDetails.get("URL"))
						.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
			             .maxBodySize(0)
			             .header("Cookie", proxyDetails.get("Cookie")).ignoreContentType(true).ignoreHttpErrors(true).timeout(10000).execute().parse();
			} else {
				doc = Jsoup.connect(url)
						.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
			             .maxBodySize(0)
			             .ignoreContentType(true).ignoreHttpErrors(true).timeout(10000).execute().parse();
			}
			if(doc ==null) {
				ErrorHandler.printError("(E)Reviews","Error getting response. response is null");
				Helper.asin.get(asin).refreshReviews();
				return;
			}

			Element customerReviews = doc.getElementById("filter-info-section");

			if(customerReviews == null) {
				String output = "none";
				if(doc.toString().contains("api-services-support@amazon.com")) {
					output = "AMAZON API ERROR";
				}
				else {
					output = "PROXY ERROR";
				}
				ErrorHandler.printError("(E)Reviews","Amazon blocking the site: " + output);
				Helper.asin.get(asin).refreshReviews();
				return;
			}

			if(customerReviews!=null) {
				if(customerReviews.text().contains("|")) {
					String reviews = customerReviews.text().trim().split(Pattern.quote(" "))[4];
					Helper.asin.get(asin).setReviews(Integer.parseInt(reviews));
				}else {
					String reviews = customerReviews.text().trim().split(Pattern.quote(" "))[3];
					Helper.asin.get(asin).setReviews(NumberFormat.getNumberInstance(Locale.US).parse(reviews).intValue());
					String ratings = customerReviews.text().trim().split(Pattern.quote(" "))[0];
					Helper.asin.get(asin).setRatings(NumberFormat.getNumberInstance(Locale.US).parse(ratings).intValue());
				}
				ErrorHandler.printError("(S)Reviews","success: " + asin);
			}

		}catch(Exception e) {
			if(!e.getMessage().contains("Read timed out"))
				e.printStackTrace();
			ErrorHandler.printError("(E)Reviews","Error: " +e.getMessage());
			Helper.asin.get(asin).refreshReviews();
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
