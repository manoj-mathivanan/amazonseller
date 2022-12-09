package com.example;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Test {

	public static void main(String[] args) throws Exception {
		
		//System.out.println(Mailer.encode("hello manoj"));
		//System.out.println("hello manoj".replaceAll(Pattern.quote(" "), "%20"));
		//Mailer.sendMail("subject to ram new", "message content to ram", "ramshan2008@gmail.com");
		//Mailer.sendMail("Test-Amazon monitor", "Check ASIN " + "1234","ma.manoj@gmail.com");
		//buytest();
		//testfba();
		//reviewsTest();
		//test1();
		//runAgain();
		//dateTest();
		//decodeImage();
		//testlogin();

		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH);
		System.out.println(month);
	}
	
	private static void testlogin(){
		Helper.sellerCentral.initialize();
		Helper.sellerCentral.login("ma.manoj@gmail.com", "amazon1!");
		String otp = "123";
		Helper.sellerCentral.setOTP(otp);
		Helper.sellerCentral.getFBAFees("B07JXY9GC1", "7.7","");
		System.out.println("done");
	}
	
	private static void decodeImage() throws Exception {
		String s = "base64image";
		byte[] decodedBytes = Base64.getDecoder().decode(s);
		FileUtils.writeByteArrayToFile(new File("/Users/mmathivanan/Desktop/image.jpg"), decodedBytes);
	}
	
	private static void dateTest() {
		String date = "2020-02-16T19:25:35+00:00";
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		DateTimeFormatter dateFormatterNew = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime ldateTime = LocalDateTime.parse(date.substring(0, date.length()-6), dateFormatter);

		System.out.println(dateFormatterNew.format(ldateTime));
	}
	
	private static void runAgain() throws Exception {
		
try {
			String asin = "B07JXY9GC1";
			String url = "https://www.amazon.com/dp/" + asin;

			HashMap<String,String> proxyDetails = Proxy.getProxyURL(url);
			
			if(proxyDetails.get("URL")==null) {
				runAgain();
				return;
			}
				
			Document doc = Jsoup.connect(proxyDetails.get("URL")).ignoreContentType(true).header("Cookie", proxyDetails.get("Cookie")).ignoreHttpErrors(true).timeout(10000).execute().parse();	
			if(doc ==null) {
				runAgain();
				return;
			}
			
			Element merchantInfo = doc.getElementById("merchant-info");
			if(merchantInfo ==null) {
				String output = "none";
				if(doc.toString().contains("api-services-support@amazon.com")) {
					output = "AMAZON API ERROR";
				}
				else {
					output = "PROXY ERROR";
				}
				runAgain();
				return;
			}
			
			Element customerRatings = doc.getElementById("acrCustomerReviewText");
			if(customerRatings!=null) {
				String ratings = customerRatings.text().split(Pattern.quote(" rating"))[0];
				//Helper.asin.get(asin).setRatings(Integer.parseInt(ratings));
			}
			

			
			if(merchantInfo.getElementsByTag("a").size()==0) {
				return;
			}
			
			String seller = merchantInfo.getElementsByTag("a").get(0).text();
			
			if(seller.compareToIgnoreCase("PEPMART TRADERS")==0) {
				//Helper.asin.get(asin).updateBuyBox(true);
				
				if(doc.getElementById("price_inside_buybox")!=null) {
					String price = doc.getElementById("price_inside_buybox").text();
					if(price!=null && price.contains("$")) {
						price = price.substring(1);
						Helper.asin.get(asin).update("sellingprice", price);
					}
				}
			}
			else {
				Helper.asin.get(asin).updateBuyBox(false);			
			}
			String asinDetails = null;
			if(doc.getElementById("averageCustomerReviews")!=null)
				asinDetails = doc.getElementById("averageCustomerReviews").attr("data-asin");
			
			if(asinDetails!=null){
				
			if(asinDetails.compareToIgnoreCase(asin)==0) {
				Helper.asin.get(asin).updateActive(true);
			}else {
				//Mailer.sendMail("ASIN VERIFICATION", "URL: " + asin + " Obtained: " + asinDetails, "ma.manoj@gmail.com");
				Helper.asin.get(asin).updateActive(false);
			}
			}
			System.out.println("Rating & Status success: " + asin);
		}catch(Exception e) {
			e.printStackTrace();
		}
	
	}
	
	private static void testfba() {
		
		try {
			String asin ="B07JXY9GC1";
			String url = "https://sellercentral.amazon.com/fba/profitabilitycalculator/getafnfee?profitcalcToken=0";
			
			System.out.println("Calling fba calculator for ASIN: " + asin);
			
			JSONObject jsonBody = new JSONObject();
			jsonBody.put("productInfoMapping", new JSONObject("{\"isWhiteGloveRequired\":false,\"weightUnitString\":\"pounds\",\"subCategory\":\"\",\"gl\":\"gl_home\",\"dimensionUnit\":\"inches\",\"length\":4,\"link\":\"http://www.amazon.com/gp/product/B07K1FNN7M/ref=silver_xx_cont_revecalc\",\"binding\":\"\",\"isAsinLimits\":true,\"weight\":0.5997,\"originalUrl\":\"\",\"title\":\"Trendy Home 12\\\" x 18\\\" Premium Hypoallergenic Stuffer Home Office Decorative Throw Pillow Insert, Standard/White (12x18)\",\"dimensionUnitString\":\"inches\",\"productGroup\":\"\",\"imageUrl\":\"https://m.media-amazon.com/images/I/31kdwS7aHCL._SCLZZZZZZZ__SL120_.jpg\",\"width\":12,\"thumbStringUrl\":\"https://m.media-amazon.com/images/I/31kdwS7aHCL._SCLZZZZZZZ__SL80_.jpg\",\"asin\":\"B07K1FNN7M\",\"encryptedMarketplaceId\":\"\",\"height\":15,\"weightUnit\":\"pounds\"}"));
			jsonBody.put("afnPriceStr", 0);
			jsonBody.put("currency", "USD");
			jsonBody.put("marketPlaceId", "ATVPDKIKX0DER");
			jsonBody.put("mfnPriceStr", Double.parseDouble("7.49"));
			jsonBody.put("mfnShippingPriceStr", Double.parseDouble("0.75"));
			HashMap<String,String> proxyDetails = Proxy.getProxyURL(url);
			
			if(proxyDetails.get("URL")==null) {
				//Helper.asin.get(asin).refreshFBAfees();
				System.out.println("Error1");
				return;
			}
			
			Document doc1 = Jsoup.connect(url).userAgent("Chrome")
					.header("Accept", "*/*")
					.header("Cache-Control", "no-cache")
					.header("Host", "sellercentral.amazon.com")
					.header("Accept-Encoding", "zip, deflate")
					.header("Content-Type", "application/json")
					.ignoreContentType(true)
					.method(Method.POST)
					.requestBody(jsonBody.toString())
					.ignoreHttpErrors(true).timeout(10000).execute().parse();	
			
			//System.out.println("FBA FEE output1: " + asin + " : " + doc1.toString());
			//System.out.println("FBA FEE output2: " + asin + " : " + doc1.getElementsByTag("body"));
			
			String output = doc1.getElementsByTag("body").text();
			//output = "failed doing again : " + asin + " output: " +  doc1.toString();
			//System.out.println(output);
			
			if(!output.startsWith("{")) {
				output = doc1.toString();
				if(output.contains("processedDate")) {
					output = output.split(Pattern.quote("<body>"))[1].split(Pattern.quote("</body>"))[0].substring(1);
				}
			}
			
			if(!output.startsWith("{")) {
				//Mailer.sendMail("fba failed", output , "ma.manoj@gmail.com");
				//Helper.asin.get(asin).refreshFBAfees();
				//System.out.println("failed doing again : " + asin + " output: " + output);
				return;
			}
			System.out.println("fba success : " + asin);
			//Helper.asin.get(asin).updateFBAFees(output,0d);
			
			}catch(Exception e) {
				e.printStackTrace();
				//Helper.asin.get(asin).refreshFBAfees();
			}
		
	}
	
	private static void test1() {
		try {
			
			String url = "https://sellercentral.amazon.com/fba/profitabilitycalculator/getafnfee?profitcalcToken=0";
			String dimensions = "{\"isWhiteGloveRequired\":false,\"weightUnitString\":\"pounds\",\"subCategory\":\"\",\"gl\":\"gl_home\",\"dimensionUnit\":\"inches\",\"length\":5,\"link\":\"http://www.amazon.com/gp/product/B07JPM1RTF/ref=silver_xx_cont_revecalc\",\"binding\":\"\",\"isAsinLimits\":true,\"weight\":2.8991,\"originalUrl\":\"\",\"title\":\"Trendy Home 12\\\" x 18\\\" Premium Hypoallergenic Stuffer Home Office Decorative Throw Pillow Insert, Standard/White (4 Pack)\",\"dimensionUnitString\":\"inches\",\"productGroup\":\"\",\"imageUrl\":\"https://m.media-amazon.com/images/I/31kdwS7aHCL._SCLZZZZZZZ__SL120_.jpg\",\"width\":13,\"thumbStringUrl\":\"https://m.media-amazon.com/images/I/31kdwS7aHCL._SCLZZZZZZZ__SL80_.jpg\",\"asin\":\"B07JPM1RTF\",\"encryptedMarketplaceId\":\"\",\"height\":17,\"weightUnit\":\"pounds\"}";
			//System.out.println("Calling fba calculator");
			
			JSONObject jsonBody = new JSONObject();
			jsonBody.put("productInfoMapping", new JSONObject(dimensions));
			jsonBody.put("afnPriceStr", 0);
			jsonBody.put("currency", "USD");
			jsonBody.put("marketPlaceId", "ATVPDKIKX0DER");
			jsonBody.put("mfnPriceStr", Double.parseDouble("5"));
			jsonBody.put("mfnShippingPriceStr", Double.parseDouble("3"));
			
			
			Document doc1 = Jsoup.connect(url).userAgent("Chrome")
					.header("Accept", "*/*")
					.header("Cache-Control", "no-cache")
					.header("Host", "sellercentral.amazon.com")
					.header("Accept-Encoding", "zip, deflate")
					.header("Content-Type", "application/json")
					.ignoreContentType(true)
					.method(Method.POST)
					.requestBody(jsonBody.toString())
					.ignoreHttpErrors(true).timeout(10000).execute().parse();	
			System.out.println(doc1);
			String output = doc1.getElementsByTag("body").text();
			
			//System.out.println("FBA FEE output: " + asin + " : " + output);
			System.out.println(output);
			if(!output.startsWith("{")) {
				//Helper.asin.get(asin).refreshFBAfees();
				return;
			}
			System.out.println(output);
			
			}catch(Exception e) {
				e.printStackTrace();
				//Helper.asin.get(asin).refreshFBAfees();
			}
	}
	
	private static void reviewsTest() throws IOException {
		Reviews rs = new Reviews("B08D3XLL26");
		rs.run();
		
	}

	private static void buytest() {
		try {
			//System.out.println("URL is: " + "https://www.amazon.com/dp/" + asin);
			String asin = "B07JP4XQ4J";

			String url = "https://www.amazon.com/gp/offer-listing/" + asin;
			//System.out.println("Starting Buy Box Output: " + asin );
			HashMap<String,String> proxyDetails = Proxy.getProxyURL(url);
			
			if(proxyDetails.get("URL")==null) {
				Helper.asin.get(asin).refreshRatingsAndStatus();
				return;
			}
				
			Document doc = Jsoup.connect(url).ignoreContentType(true).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36").ignoreHttpErrors(true).timeout(10000).execute().parse();	
			if(doc.getElementById("olpProduct") ==null) {
				System.out.println("error");
				buytest();
				return;
			}
			
			
			Elements customerReviewsClass = doc.getElementsByClass("a-link-normal");
			System.out.println("reviews array: " + customerReviewsClass);
			if(customerReviewsClass != null) {
				Element customerReviews = customerReviewsClass.get(3);
				System.out.println("reviews first: " + customerReviews);
			if(customerReviews!=null) {
				System.out.println("reviews text" +  customerReviews.text());
				String reviews = customerReviews.text().split(Pattern.quote(" "))[0];
				System.out.println("reviews number" +  reviews);
				Helper.asin.get(asin).setReviews(Integer.parseInt(reviews));
			}
			}
			
			Element prodDetails = doc.getElementById("productDetails_detailBullets_sections");
			Elements rows = prodDetails.select("tr");
			String asinDetails = null;

			for (int i = 1; i < rows.size(); i++) { //first row is the col names so skip it.
				Element row = rows.get(i);
				Elements cols = row.select("td");
				if(cols.get(0).text().compareToIgnoreCase("ASIN")==0) {
					asinDetails = cols.get(1).text();
					break;
				}
			}
			
			if(asinDetails!=null && asinDetails.compareToIgnoreCase(asin)==0) {
				String price = doc.getElementById("priceblock_ourprice").text().substring(1);
				Helper.asin.get(asin).update("sellingprice", price);
				Helper.asin.get(asin).updateActive(true);
			}else {
				Helper.asin.get(asin).updateActive(false);
			}
			


		}catch(Exception e) {
			e.printStackTrace();
			//Helper.asin.get(asin).updateBuyBox(false);
			
		}
	
	}

}
