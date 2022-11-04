package com.innovaccer.utils.v2.dbconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.innovaccer.utils.AesEncrypter;
import com.innovaccer.utils.dbconnection.DataBaseEnumConstants.DatabaseType;
import com.innovaccer.utils.v2.BrowserUtils;
import com.innovaccer.utils.v2.Config;
import com.innovaccer.utils.v2.Encryptions;
import com.innovaccer.utils.v2.LoggerUtils;
import com.innovaccer.utils.v2.fileutils.JSONUtils;
import com.innovaccer.utils.v2.fileutils.YamlUtils;

public class DataBaseConnection {
	
	private Config configInstance;
	private LoggerUtils loggerUtils;
    private Encryptions encryption;
    
    public DataBaseConnection() {
        init(Config.getConfig());
    }

    public DataBaseConnection(Config testConfig) {
        init(testConfig);
    }

    private void init(Config testConfig) {
        this.configInstance = testConfig;
        loggerUtils = new LoggerUtils(configInstance);
        encryption = new Encryptions(configInstance);
    }

    public static Connection connection;
	/**
	 * Put single row of resultSet in HashMap and also in runtime properties
	 * @param sqlResultSet
	 * @author i0465
	 * @return
	 */
	public Map<String, String> addToRunTimeProperties(ResultSet sqlResultSet)
	{
		HashMap<String, String> mapData = new HashMap<String, String>();

		try
		{
			ResultSetMetaData meta = sqlResultSet.getMetaData();
			for (int col = 1; col <= meta.getColumnCount(); col++)
			{
				try
				{
					String columnName = meta.getColumnLabel(col);
					String columnValue = sqlResultSet.getObject(col).toString();

					//Code to handle TINYINT case
					if(meta.getColumnTypeName(col).equalsIgnoreCase("TINYINT"))
						columnValue = Integer.toString(sqlResultSet.getInt(col));

					mapData.put(columnName, columnValue);
				}
				catch (Exception e)
				{
					mapData.put(meta.getColumnLabel(col), "");
				}
			}
		}
		catch (SQLException e)
		{
			loggerUtils.logException(e);
		}

		Set<String> keys = mapData.keySet();
		for (String key : keys)
		{
			configInstance.putRunTimeProperty(key, mapData.get(key));
		}
		return mapData;
	}
	
	/**
	 * Creates database connection using the Config parameters -
	 * 'DBConnectionString', 'DBConnectionUsername' and 'DBConnectionPassword'
	 *            @author i0465
	 * @return Db Connection
	 */
	public Connection getConnection(DatabaseType dataBaseType)
	{
		Connection con = null;
		String connectString = null;
		String userName = null;
		String password = null;
		String relationDataBaseType;
		String dataBaseName =configInstance.getRunTimeProperty("SQLCommonDataBaseName");
		String isDBCredentialEncrypted = configInstance.getRunTimeProperty("isDBCredentialEncrypted");
		if(dataBaseType.values.equalsIgnoreCase("read_from_config"))
			dataBaseName =configInstance.getRunTimeProperty("SQLCommonDataBaseName");
		else
			dataBaseName=dataBaseType.values;
		
		userName=configInstance.getRunTimeProperty("SQLDBConnectionUsername");
		password=configInstance.getRunTimeProperty("SQLDBConnectionPassword");
		if(isDBCredentialEncrypted != null && isDBCredentialEncrypted.equalsIgnoreCase("true")) {
			userName = encryption.aesDecryption(configInstance,userName);
			password = encryption.aesDecryption(configInstance,password);
		}
		
		if(configInstance.getRunTimeProperty("RelationDataBaseType") != null)
			relationDataBaseType = configInstance.getRunTimeProperty("RelationDataBaseType");
		else
			relationDataBaseType="default";
		try
		{
			Class.forName(configInstance.getRunTimeProperty("DBConnectionDriver"));
			switch (relationDataBaseType.toLowerCase())
			{
			
			case "redshift" : 
				connectString = configInstance.getRunTimeProperty("SQLDBConnectionString")+"/" + dataBaseName;
				loggerUtils.logComment("Connecting to db :-" + connectString);
				if(configInstance.getDBConnection() !=null && !configInstance.getDBConnection().isClosed())
					return configInstance.getDBConnection();
				
				Properties properties = new Properties();
		        properties.setProperty("user", userName);
		        properties.setProperty("password", password);
				loggerUtils.logComment("Connecting to Test db:-" + connectString);
				configInstance.setDBConnection(DriverManager.getConnection(connectString,properties));
				con = configInstance.getDBConnection();
				break;

			case "greenplum":
			case "sql":
			case "postgres" :
				
				if(configInstance.getDBConnection() !=null && !configInstance.getDBConnection().isClosed())
					return configInstance.getDBConnection();
				
				connectString = configInstance.getRunTimeProperty("SQLDBConnectionString")+ "/" + dataBaseName;		
				loggerUtils.logComment("Connecting to db :-" + connectString);
				configInstance.setDBConnection(DriverManager.getConnection(connectString, userName, password));
				con = configInstance.getDBConnection();
				break;

			default:
				connectString = configInstance.getRunTimeProperty("SQLDBConnectionString")+"/" + dataBaseName;
				
				loggerUtils.logComment("Connecting to db :-" + connectString);
				if(configInstance.getDBConnection() !=null && !configInstance.getDBConnection().isClosed())
					return configInstance.getDBConnection();				
				loggerUtils.logComment("Connecting to Test db:-" + connectString);
				connectString = configInstance.getRunTimeProperty("TestDB");
				configInstance.setDBConnection(DriverManager.getConnection(connectString, userName, password));
				con = configInstance.getDBConnection();
			}

		}
		catch (ClassNotFoundException e)
		{
			con = null;
			loggerUtils.logException(e);
		}
		catch (SQLException e)
		{
			loggerUtils.logException(e);
		}
		return configInstance.getDBConnection();
	}
	
	/**
	 * Close the database connection, if open.
	 * @author i0465
	 */
	public void closeDatabaseConnection()
	{
		if(connection != null)
		{
			try
			{
				connection.close();
				connection = null;
				System.out.println("Database connection closed successfully.");
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
}
