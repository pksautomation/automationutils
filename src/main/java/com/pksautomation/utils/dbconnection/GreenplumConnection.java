package com.pksautomation.utils.dbconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * this class is used only for testing purpose, Code for Relation Database connection is written in DataBase.java class
 * @author pksautomation
 *
 */
public class GreenplumConnection {
	public static Connection greenplumCon;
	
	public static Connection getConnection(){
		try {
		Class.forName("org.postgresql.Driver");
		greenplumCon = DriverManager.getConnection("jdbc:postgresql://db.internal.tech:5432/mna","*****", "********");
		Statement st = greenplumCon.createStatement();
		ResultSet rs = st.executeQuery("select * from attribution limit 1");
		while (rs.next()) {
		System.out.println("Column 1 returned ");
		System.out.println(rs.getObject(1));
		System.out.println(rs.getObject(2));
		}
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return greenplumCon;
	}
}
