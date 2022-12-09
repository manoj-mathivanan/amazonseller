package com.example;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.regex.Pattern;

public class Importer {

	String input = null;

	public Importer(String input) {
		this.input = input;
	}

	public int updateToDatabase() {
		int rows=0;
		Connection connection = null;
		try { 
			String[] lines = input.split(System.getProperty("line.separator"));
			connection = Helper.dataSource.getConnection();
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
			DateTimeFormatter dateFormatterNew = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			
			PreparedStatement stmt1 = connection.prepareStatement("select count(*) from orders");
			ResultSet rs = stmt1.executeQuery();
			rs.next();
			int start_count = rs.getInt(1);
			HashMap<String,Order> orders = new HashMap<String,Order>();
			
			PreparedStatement stmt2 = connection.prepareStatement("INSERT INTO orders (order_id, asin,last_updated_date,item_status,quantity,sku) VALUES(?,?,?,?,?,?) ON CONFLICT ON CONSTRAINT order_unique  DO NOTHING;");

			for(int i=1;i<lines.length;i++) {
				String line = lines[i].trim();
				if(!line.isEmpty() && !line.startsWith("amazon-order-id")) {
					String parts[] = line.split(Pattern.quote("\t"));
					String item_status = parts[13];
					if(item_status.compareToIgnoreCase("Cancelled")==0)
						continue;
					String asin = parts[12];
					String order_id = parts[0];
					String last_updated_date = parts[3];
					String sku = parts[11];
					int quantity = Integer.parseInt(parts[14]);
					
			        LocalDateTime ldateTime = LocalDateTime.parse(last_updated_date.substring(0, last_updated_date.length()-6), dateFormatter);
			        

			        last_updated_date = dateFormatterNew.format(ldateTime);
			        
			        String key = asin+"_"+last_updated_date;
			        if(orders.containsKey(key)) {
			        	Order order = orders.get(key);
			        	order.quantity = order.quantity + quantity;
			        }else {
			        	Order order = new Order(order_id,asin,last_updated_date,item_status,sku,quantity);
			        	orders.put(key, order);
			        }
			        
				}
			}
			
			for(Order order : orders.values()) {
				
				stmt2.setString(1, order.order_id);
				stmt2.setString(2, order.asin);
				stmt2.setDate(3, Date.valueOf(order.date));
				stmt2.setString(4, order.status);
				stmt2.setInt(5, order.quantity);
				stmt2.setString(6, order.sku);
				stmt2.addBatch();
				
			}
			
			stmt2.executeBatch();
			
			stmt1 = connection.prepareStatement("select count(*) from orders");
			rs = stmt1.executeQuery();
			rs.next();
			rows = rs.getInt(1) - start_count;
			
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
		System.out.println(rows + " updated");
		return rows;
	}

	public static void main(String[] args) {
		String st = "114-4892924-3377867	114-4892924-3377867	2020-02-18T05:05:21+00:00	2020-02-18T17:23:22+00:00	Shipped	Amazon	Amazon.com			Expedited	Trendy Home 12\" x 18\" Premium Hypoallergenic Stuffer Home Office Decorative Throw Pillow Insert, Standard/White (12x18)	th1218	B07K1FNN7M	Shipped	1	USD	7.49								MEMPHIS	TN	38117-3311	US		false		\r\n" + 
				"113-9925451-0581038	113-9925451-0581038	2020-02-18T05:02:46+00:00	2020-02-18T16:42:18+00:00	Shipped	Amazon	Amazon.com			Expedited	Trendy Home 20\" x 20\" Premium Hypoallergenic Stuffer Home Office Decorative Throw Pillow Insert, Standard/White (20x20(4pack))	th20p4	B07JQBJDRC	Shipped	1	USD	25.99	2.14							GRAND PRAIRIE	TX	75050-7893	US		false		\r\n" + 
				"111-3357289-8092261	111-3357289-8092261	2020-02-18T04:49:04+00:00	2020-02-19T08:30:11+00:00	Shipped	Amazon	Amazon.com			Expedited	Trendy Home 12\" x 18\" Premium Hypoallergenic Stuffer Home Office Decorative Throw Pillow Insert, Standard/White (12x18)	th1218	B07K1FNN7M	Shipped	1	USD	7.49	0.42							Mequon	WI	53092	US		false		\r\n" + 
				"112-4197184-1505047	112-4197184-1505047	2020-02-18T04:38:23+00:00	2020-02-18T20:11:50+00:00	Shipped	Amazon	Amazon.com			Expedited	Trendy Home 20\" x 20\" Premium Hypoallergenic Stuffer Home Office Decorative Throw Pillow Insert, Standard/White (20x20(4pack))	th20p4	B07JQBJDRC	Shipped	4	USD	103.96	8.56							GRAND PRAIRIE	TX	75052	US		false		\r\n" + 
				"112-6474735-6981805	112-6474735-6981805	2020-02-18T04:37:50+00:00	2020-02-18T21:41:29+00:00	Shipped	Amazon	Amazon.com			Expedited	Trendy Home 12\" x 20\" Premium Hypoallergenic Stuffer Home Office Decorative Throw Pillow Insert, Standard/White (12x20)	th1220	B07JP4XQ4J	Shipped	1	USD	7.49	0.48							STRATFORD	CT	06614-1403	US		false		\r\n" + 
				"114-1842682-5713050	114-1842682-5713050	2020-02-18T04:01:09+00:00	2020-02-18T18:36:04+00:00	Shipped	Amazon	Amazon.com			Expedited	Trendy Home 16\" x 16\" Premium Hypoallergenic Stuffer Home Office Decorative Throw Pillow Insert, Standard/White (16x16)	th16	B07JP47WQK	Shipped	2	USD	14.98	1.32							NEW YORK	NY	10012-3618	US		false		\r\n" + 
				"111-7668629-1498608	111-7668629-1498608	2020-02-18T03:52:22+00:00	2020-02-18T18:11:50+00:00	Shipped	Amazon	Amazon.com			Expedited	Trendy Home 12\" x 18\" Premium Hypoallergenic Stuffer Home Office Decorative Throw Pillow Insert, Standard/White (12x18)	th1218	B07K1FNN7M	Shipped	1	USD	7.49								MARIETTA	GA	30008-4489	US		false		\r\n" + 
				"114-3698762-8109021	114-3698762-8109021	2020-02-18T03:45:42+00:00	2020-02-18T10:15:06+00:00	Shipped	Amazon	Amazon.com			Expedited	Trendy Home 20\" x 20\" Premium Hypoallergenic Stuffer Home Office Decorative Throw Pillow Insert, Standard/White (20x20(4pack))	th20p4	B07JQBJDRC	Shipped	1	USD	25.99	1.56							MEDIA	PA	19063	US		false		\r\n" + 
				"111-4219154-4775464	111-4219154-4775464	2020-02-18T03:41:42+00:00	2020-02-19T01:19:47+00:00	Shipped	Amazon	Amazon.com			Expedited	Trendy Home 12\" x 20\" Premium Hypoallergenic Stuffer Home Office Decorative Throw Pillow Insert, Standard/White (12x20)	th1220	B07JP4XQ4J	Shipped	2	USD	14.98	1.2							OWENS CROSS ROADS	AL	35763-9058	US		false		\r\n" + 
				"114-0514444-5702662	114-0514444-5702662	2020-02-18T03:31:55+00:00	2020-02-19T06:40:04+00:00	Shipped	Amazon	Amazon.com			Expedited	Trendy Home 12\" x 18\" Premium Hypoallergenic Stuffer Home Office Decorative Throw Pillow Insert, Standard/White (12x18)	th1218	B07K1FNN7M	Shipped	2	USD	14.98	1.12							FARGO	ND	58103-4720	US		false		\r\n" + 
				"111-1357150-2051438	111-1357150-2051438	2020-02-18T03:27:11+00:00	2020-02-18T15:32:01+00:00	Shipped	Amazon	Amazon.com			Expedited	Trendy Home 12\" x 20\" Premium Hypoallergenic Stuffer Home Office Decorative Throw Pillow Insert, Standard/White (12x20)	th1220	B07JP4XQ4J	Shipped	1	USD	7.49								Ringgold	GA	30736-3017	US		false		\r\n" + 
				"114-9172604-4691450	114-9172604-4691450	2020-02-18T03:18:04+00:00	2020-02-18T14:57:02+00:00	Shipped	Amazon	Amazon.com			Expedited	Trendy Home 20\" x 20\" Premium Hypoallergenic Stuffer Home Office Decorative Throw Pillow Insert, Standard/White (20x20(4pack))	th20p4	B07JQBJDRC	Shipped	1	USD	25.99	1.92							MOORHEAD	MN	56560-6773	US		false		\r\n" + 
				"113-6888348-8537012	113-6888348-8537012	2020-02-18T03:12:30+00:00	2020-02-18T07:54:46+00:00	Shipped	Amazon	Amazon.com			Expedited	Trendy Home 12\" x 20\" Premium Hypoallergenic Stuffer Home Office Decorative Throw Pillow Insert, Standard/White (12x20)	th1220	B07JP4XQ4J	Shipped	1	USD	7.49	0.62							KELLER	TX	76248-2357	US		false		\r\n" + 
				"\n"
				+ "\n"
				+ "\n"
				+ "\n"
				+ "\n";
		Importer imp = new Importer(st);
		imp.updateToDatabase();
	}

}
