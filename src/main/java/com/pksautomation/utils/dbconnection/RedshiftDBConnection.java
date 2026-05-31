package com.pksautomation.utils.dbconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

// this class is used only for testing purpose, Code for Relation Database connection is written in DataBase.java class
/**
 * 
 * @author pksautomation
 *
 */
class RedshiftDBConnection {
	static final String redshiftUrl = "jdbc:redshift://xxxxxxxxx:5439/xxxxxx";
    static final String masterUsername = "xxxxxxx";
    static final String password = "xxxxxxx";

    public static void main(String[] args) {
        Connection connection = null;
        Statement statement = null;

        try {
            Class.forName("com.amazon.redshift.jdbc41.Driver");
            Properties properties = new Properties();
            properties.setProperty("user", masterUsername);
            properties.setProperty("password", password);
            connection = DriverManager.getConnection(redshiftUrl, properties);
            // Further code to follow
        } catch(ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }
}
