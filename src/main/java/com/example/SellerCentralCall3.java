package com.example;

import java.io.File;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class SellerCentralCall3 {


	private String loginPage = "https://sellercentral.amazon.com";
	private ChromeDriver driver;
	private boolean isLoggedIn = false;
	private boolean initialized = false;
	private int logoutCount = 0;
	private String username = "";
	private String password = "";
	private boolean mailSent = true;

	
	public synchronized void initialize() {
		if(initialized) return;
		try {
			ChromeOptions options = new ChromeOptions();


			if(Helper.isLocal) {
				/*
				options.addArguments("--headless");
				options.addArguments("--disable-gpu");
				options.addArguments("—-disk-cache-size=0");
				options.addArguments("--enable-low-end-device-mode");
				options.addArguments("--disable-backing-store-limit");
				options.addArguments("--incognito");
				*/
				System.setProperty("webdriver.chrome.driver", "/Users/mmathivanan/git/amazon/chromedriver_mac");
			}
			else {
				options.addArguments("--headless");     
				options.addArguments("--disable-gpu");
				options.addArguments("—-disk-cache-size=0");
				options.addArguments("--enable-low-end-device-mode");
				options.addArguments("--disable-backing-store-limit");
				HashMap<String, Object> prefs = new HashMap<String, Object>();
				prefs.put("profile.managed_default_content_settings.images", 2);
				options.setExperimentalOption("prefs", prefs);
				//options.addArguments("--incognito");
				//options.setBinary("/app/.apt/usr/bin/google-chrome");
				System.setProperty("webdriver.chrome.driver", "/home/ma_manoj/chromedriver");
			}

			driver = new ChromeDriver(options);
			initialized = true;
		}catch(Exception e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void sleep(long millis) {
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private synchronized String getJson(String url) {
		try {
		if(isLoggedIn) {
			mailSent = false;
			driver.get(url);
			sleep(3000);
			String json = driver.findElement(By.tagName("body")).getText();
			//System.out.println("Getting json: " +  json);
			if(!json.startsWith("{")) {
				isLoggedOut();
				return null;
			}
			logoutCount = 0;
			return json;
		}else {
			if(!mailSent) {
				takeScreenshotAndSendMail("Logged out");
				mailSent = true;
			}
			return null;
		}
		}catch(Exception e) {
			if(!mailSent) {
				takeScreenshotAndSendMail("Exception: " + e.getMessage());
				mailSent = true;
			}
			return null;
		}
	}
	
	public synchronized Boolean isActive(String asin) {
		if(isLoggedIn) {
			try {
				String url = "https://sellercentral.amazon.com/inventory?viewId=PRICING&search:"+asin;
				driver.get(url);
				sleep(3000);
				return StringUtils.countMatches(driver.getPageSource(),asin)>3;
				//return driver.getPageSource()..contains(asin);
			}catch(Exception e) {
				return null;
			}
		}
		return null;
	}
	
	public synchronized String getFBAFees(String asin, String defaultVal, String sku) {
		try {
		String url = "https://sellercentral.amazon.com/inventory?viewId=PRICING&search:"+asin;
		if(isLoggedIn) {
			mailSent = false;
			driver.get(url);
			sleep(5000);
			String text = driver.findElement(By.id("np-widget-inline-text-np-widget-id-0")).getText();
			WebElement table = driver.findElementsByClassName("mt-table").get(0);
			List<WebElement> rowsList = table.findElements(By.tagName("tr"));
			int row = 0;
			if(rowsList.size()>2){
				for(int i=1;i<rowsList.size()-1;i++){
					List<WebElement> cols = rowsList.get(i).findElements(By.tagName("td"));
					boolean found = false;
					for(int c=0;c<cols.size();c++){
						WebElement col = cols.get(c);
						String attr = col.getAttribute("data-column");
						if(attr!=null && attr.contains("sku")){
							WebElement div = col.findElements(By.tagName("a")).get(0);
							String page_sku = div.getText().trim();
							if(page_sku.compareToIgnoreCase(sku)==0){
								found = true;
							}
							break;
						}
					}
					if(found){
						for(int c=0;c<cols.size();c++){
							WebElement col = cols.get(c);
							String attr = col.getAttribute("data-column");
							if(attr!=null && attr.contains("fee_preview_widget")){
								WebElement div = col.findElements(By.tagName("a")).get(0);
								text = div.getText().trim();
								break;
							}
						}
						break;
					}
				}
			}

			if(text!=null) {
				String st1[] = text.trim().split(Pattern.quote("Includes "));
				if(st1.length>1) {
					String st2[] = st1[1].split(Pattern.quote(" "));
					if(st2.length>0 && st2[0].contains("$")) {
						System.out.println("Setting the new FBA fees value to be: " + st2[0].substring(1) + ". Old value: " + defaultVal);
						defaultVal = st2[0].substring(1);
					}else {
					System.out.println("FBA fees error3");}
				}else {
				System.out.println("FBA fees error2");}
			}else {
			System.out.println("FBA fees error1");}
		}else {
			if(!mailSent) {
				takeScreenshotAndSendMail("Logged out");
				mailSent = true;
			}
		}	
		}catch(Exception e) {
			e.printStackTrace();
			//takeScreenshotAndSendMail("Search screen");
			//System.out.println(driver.getPageSource());
		}
		return defaultVal;
	}
	
	public synchronized String getDimensions(String sku) {
		String url = "https://sellercentral.amazon.com/revenuecalculator/productmatch?searchKey="+sku+"&countryCode=US&locale=en-US";
		return getJson(url);
	}
	
	public synchronized String getInventory(String sku) {
		String url = "https://sellercentral.amazon.com/skucentralarbiter/inventory?msku=" + sku + "&locale=en-US";
		return getJson(url);
	}
	
	public  synchronized String getPrice(String sku) {
		String url = "https://sellercentral.amazon.com/skucentralarbiter/pricing?msku=" + sku + "&locale=en-US";
		return getJson(url);
	}
	//TODO: let exceptions be thrown
	public synchronized String login(String username, String pass) {
		try {
			this.username = username;
			this.password = pass;
			try {
			driver.get(loginPage);
			}
			catch(Exception e) {
				//if(e!=null && e.getMessage().contains("invalid session id")) {
					initialized=false;
					try{if(driver!=null) driver.close();}catch(Exception e2) {}
					initialize();
					driver.get(loginPage);
				//}
			}
			sleep(3000);
			
			try {
			if(isElementPresent("NEWS_HEADLINES")) {
				isLoggedIn = true;
				logoutCount = 0;
				return "loggedIn";
			}
			}catch(Exception e) {e.printStackTrace();}
			
			try {
				if(driver.getPageSource().contains("Keep me signed") && !driver.getPageSource().contains("OTP")) {
					WebElement password = driver.findElement(By.name("password"));
					password.sendKeys(this.password);
					driver.findElement(By.name("rememberMe")).click();;
					WebElement login = driver.findElement(By.id("signInSubmit"));
					sleep(4000);
					login.click();
				}
			}catch(Exception e) {e.printStackTrace();}
			
			try {
			if(driver.getPageSource().contains("Log in")) {
				driver.findElement(By.linkText("Log in")).click();
				sleep(7000);
				WebElement email = driver.findElement(By.name("email"));
				email.sendKeys(username);
				WebElement password = driver.findElement(By.name("password"));
				password.sendKeys(pass);
				driver.findElement(By.name("rememberMe")).click();;
				WebElement login = driver.findElement(By.id("signInSubmit"));
				sleep(2000);
				login.click();
				sleep(4000);
				if(driver.getTitle().contains("Two-Step Verification")) {
					return "triggered otp";
				}
			}
			}catch(Exception e) {e.printStackTrace();}
			
			if(checkLoggedIn()) {
				isLoggedIn = true;
				logoutCount = 0;
				return "logged In";
			}
			
			takeScreenshotAndSendMail("error logging in");
			return "unknown page";
		}
		catch(Exception e) {
			
				System.out.println("Error: " + e.getMessage());
				e.printStackTrace();
				return e.getMessage();
			
		}
	}

	public synchronized String setOTP(String otp) {
		try {
			if(driver.getTitle().contains("Two-Step Verification")) {
				WebElement otpText = driver.findElement(By.id("auth-mfa-otpcode"));
				otpText.sendKeys(otp);
				driver.findElement(By.name("rememberDevice")).click();
				driver.findElement(By.id("auth-signin-button")).click();
				sleep(4000);
				if(checkLoggedIn()){
					return "success";
				}
				else {
					return "logged in but validation by fetch sku failed";
				}
			}else {
				System.out.println("not otp page");
				return "not otp page";
			}
		}catch(Exception e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
			return e.getMessage();
		}
	}
	
	public synchronized void isLoggedOut() {
		if(logoutCount>2) {
			isLoggedIn = false;
			logoutCount = 0;
			login(username,password);
			if(isLoggedIn) return;
			takeScreenshotAndSendMail("logged Out");
			mailSent = true;
		}else {
			logoutCount++;
		}
	}
	
	public void takeScreenshotAndSendMail(String subject) {
		sleep(3000);

		try {
			TakesScreenshot scrShot =((TakesScreenshot)driver);
			File SrcFile=scrShot.getScreenshotAs(OutputType.FILE);
			byte[] fileContent = FileUtils.readFileToByteArray(SrcFile);
			String encodedString = Base64.getEncoder().encodeToString(fileContent);
			Mailer.sendMail(subject, encodedString,"ma.manoj@gmail.com");
		}catch(Exception e) {
			Mailer.sendMail(subject, e.getMessage(),"ma.manoj@gmail.com");
		}
		Mailer.sendMail(subject, driver.findElement(By.tagName("body")).getText(), "ma.manoj@gmail.com");
		Mailer.sendMail(subject, driver.findElement(By.tagName("body")).getText(), "ramshan2008@gmail.com");

		//Mailer.sendMail(subject, "", "ma.manoj@gmail.com");
		//Mailer.sendMail(subject, "", "ramshan2008@gmail.com");
	}
	
	public boolean checkLoggedIn() throws Exception {
		try {
		sleep(3000);
		if(driver.getPageSource().contains("Select an Account")) {
			
			List<WebElement> elements = driver.findElements(By.className("picker-name"));
			for(WebElement element :  elements) {
				System.out.println(element.getText());
				if(element.getText().contains("PEPMART")) {
					element.click();
					sleep(4000);
					break;
				}
			}
			sleep(3000);
			elements = driver.findElements(By.className("picker-name"));
			for(WebElement element :  elements) {
				System.out.println(element.getText());
				if(element.getText().contains("United State")) {
					element.click();
					sleep(4000);
					driver.findElements(By.className("picker-switch-accounts-button")).get(0).click();
					break;
				}
			}
		}
		}catch(Exception e) {e.printStackTrace();}

		driver.get(loginPage);
		sleep(5000);
		if(!isElementPresent("NEWS_HEADLINES")) { 
			takeScreenshotAndSendMail("log in validation failed");
			return false;
		}
		else {
			takeScreenshotAndSendMail("logged in");
			isLoggedIn = true;
			logoutCount = 0;
			return true;
		}
	}
	
	private boolean isElementPresent(String element) {
		try {
			WebElement e1 = driver.findElement(By.id(element));
			if(e1==null)
				return false;
		}catch(Exception e) {
			return false;
		}
		return true;
	}

}