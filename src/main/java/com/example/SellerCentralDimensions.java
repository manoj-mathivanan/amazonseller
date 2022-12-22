package com.example;

import org.json.JSONObject;

public class SellerCentralDimensions implements Runnable{
	private String asin;

	public SellerCentralDimensions(String asin) {

		this.asin = asin;
	}

	@Override
	public void run() {
		try {

			Thread.sleep(Helper.delay);
			/*
			String json3 = Helper.sellerCentral.getDimensions(Helper.asin.get(asin).title);
			Thread.sleep(3000);
			
			if(json3!=null) {
				
				//System.out.println("Got value: " + json3);
				Helper.asin.get(asin).updateDimensions(json3);
				ErrorHandler.printError("(S)Dimensions","seller central success : " + asin);
				
			}else {
			ErrorHandler.printError("(E)Dimensions","Response is null");
			}
			
			Thread.sleep(4000);

			 */
			String json2 = Helper.sellerCentral.getInventory(Helper.asin.get(asin).title);
			Thread.sleep(3000);
			
			if(json2!=null) {
				
				//System.out.println("Got value for available stock: " + json2);
				JSONObject obj = new JSONObject(json2);
				JSONObject asd = new JSONObject(obj.get("result").toString());
				JSONObject asinDetails = asd.getJSONObject("GetInventoryBreakdownData").getJSONObject("availableBreakdown");
				int instock = asinDetails.getInt("availableTotal") - asinDetails.getInt("inbound");
				Helper.asin.get(asin).update("availableunits", instock+"");
				ErrorHandler.printError("(S)Volume Dimensions","seller central success : " + asin);

				Helper.asin.get(asin).updateDimensions(json2);
				ErrorHandler.printError("(S)Dimensions","seller central success : " + asin);
			}else {
			
			ErrorHandler.printError("(E)Dimensions","VOL/quantity is null");
			}
			
			return;
/*
			String url = "https://sellercentral.amazon.com/fba/profitabilitycalculator/productmatches?searchKey=" + asin +"&searchType=keyword&language=en_US&profitcalcToken=0";
			
			HashMap<String,String> proxyDetails = Proxy.getProxyURL(url);

			if(proxyDetails.get("URL")==null) {
				ErrorHandler.printError("(E)Dimensions","Error getting url from proxy site");
				Helper.asin.get(asin).refreshSellerCentralDimension();
				return;
			}
			
			Document doc1 = Jsoup.connect(proxyDetails.get("URL")).ignoreContentType(true)
					.header("Cookie", proxyDetails.get("Cookie"))
					.header("Cache-Control", "no-cache")
					.header("Host", "sellercentral.amazon.com")
					.header("Accept-Encoding", "zip, deflate")
					.header("Content-Type", "application/json")
					.ignoreHttpErrors(true).timeout(10000).execute().parse();	
						
			
			
			if(doc1 ==null) {
				ErrorHandler.printError("(E)Dimensions","Error getting response. response is null");
				Helper.asin.get(asin).refreshSellerCentralDimension();
				return;
			}
			
			String output = doc1.getElementsByTag("body").text();
			
			if(!output.startsWith("{")) {
				ErrorHandler.printError("(E)Dimensions","Response is not a json : "+output);
				Helper.asin.get(asin).refreshSellerCentralDimension();
				return;
			}
			ErrorHandler.printError("(S)Dimensions","seller central success : " + asin);
			Helper.asin.get(asin).updateDimensions(output);
*/
		}catch(Exception e) {
			if(!e.getMessage().contains("Read timed out"))
				e.printStackTrace();
			ErrorHandler.printError("(E)Dimensions","Error: " + e.getMessage());
			Helper.asin.get(asin).refreshSellerCentralDimension();
		}

	}

}
