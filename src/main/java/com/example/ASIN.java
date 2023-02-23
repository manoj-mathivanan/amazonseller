package com.example;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class ASIN {
	
	public long lastUpdated;

	public String asin;
	public String title;
	public Boolean buybox;

	public String expectedlength;
	public String expectedwidth;
	public String expectedheight;
	public String expectedweight;

	public String obtainedlength;
	public String obtainedwidth;
	public String obtainedheight;
	public String obtainedweight;
	
	public String incomingunits;
	public String incomingvolume;
	
	public String availableunits;
	public String availablevolume;
	
	public String ubc;

	public String ref;

	public Boolean lengthmismatch = false;
	public Boolean widthmismatch = false;
	public Boolean heightmismatch = false;
	public Boolean weightmismatch = false;
	
	public Boolean maillengthmismatch = true;
	public Boolean mailwidthmismatch = true;
	public Boolean mailheightmismatch = true;
	public Boolean mailweightmismatch = true;

	public String lastpulled;

	public String fbafee;
	public String storagefee;
	public String sellingprice;

	public String returns;
	public String marketing;
	public String productcost;
	public String shipping;
	public String profit;
	public String profitpercentage;
	public Boolean profitstatus;
	public String costprice;

	public String fbalastpulled;

	public Boolean active;

	public int totalReviews;
	public int newReviews;
	public int ratings;

	public Boolean sendmail = true;
	private ScheduledTask taskScheduler;
	private Timer refresher;

	private static DecimalFormat df2 = new DecimalFormat("0.##");

	public String dimensionjson;

	public int count = 0;

	public ASIN(String asin) {
		this.asin = asin;
		fetchValuesFromDB();
		taskScheduler = new ScheduledTask();
		refresher = new Timer();
	}

	public class ScheduledTask extends TimerTask {
		public void run() {
			if(System.currentTimeMillis() - lastUpdated>1200000) {
				refreshFromAmazon();
			}
		}
	}
	
	public void startMonitor(long delay) {
		System.out.println("Starting monitor for ASIN: " + asin + " ratings: " + ratings);
		refresher.schedule(taskScheduler , delay, 900000);
	}

	public void refreshFromAmazon() {
		try {
			refreshRatingsAndStatus();//ratings, buy box, selling price, status
			if(active==true) {
				System.out.println("Asin: " + asin + " SKU: " + title + " active.");
				refreshSellerCentralDimension(); //length, width, height
				refreshFBAfees(); //fba fees, storage, profit
			}
			refreshReviews();//reviews
		}
		catch(Exception e) {
			e.printStackTrace();
		}	
	}

	public void refreshSellerCentralDimension() {
		Helper.executor.execute(new SellerCentralDimensions(asin));
	}

	public void refreshRatingsAndStatus() {
		Helper.executor.execute(new RatingsAndStatus(asin));
	}

	public void refreshFBAfees() {
		if(!active)
			System.out.println("Not fetching FBA Fees for ASIN: " + asin + " since it is not active");
		if(active==true && sellingprice!=null && productcost!=null && dimensionjson!=null && shipping!=null) {
			Helper.executor.execute(new FBAFees(asin));
		}
	}

	public void refreshReviews() {
		Helper.executor.execute(new Reviews(asin));
	}
	
	public void fetchValuesFromDB() {
		Connection connection = null;
		try { 
			connection = Helper.dataSource.getConnection();
			PreparedStatement stmt = connection.prepareStatement("SELECT * FROM amazonasin where asin = ?");
			stmt.setString(1, asin);
			ResultSet rs = stmt.executeQuery();
			if(!rs.next()) {
				System.out.println("ASIN NOT FOUND: " + asin);
				addNewASIN();
				return;
			}
			title = rs.getString("title");
			expectedlength = rs.getString("expectedlength");
			expectedwidth = rs.getString("expectedwidth");
			expectedheight = rs.getString("expectedheight");
			expectedweight = rs.getString("expectedweight");
			obtainedlength = rs.getString("obtainedlength");
			obtainedwidth = rs.getString("obtainedwidth");
			obtainedheight = rs.getString("obtainedheight");
			obtainedweight = rs.getString("obtainedweight");
			incomingunits = rs.getString("incomingunits");
			availableunits = rs.getString("availableunits");
			ubc = rs.getString("ubc");
			ref = rs.getString("ref");
			lengthmismatch = rs.getBoolean("lengthmismatch");
			widthmismatch = rs.getBoolean("widthmismatch");
			heightmismatch = rs.getBoolean("heightmismatch");
			weightmismatch = rs.getBoolean("weightmismatch");
			maillengthmismatch = rs.getBoolean("maillengthmismatch");
			mailwidthmismatch = rs.getBoolean("mailwidthmismatch");
			mailheightmismatch = rs.getBoolean("mailheightmismatch");
			mailweightmismatch = rs.getBoolean("mailweightmismatch");
			sendmail = rs.getBoolean("sendmail");
			lastpulled = rs.getString("lastpulled");
			fbalastpulled = rs.getString("fbalastpulled");
			buybox = rs.getBoolean("buybox");
			fbafee = rs.getString("fbafee");
			storagefee = rs.getString("storagefee");
			sellingprice = rs.getString("sellingprice");
			returns = rs.getString("returns");
			marketing = rs.getString("marketing");
			productcost = rs.getString("productcost");
			shipping = rs.getString("shipping")==null||rs.getString("shipping").isEmpty()?"0":rs.getString("shipping");
			profit = rs.getString("profit");
			profitpercentage = rs.getString("profitpercentage");
			profitstatus = rs.getBoolean("profitstatus");
			dimensionjson = rs.getString("dimensionjson");
			active = rs.getBoolean("active");
			totalReviews = rs.getInt("totalreviews");
			newReviews = rs.getInt("newReviews");
			ratings = rs.getInt("ratings");
			if(sellingprice!=null && profit!=null)
				costprice = (Double.parseDouble(sellingprice)-Double.parseDouble(profit))+"";
			else
				costprice = null;

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
	}

	public JSONObject getDetails() {
		fetchValuesFromDB();
		JSONObject asinDetails = new JSONObject();
		asinDetails.put("asin", asin);
		asinDetails.put("title", title);
		asinDetails.put("expectedlength", roundOff(expectedlength));
		asinDetails.put("expectedwidth", roundOff(expectedwidth));
		asinDetails.put("expectedheight", roundOff(expectedheight));
		asinDetails.put("expectedweight", roundOff(expectedweight));
		asinDetails.put("obtainedlength", roundOff(obtainedlength));
		asinDetails.put("obtainedwidth", roundOff(obtainedwidth));
		asinDetails.put("obtainedheight", roundOff(obtainedheight));
		asinDetails.put("obtainedweight", roundOff(obtainedweight));
		asinDetails.put("obtainedvolume", getVolume(obtainedlength,obtainedwidth,obtainedheight));
		asinDetails.put("incomingunits", roundOff(incomingunits));
		asinDetails.put("availableunits", roundOff(availableunits));
		asinDetails.put("availablevolume", getTotalExistingVolume(obtainedlength,obtainedwidth,obtainedheight,availableunits));
		asinDetails.put("ubc", ubc);
		asinDetails.put("ref", ref);
		asinDetails.put("incomingvolume", getTotalIncomingVolume(obtainedlength,obtainedwidth,obtainedheight,incomingunits));
		asinDetails.put("lengthmismatch", (!lengthmismatch?"tick.png":"wrong.png"));
		asinDetails.put("widthmismatch", (!widthmismatch?"tick.png":"wrong.png"));
		asinDetails.put("heightmismatch", (!heightmismatch?"tick.png":"wrong.png"));
		asinDetails.put("weightmismatch", (!weightmismatch?"tick.png":"wrong.png"));
		asinDetails.put("sendmail", (sendmail?"yes":"no"));
		asinDetails.put("buybox", (buybox?"tick.png":"wrong.png"));
		asinDetails.put("profitstatus", (profitstatus?"tick.png":"wrong.png"));
		asinDetails.put("fbafee", roundOff(fbafee));
		asinDetails.put("storagefee", roundOff(storagefee));
		asinDetails.put("shipping", roundOff(shipping));
		asinDetails.put("sellingprice", roundOff(sellingprice));
		asinDetails.put("costprice", roundOff(costprice));
		asinDetails.put("productcost", roundOff(productcost));
		asinDetails.put("profit", roundOff(profit));
		asinDetails.put("profitpercentage", roundOff(profitpercentage));
		asinDetails.put("active", active);
		asinDetails.put("returns", returns);
		asinDetails.put("marketing", marketing);
		String minutes = "";
		if(lastpulled==null) {
			minutes = "updating...";
		}else {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
			LocalDateTime now = LocalDateTime.now();  
			LocalDateTime d1 = LocalDateTime.parse(lastpulled, dtf);
			if(now.getDayOfMonth()!=now.getDayOfMonth()) {
				if(now.getHour()==d1.getHour())
					minutes = (now.getMinute() - d1.getMinute()) + " mins ago";
				else
					minutes = (now.getHour() - d1.getHour()) + " hrs ago";	
			} else {

				minutes = (now.getHour()*60 +now.getMinute())-(d1.getHour()*60 +d1.getMinute()) + " mins ago";
			}
		}
		asinDetails.put("lastpulled", minutes);

		String minutes2 = "";
		if(fbalastpulled==null) {
			minutes2 = "updating...";
		}else {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
			LocalDateTime now = LocalDateTime.now();  
			LocalDateTime d1 = LocalDateTime.parse(fbalastpulled, dtf);
			if(now.getDayOfMonth()!=now.getDayOfMonth()) {
				if(now.getHour()==d1.getHour())
					minutes2 = (now.getMinute() - d1.getMinute()) + " mins ago";
				else
					minutes2 = (now.getHour() - d1.getHour()) + " hrs ago";	
			} else {

				minutes2 = (now.getHour()*60 +now.getMinute())-(d1.getHour()*60 +d1.getMinute()) + " mins ago";
			}
		}
		asinDetails.put("fbalastpulled", minutes2);
		asinDetails.put("totalreviews", totalReviews);
		asinDetails.put("newreviews", newReviews);
		asinDetails.put("ratings", ratings);
		return asinDetails;
	}

	private String getVolume(String obtainedlength, String obtainedwidth, String obtainedheight) {
		Double volume = (Double.parseDouble(roundOff(obtainedlength))*Double.parseDouble(roundOff(obtainedwidth))*Double.parseDouble(roundOff(obtainedheight)))/1728;
		return df2.format(volume);
	}
	
	private String getTotalIncomingVolume(String obtainedlength, String obtainedwidth, String obtainedheight, String incomingUnits) {
		Double volume = (Double.parseDouble(roundOff(incomingUnits))*Double.parseDouble(roundOff(obtainedlength))*Double.parseDouble(roundOff(obtainedwidth))*Double.parseDouble(roundOff(obtainedheight)))/1728;
		incomingvolume = df2.format(volume);
		return incomingvolume;
	}
	
	private String getTotalExistingVolume(String obtainedlength, String obtainedwidth, String obtainedheight, String incomingUnits) {
		Double volume = (Double.parseDouble(roundOff(incomingUnits))*Double.parseDouble(roundOff(obtainedlength))*Double.parseDouble(roundOff(obtainedwidth))*Double.parseDouble(roundOff(obtainedheight)))/1728;
		availablevolume = df2.format(volume);
		return availablevolume;
	}

	public void delete() {
		Connection connection = null;
		try { 
			connection = Helper.dataSource.getConnection();
			PreparedStatement stmt = connection.prepareStatement("delete from amazonasin where asin = ?");
			stmt.setString(1, asin);
			stmt.executeUpdate();
			refresher.cancel();
			Helper.asin.remove(asin);

		} catch (Exception e) {
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

	public void toggleMail() {
		Connection connection = null;
		try { 
			connection = Helper.dataSource.getConnection();
			PreparedStatement stmt = null;
			stmt = connection.prepareStatement("update amazonasin set sendmail = ? where asin = ?");
			if(sendmail)
				stmt.setBoolean(1, false);
			else
				stmt.setBoolean(1, true);

			stmt.setString(2, asin);
			stmt.executeUpdate();
			stmt.close();
			connection.close();
			fetchValuesFromDB();

		} catch (Exception e) {
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

	public Boolean update(String key, String value) {
		Connection connection = null;
		try { 
			connection = Helper.dataSource.getConnection();
			PreparedStatement stmt = null;

			String sql = "update amazonasin set " + key + " = ? where asin = ?";

			if(key.compareToIgnoreCase("title")==0)
				sql = "update amazonasin set title = ? where asin = ?";
			else if(key.compareToIgnoreCase("sku")==0)
				sql = "update amazonasin set title = ? where asin = ?";

			stmt = connection.prepareStatement(sql);
			stmt.setString(1, value.replaceAll("^\"|\"$", ""));
			stmt.setString(2, asin);
			stmt.executeUpdate();
			stmt.close();
			connection.close();

			fetchValuesFromDB();

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally {
			if(connection!=null)
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}

	private boolean compareStrings(String obtained, String expected) {
		if(Double.parseDouble(obtained)<=Double.parseDouble(expected)) {
			return true;
		}
		else
			return false;
	}

	private boolean isEqualString(String newVal, String oldVal) {
		if(Double.parseDouble(newVal)==Double.parseDouble(oldVal)) {
			return true;
		}
		else
			return false;
	}

	public void updateDimensions(String amazonOutput) {
		Connection connection = null;
		try { 
			connection = Helper.dataSource.getConnection();
			PreparedStatement stmt = connection.prepareStatement("SELECT * FROM amazonasin where asin = ?");
			stmt.setString(1, asin);
			ResultSet rs = stmt.executeQuery();
			rs.next();

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
			LocalDateTime now = LocalDateTime.now();  

			if(amazonOutput == null) {
				return;
			}

			//JSONObject obj = new JSONObject(amazonOutput);
			//obj = new JSONObject((new JSONObject(amazonOutput)).get("result").toString());
			JSONObject details = (new JSONObject((new JSONObject(amazonOutput)).get("result").toString())).getJSONObject("GetInventoryBreakdownData").getJSONObject("itemDetails");
			JSONObject dimensions = details.getJSONObject("dimension");
			JSONObject weight = details.getJSONObject("weight");
			String length;
			String width;
			String height;

			PreparedStatement stmt2 = connection.prepareStatement("update amazonasin set "
					+ "title=?,obtainedlength=?,obtainedwidth=?,obtainedheight=?,obtainedweight=?,"
					+ "lastpulled=?,"
					+ "lengthmismatch=?,widthmismatch=?,heightmismatch=?,weightmismatch=?,"
					+ "maillengthmismatch=?,mailwidthmismatch=?,mailheightmismatch=?,mailweightmismatch=?,"
					+ "dimensionjson=? "
					+ "where asin=?");

			List<String> values = new ArrayList<String>();
			values.add(roundOff(dimensions.get("length").toString()));
			values.add(roundOff(dimensions.get("width").toString()));
			values.add(roundOff(dimensions.get("height").toString()));
			values.sort(new valuesSorter());
			length = values.get(2);
			width = values.get(1);
			height = values.get(0);
			String weightval = roundOff(weight.get("weight").toString());

			stmt2.setString(1, rs.getString("title"));
			stmt2.setString(2, length);
			stmt2.setString(3, width);
			stmt2.setString(4, height);
			stmt2.setString(5, roundOff(weight.get("weight").toString()));
			stmt2.setString(6, dtf.format(now));

			if(compareStrings(length, rs.getString("expectedlength"))) {
				stmt2.setBoolean(7, false);
				maillengthmismatch = true;
			}
			else {
				stmt2.setBoolean(7,true);
				if(maillengthmismatch && rs.getBoolean("sendMail")) {
					Mailer.sendMail(asin,"length",rs.getString("expectedlength"),length,rs.getString("title"));
					maillengthmismatch = false;
				}
			}
			if(compareStrings(width, rs.getString("expectedwidth"))) {
				stmt2.setBoolean(8, false);
				mailwidthmismatch = true;
			}
			else {
				stmt2.setBoolean(8,true);
				if(mailwidthmismatch && rs.getBoolean("sendMail")) {
					Mailer.sendMail(asin,"width",rs.getString("expectedwidth"),width,rs.getString("title"));
					mailwidthmismatch = false;
				}
			}
			if(compareStrings(height, rs.getString("expectedheight"))) {
				stmt2.setBoolean(9, false);
				mailheightmismatch = true;
			}
			else {
				stmt2.setBoolean(9,true);
				if(mailheightmismatch && rs.getBoolean("sendMail")) {
					Mailer.sendMail(asin,"height",rs.getString("expectedheight"),height,rs.getString("title"));
					mailheightmismatch = false;
				}
			}
			if(compareStrings(roundOff(weight.get("weight").toString()), rs.getString("expectedweight"))) {
				stmt2.setBoolean(10, false);
				mailweightmismatch = true;
			}
			else {
				stmt2.setBoolean(10,true);
				if(mailweightmismatch && rs.getBoolean("sendMail")) {
					Mailer.sendMail(asin,"weight",rs.getString("expectedweight"),roundOff(weight.get("weight").toString()),rs.getString("title"));
					mailweightmismatch = false;
				}
			}
			
			stmt2.setBoolean(11, maillengthmismatch);
			stmt2.setBoolean(12, mailwidthmismatch);
			stmt2.setBoolean(13, mailheightmismatch);
			stmt2.setBoolean(14, mailweightmismatch);

			stmt2.setString(15, amazonOutput);

			stmt2.setString(16, asin);

			stmt2.executeUpdate();

			if(!isEqualString(length,obtainedlength))
				Mailer.sendDimensionUpdateMail(asin,"length",length,obtainedlength,title);
			if(!isEqualString(width,obtainedwidth))
				Mailer.sendDimensionUpdateMail(asin,"width",width,obtainedwidth,title);
			if(!isEqualString(height,obtainedheight))
				Mailer.sendDimensionUpdateMail(asin,"height",height,obtainedheight,title);
			if(!isEqualString(weightval,obtainedweight))
				Mailer.sendDimensionUpdateMail(asin,"weight",weightval,obtainedweight,title);

			fetchValuesFromDB();

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
	}

	public void updateFBAFees(String fbaFeesStr) {

		Connection connection = null;
		try { 
			connection = Helper.dataSource.getConnection();

			if(fbaFeesStr == null) {
				//count = 1000;
				return;
			}
			
			Double referralFee = 0.15*Double.parseDouble(sellingprice);
			Double fbaFees = Double.parseDouble(roundOff(fbaFeesStr));
			Double storage_factor = 0.83;
			Calendar cal = Calendar.getInstance();
			int month = cal.get(Calendar.MONTH);
			if(month>=9) storage_factor=2.4;//2.4 is factor for oct,nov,dec
			Double storage = ((Double.parseDouble(roundOff(obtainedlength))*Double.parseDouble(roundOff(obtainedwidth))*Double.parseDouble(roundOff(obtainedheight)))/1728)*storage_factor;
			//display referral fee
			
			Double amazonFees = storage + fbaFees + referralFee;
			shipping = shipping==null||shipping.isEmpty()?"0":shipping;
			Double profit = Double.parseDouble(sellingprice) - Double.parseDouble(productcost) - Double.parseDouble(shipping) - amazonFees - Double.parseDouble(returns)*Double.parseDouble(sellingprice)/100 - Double.parseDouble(marketing)*Double.parseDouble(sellingprice)/100;
			PreparedStatement stmt2 = connection.prepareStatement("update amazonasin set "
					+ "fbafee=?,storagefee=?,profit=?,profitpercentage=?,profitstatus=?,fbalastpulled=?,ref=? "
					+ "where asin=?");
			
			System.out.println(fbaFees+""+storage+"");

			stmt2.setString(1, fbaFees+"");
			stmt2.setString(2, df2.format(storage)+"");
			stmt2.setString(3, df2.format(profit));
			stmt2.setString(4, df2.format((profit/Double.parseDouble(sellingprice)*100)) + "");
			stmt2.setBoolean(5, profit>0?true:false);
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
			LocalDateTime now = LocalDateTime.now();
			stmt2.setString(6, dtf.format(now));
			stmt2.setString(7, df2.format(referralFee)+"");
			stmt2.setString(8, asin);

			stmt2.executeUpdate();

			fetchValuesFromDB();

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
	}

	public class valuesSorter implements Comparator<String> {
		@Override
		public int compare(String x, String y) {

			if(Double.parseDouble(x)<Double.parseDouble(y))
				return -1;
			else if(Double.parseDouble(x)==Double.parseDouble(y))
				return 0;
			else 
				return 1;

		}

	}

	public void updateBuyBox(Boolean buyBoxStatus) {
		Connection connection = null;
		try { 
			connection = Helper.dataSource.getConnection();

			PreparedStatement stmt2 = connection.prepareStatement("update amazonasin set "
					+ "buybox=? "
					+ "where asin=?");

			stmt2.setBoolean(1, buyBoxStatus);
			stmt2.setString(2, asin);

			stmt2.executeUpdate();

			fetchValuesFromDB();

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
	}

	public void updateActive(Boolean activeStatus) {
		Connection connection = null;
		try { 
			connection = Helper.dataSource.getConnection();

			PreparedStatement stmt2 = connection.prepareStatement("update amazonasin set "
					+ "active=? "
					+ "where asin=?");

			stmt2.setBoolean(1, activeStatus);
			stmt2.setString(2, asin);

			stmt2.executeUpdate();

			fetchValuesFromDB();

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
	}

	public void setReviews(Integer currentReviews) {
		newReviews = currentReviews - totalReviews + newReviews;
		totalReviews = currentReviews;
		Connection connection = null;
		try { 
			connection = Helper.dataSource.getConnection();

			PreparedStatement stmt2 = connection.prepareStatement("update amazonasin set "
					+ "totalreviews=?, newReviews=? "
					+ "where asin=?");

			if(newReviews<0) newReviews = 0;
			stmt2.setInt(1, totalReviews);
			stmt2.setInt(2, newReviews);
			stmt2.setString(3, asin);

			stmt2.executeUpdate();

			fetchValuesFromDB();

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
	}

	public void setRatings(Integer ratings) {
		Connection connection = null;
		try { 
			connection = Helper.dataSource.getConnection();

			PreparedStatement stmt2 = connection.prepareStatement("update amazonasin set "
					+ "ratings=? "
					+ "where asin=?");

			stmt2.setInt(1, ratings);
			stmt2.setString(2, asin);

			stmt2.executeUpdate();

			fetchValuesFromDB();

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
	}

	public void resetReviewCounter() {
		Connection connection = null;
		try { 
			connection = Helper.dataSource.getConnection();

			PreparedStatement stmt2 = connection.prepareStatement("update amazonasin set "
					+ "newreviews=? "
					+ "where asin=?");

			stmt2.setInt(1, 0);
			stmt2.setString(2, asin);

			stmt2.executeUpdate();

			fetchValuesFromDB();

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
	}



	public String roundOff(String value) {
		try {
		if(value == null ) {
			return "0";
		}
		if(value.isEmpty()) {
			return "0";
		}
		return df2.format(Double.parseDouble(value)) + "";
		}catch(Exception e) {
			e.printStackTrace();
			return "0";
		}
	}



	public void addNewASIN() {
		Connection connection = null;
		try { 
			connection = Helper.dataSource.getConnection();
			PreparedStatement stmt = connection.prepareStatement("INSERT INTO amazonasin(asin) values(?)");
			stmt.setString(1, asin);
			stmt.executeUpdate();
			fetchValuesFromDB();

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
	}
	
	public String getOrders(String startdate, String enddate) {
		
		JSONArray orders = new JSONArray();
		Connection connection = null;
		try { 
			
			connection = Helper.dataSource.getConnection();
			PreparedStatement stmt = connection.prepareStatement("SELECT last_updated_date,asin,quantity FROM orders where ASIN = ? and last_updated_date BETWEEN ? AND ? ORDER BY last_updated_date");
			stmt.setString(1, asin);
			stmt.setDate(2, Date.valueOf(startdate));
			stmt.setDate(3, Date.valueOf(enddate));
			
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()) {
				JSONObject asinDetails = new JSONObject();
				
				asinDetails.put("date", rs.getString(1));
				asinDetails.put("quantity", Integer.parseInt(rs.getString(3)));
				orders.put(asinDetails);
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
		
		return orders.toString();
	}

}

