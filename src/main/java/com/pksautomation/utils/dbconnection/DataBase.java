package com.innovaccer.utils.dbconnection;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.bson.conversions.Bson;

import com.innovaccer.utils.AesEncrypter;
import com.innovaccer.utils.Config;
import com.innovaccer.utils.Helper;
import com.innovaccer.utils.Log;
import com.innovaccer.utils.TestDataReader;
import com.innovaccer.utils.dbconnection.DataBaseEnumConstants.DatabaseType;
import com.innovaccer.utils.dbconnection.DataBaseEnumConstants.MongoDataBaseType;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.mongodb.ServerAddress;
/**
 * 
 * @author pramod.singh
 *
 */
public class DataBase
{	
	public static Connection connection;
	/**
	 * Put single row of resultSet in HashMap and also in runtime properties
	 * @param testConfig
	 * @param sqlResultSet
	 * @author i0465
	 * @return
	 */
	public static Map<String, String> addToRunTimeProperties(Config testConfig, ResultSet sqlResultSet)
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
			testConfig.logException(e);
		}

		Set<String> keys = mapData.keySet();
		for (String key : keys)
		{
			testConfig.putRunTimeProperty(key, mapData.get(key));
		}
		return mapData;
	}

	/**
	 * Executes the select db query and returns complete
	 * Resultset
	 * 
	 * @param Config
	 *            test config instance
	 * @param sqlRow
	 *            row number of the 'Query' column of 'SQL' sheet of Test data
	 *            excel having the query to be executed
	 *            @author i0465
	 * @return ResultSet -- Complete Result which is fetched is returned
	 */
	public static ResultSet executeSelectQuery(Config testConfig, int sqlRow, DatabaseType dbType)
	{
		// Read the Query column of SQL sheet of Test data excel
		TestDataReader sqlData = testConfig.getCachedTestDataReaderObject("SQL");
		String selectQuery = sqlData.GetData(sqlRow, "Query");
		selectQuery = Helper.replaceArgumentsWithRunTimeProperties(testConfig, selectQuery);
		Log.Comment("Executing the query - '" + selectQuery + "'", testConfig);
		return executeSelectQuery(testConfig, selectQuery, dbType);
	}

	/**
	 * Executes the select db query, and saves the result in
	 * Config.runtimeProperties as well as returns Map
	 * 
	 * @param Config
	 *            test config instance
	 * @param sqlRow
	 *            row number of the 'Query' column of 'SQL' sheet of Test data
	 *            excel having the query to be executed
	 * @param rowNumber
	 *            row number to be returned (use 1 for first row and -1 for last
	 *            row)
	 * @author i0465
	 * @return Map containing key:value pairs of specified row
	 */
	public static Map<String, String> executeSelectQuery(Config testConfig, int sqlRow, int rowNumber, DatabaseType dbType)
	{
		// Read the Query column of SQL sheet of Test data excel
		TestDataReader sqlData = testConfig.getCachedTestDataReaderObject("SQL");
		String selectQuery = sqlData.GetData(sqlRow, "Query");
		selectQuery = Helper.replaceArgumentsWithRunTimeProperties(testConfig, selectQuery);
		Log.Comment("Executing the query - '" + selectQuery + "'", testConfig);
		return executeSelectQuery(testConfig, selectQuery, rowNumber, dbType);
	}

	/**
	 * Executes the select db query and return the complete Result Set
	 * 
	 * @param Config
	 *            test config instance
	 * @param selectQuery
	 *            query to be executed
	 * @param DatabaseType
	 *            online/offline
	 * @author i0465
	 * @return Resultset
	 */

	public static ResultSet executeSelectQuery(Config testConfig, String selectQuery, DatabaseType dbType)
	{
		Date startDate = new Date();
		
		selectQuery = Helper.replaceArgumentsWithRunTimeProperties(testConfig, selectQuery);
		testConfig.logComment(" Start to execute SQL query : " + selectQuery);
		testConfig.logComment("=============================================================>>>>>>>>>>>>");
		Statement stmt = null;
		ResultSet resultSet = null;
		try
		{
			stmt = getConnection(testConfig, dbType).createStatement();
			resultSet = stmt.executeQuery(selectQuery);
		}
		catch (SQLException e)
		{
			testConfig.logException(e);
		}

		if (null == resultSet)
			testConfig.logWarning("No data was returned for this query");

		Date endDate = new Date();
		double timeDifference = (endDate.getTime() - startDate.getTime()) / 1000.00;

		if(timeDifference > 60)
			testConfig.logComment("<B>Time taken to run this query in minutes : " + timeDifference/60 + "</B>");
		//else
		//testConfig.logComment("Time taken to run this query in seconds : " + timeDifference);

		return resultSet;
	}


	private static HashMap<Integer, HashMap<String, String>> executeQueryHelper(Config testConfig,ResultSet resultSet)
	{
		// Convert that ResultSet into a HashMap
		HashMap<Integer, HashMap<String, String>> rowMapData = new HashMap<Integer, HashMap<String, String>>();
		//Starting Row Number 
		int row=1;
		try
		{
			while (resultSet.next())
			{			
				ResultSetMetaData meta = resultSet.getMetaData();

				HashMap<String, String> colMapData = new HashMap<String, String>();
				for (int col = 1; col <= meta.getColumnCount(); col++)
				{
					try{
						colMapData.put(meta.getColumnLabel(col), resultSet.getObject(col).toString());
					}
					catch(NullPointerException e){
						colMapData.put(meta.getColumnLabel(col), "");
					}
				}
				rowMapData.put(row,colMapData);	
				row++;
			}
		}catch (SQLException e){
			testConfig.logException(e);}
		catch(NullPointerException e){
			testConfig.logWarning("No data was returned for this query");
			rowMapData=null;
		}
		return rowMapData;
	}

	/**
	 * This Method is used to return all the rows return by a select query in a HashMap Structure
	 * 	Map<String,String> --> Map<Column Name,Column Data>
	 * @param testConfig
	 * @param DataBaseType  type 
	 * @param sqlRow Row number of SQl Query in dataSheet 
	 * @author i0465
	 * @return HashMap <Integer, Map<String,String>>
	 * 	Integer --> Row Numbers
	 * 	Map->Column Name And Values 
	 */
	public static HashMap<Integer, HashMap<String, String>> executeSelectQuery(Config testConfig,DatabaseType type,int sqlRow,String sheetname)
	{	
		// Fetch Complete Result Set

		ResultSet resultSet=executeSelectQuery(testConfig, sqlRow, type,sheetname);
		return executeQueryHelper(testConfig,resultSet);
	}

	/**
	 * This Method is used to return all the rows return by a select query in a HashMap Structure
	 * 	Map<String,String> --> Map<Column Name,Column Data>
	 * @param testConfig
	 * @param DataBaseType  type 
	 * @param sqlRow Row number of SQl Query in dataSheet 
	 * @author i0465
	 * @return HashMap <Integer, Map<String,String>>
	 * 	Integer --> Row Numbers
	 * 	Map->Column Name And Values 
	 */
	public static HashMap<Integer, HashMap<String, String>> executeSelectQuery(Config testConfig,DatabaseType type,int sqlRow)
	{	
		// Fetch Complete Result Set

		ResultSet resultSet=executeSelectQuery(testConfig, sqlRow, type);
		return executeQueryHelper(testConfig,resultSet);
	}

	/**
	 * Executes the select db query, and saves the result in
	 * Config.runtimeProperties as well as returns Map
	 * 
	 * @param Config
	 *            test config instance
	 * @param selectQuery
	 *            query to be executed
	 * @param rowNumber
	 *            row number to be returned (use 1 for first row and -1 for last
	 *            row)
	 * @author i0465
	 * @return Map containing key:value pairs of specified row
	 */
	public static Map<String, String> executeSelectQuery(Config testConfig, String selectQuery, int rowNumber, DatabaseType dbType)
	{
		Date startDate = new Date();
		selectQuery = Helper.replaceArgumentsWithRunTimeProperties(testConfig, selectQuery);
		testConfig.logComment(" Start to execute SQL query : " + selectQuery);
		testConfig.logComment("=============================================================>>>>>>>>>>>>");
		Statement stmt = null;
		ResultSet resultSet = null;
		try
		{
			stmt = getConnection(testConfig, dbType).createStatement();
			resultSet = stmt.executeQuery(selectQuery);
		}
		catch (SQLException e)
		{
			testConfig.logException(e);
		}
		catch (NullPointerException ne) 
		{
			testConfig.endExecutionOnfailure = true;
			testConfig.logFail("<-----Unable to Create Connection With Database!! Please check your Internet----->");
		}
		Map<String, String> resultMap = null;

		int row = 1;
		try
		{
			if (rowNumber == -1)
			{
				if (resultSet.last())
					resultMap = addToRunTimeProperties(testConfig, resultSet);
			}
			else
			{
				while (resultSet.next())
				{
					if (row == rowNumber)
					{
						resultMap = addToRunTimeProperties(testConfig, resultSet);
						break;
					}
					else
					{
						row++;
					}
				}
			}
		}
		catch (SQLException e)
		{
			testConfig.logException(e);
		}
		catch (NullPointerException ne) 
		{
			testConfig.logWarning("<----------------No Data returned by Query!! Please check---------------->");
		}
		finally
		{
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				testConfig.logException(e);
			}
		}

		if (null == resultMap)
			testConfig.logWarning("No data was returned for this query");

		Date endDate = new Date();
		double timeDifference = (endDate.getTime() - startDate.getTime()) / 1000.00;

		if(timeDifference > 60)
			testConfig.logComment("<B>Time taken to run this query in minutes : " + timeDifference/60 + "</B>");
		//else
		//testConfig.logComment("Time taken to run this query in seconds : " + timeDifference);

		return resultMap;
	}

	/**
	 * Executes the select db query, and saves the result in
	 * Config.runtimeProperties as well as returns Map
	 * 
	 * @param Config
	 *            test config instance
	 * @param sqlToUpdate
	 *            row number of the 'Query' column of 'SQL' sheet of Test data
	 *            excel having the query to be executed
	 * @param rowNumber
	 *            row number to be returned (use 1 for first row and -1 for last
	 *            row)
	 * @author i0465
	 * @return Map containing key:value pairs of specified row
	 */
	public static int executeUpdateQuery(Config testConfig, int sqlToUpdate, DatabaseType dbType)
	{		
		// Read the Query column of SQL sheet of Test data excel
		TestDataReader sqlData = testConfig.getCachedTestDataReaderObject("SQL");
		String updateQuery = sqlData.GetData(sqlToUpdate, "Query");

		return executeUpdateQuery(testConfig, updateQuery, dbType);
	}
	
	/**
	 * 
	 * @param testConfig
	 * @param sqlRow
	 * @param dbType
	 * @author i0465
	 * @return
	 */
	public static int executeUpdateQuery(Config testConfig,  String sheetPath, int sqlRow, DatabaseType dbType)
	{		
		// Read the Query column of SQL sheet of Test data excel
		TestDataReader sqlData = testConfig.getCachedTestDataReaderObject("SQL",sheetPath);
		String updateQuery = sqlData.GetData(sqlRow, "Query");

		return executeUpdateQuery(testConfig, updateQuery, dbType);
	}

	/**
	 * Executes the update db query
	 * 
	 * @param Config
	 *            test config instance
	 * @param updateQuery
	 *            query to be executed
	 *            @author i0465
	 * @return number of rows affected
	 */
	public static int executeUpdateQuery(Config testConfig, String updateQuery, DatabaseType dbType)
	{
		Date startDate = new Date();

		Statement stmt = null;
		int rows = 0;
		try
		{
			stmt = getConnection(testConfig, dbType).createStatement();
			updateQuery = Helper.replaceArgumentsWithRunTimeProperties(testConfig, updateQuery);

			if(testConfig.getRunTimeProperty("replaceNULLInQuery") != null && testConfig.getRunTimeProperty("replaceNULLInQuery").equalsIgnoreCase("true"))
			{
				if(updateQuery.contains("'(null)'") || updateQuery.contains("'(NULL)'") || updateQuery.contains("'null'") || updateQuery.contains("'NULL'"))
				{
					updateQuery = updateQuery.replace("'(null)'", "NULL").replace("'(NULL)'", "NULL").replace("'null'", "NULL").replace("'NULL'", "NULL");
				}
			}

			Log.Comment("\nExecuting the update query - '" + updateQuery + "'", testConfig);
			rows = stmt.executeUpdate(updateQuery);
		}
		catch (SQLException e)
		{
			testConfig.logException(e);
		}
		finally
		{
			if (stmt != null)
			{
				try
				{
					stmt.close();
				}
				catch (SQLException e)
				{
					testConfig.logException(e);
				}
			}
		}
		if (0 == rows)
			testConfig.logWarning("No rows were updated by this query");

		Date endDate = new Date();
		double timeDifference = (endDate.getTime() - startDate.getTime()) / 1000.00;

		if(timeDifference > 60)
			testConfig.logComment("<B>Time taken to run this query in minutes : " + timeDifference/60 + "</B>");

		return rows;
	}
	/**
	 * Execute Query From Given Sheet
	 * @param testConfig
	 * @param sqlRow
	 * @param dbType
	 * @param sheetname
	 * @author i0465
	 * @return
	 */
	public static ResultSet executeSelectQuery(Config testConfig, int sqlRow, DatabaseType dbType,String sheetname)
	{
		// Read the Query column of SQL sheet of Test data excel
		TestDataReader sqlData = testConfig.getCachedTestDataReaderObject(sheetname);
		String selectQuery = sqlData.GetData(sqlRow, "Query");
		selectQuery = Helper.replaceArgumentsWithRunTimeProperties(testConfig, selectQuery);
		Log.Comment("Executing the query - '" + selectQuery + "'", testConfig);
		return executeSelectQuery(testConfig, selectQuery, dbType);
	}

	/**
	 * Creates database connection using the Config parameters -
	 * 'DBConnectionString', 'DBConnectionUsername' and 'DBConnectionPassword'
	 * 
	 * @param Config
	 *            test config instance
	 *            @author i0465
	 * @return Db Connection
	 */
	private static Connection getConnection(Config testConfig, DatabaseType dataBaseType)
	{
		Connection con = null;
		String connectString = null;
		String userName = null;
		String password = null;
		String relationDataBaseType;
		String dataBaseName =testConfig.getRunTimeProperty("SQLCommonDataBaseName");
		String isDBCredentialEncrypted = testConfig.getRunTimeProperty("isDBCredentialEncrypted");
		if(dataBaseType.values.equalsIgnoreCase("read_from_config"))
			dataBaseName =testConfig.getRunTimeProperty("SQLCommonDataBaseName");
		else
			dataBaseName=dataBaseType.values;
		
		userName=testConfig.getRunTimeProperty("SQLDBConnectionUsername");
		password=testConfig.getRunTimeProperty("SQLDBConnectionPassword");
		if(isDBCredentialEncrypted != null && isDBCredentialEncrypted.equalsIgnoreCase("true")) {
			userName = AesEncrypter.decryptString(testConfig,userName);
			password = AesEncrypter.decryptString(testConfig,password);
		}
		
		if(testConfig.getRunTimeProperty("RelationDataBaseType") != null)
			relationDataBaseType = testConfig.getRunTimeProperty("RelationDataBaseType");
		else
			relationDataBaseType="default";
		try
		{
			Class.forName(testConfig.getRunTimeProperty("DBConnectionDriver"));
			switch (relationDataBaseType.toLowerCase())
			{
			
			case "redshift" : 
				connectString = testConfig.getRunTimeProperty("SQLDBConnectionString")+"/" + dataBaseName;
				testConfig.logComment("Connecting to db :-" + connectString);
				if(testConfig.DBConnection !=null && !testConfig.DBConnection.isClosed())
					return testConfig.DBConnection;
				
				Properties properties = new Properties();
		        properties.setProperty("user", userName);
		        properties.setProperty("password", password);
				testConfig.logComment("Connecting to Test db:-" + connectString);
				testConfig.DBConnection = DriverManager.getConnection(connectString,properties);
				con = testConfig.DBConnection;
				break;

			case "greenplum":
			case "sql":
			case "postgres" :
				
				if(testConfig.DBConnection !=null && !testConfig.DBConnection.isClosed())
					return testConfig.DBConnection;
				
				connectString = testConfig.getRunTimeProperty("SQLDBConnectionString")+ "/" + dataBaseName;		
				testConfig.logComment("Connecting to db :-" + connectString);
				testConfig.DBConnection = DriverManager.getConnection(connectString, userName, password);
				con = testConfig.DBConnection;
				break;

			default:
				connectString = testConfig.getRunTimeProperty("SQLDBConnectionString")+"/" + dataBaseName;
				
				testConfig.logComment("Connecting to db :-" + connectString);
				if(testConfig.DBConnection !=null && !testConfig.DBConnection.isClosed())
					return testConfig.DBConnection;				
				testConfig.logComment("Connecting to Test db:-" + connectString);
				connectString = testConfig.getRunTimeProperty("TestDB");
				testConfig.DBConnection = DriverManager.getConnection(connectString, userName, password);
				con = testConfig.DBConnection;
			}

		}
		catch (ClassNotFoundException e)
		{
			con = null;
			testConfig.logException(e);
		}
		catch (SQLException e)
		{
			testConfig.logException(e);
		}
		return testConfig.DBConnection;
	}
	/**
	 * Executes detele query in DB
	 * @param testConfig : test config instance
	 * @param sqlRow : row number of sql query in excel
	 * @param dbType : type of DB
	 * @author i0465
	 * @return 
	 */
	public static int executeDeleteQuery(Config testConfig, int sqlRow, DatabaseType dbType)
	{		
		// Read the Query column of SQL sheet of Test data excel
		TestDataReader sqlData = testConfig.getCachedTestDataReaderObject("SQL");
		String deleteQuery = sqlData.GetData(sqlRow, "Query");

		return executeUpdateQuery(testConfig, deleteQuery, dbType);
	}

	/**
	 * This method converts resultset to list
	 * 
	 * @param resultset
	 *            SQL resultSet
	 * @author i0465
	 * @return sql data in list<hashmap<string,string>
	 */
	public static List<HashMap<String, String>> convertResultSetToList(Config testConfig, ResultSet rs)
	{
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		try
		{
			ResultSetMetaData md = rs.getMetaData();
			int columns = md.getColumnCount();

			while (rs.next())
			{
				HashMap<String, String> row = new HashMap<String, String>(columns);
				for (int i = 1; i <= columns; ++i)
				{
					row.put(md.getColumnLabel(i), rs.getString(i));
				}
				list.add(row);
			}
		}
		catch(SQLException e)
		{
			testConfig.logComment(e.getMessage());
		}

		return list;
	}

	/**
	 * This method is used to run a query on a provided DB with given connection string, username and password.
	 * @param query
	 * @param connectString
	 * @param userName
	 * @param password
	 * @author i0465
	 * @return
	 */
	public static ResultSet executeQueryWithoutClosingConnection(String query, String connectionString, String username, String password)
	{
		Date startDate = new Date();
		Statement stmt = null;
		ResultSet resultSet = null;
		try
		{
			connection = DriverManager.getConnection(connectionString, username, password);
			stmt = connection.createStatement();
			resultSet = stmt.executeQuery(query);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		if (null == resultSet)
			System.out.println("No data was returned for this query");

		Date endDate = new Date();
		double timeDifference = (endDate.getTime() - startDate.getTime()) / 1000.00;

		if(timeDifference > 60)
			System.out.println("<B>Time taken to run this query in minutes : " + timeDifference/60 + "</B>");
		else
			System.out.println("Time taken to run this query in seconds : " + timeDifference);
		return resultSet;
	}

	/**
	 * This method is used to run a query on a Postgres DB with given connection string, username and password.
	 * @param query
	 * @param connectString
	 * @param userName
	 * @param password
	 * @author i0465
	 * @return
	 */
	public static ResultSet executePostgresQueryWithoutClosingConnection(String query, String connectionString, String username, String password)
	{
		Date startDate = new Date();
		Statement stmt = null;
		ResultSet resultSet = null;
		try
		{
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection(connectionString, username, password);
			stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			resultSet = stmt.executeQuery(query);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}

		if (null == resultSet)
			System.out.println("No data was returned for this query");

		Date endDate = new Date();
		double timeDifference = (endDate.getTime() - startDate.getTime()) / 1000.00;

		if(timeDifference > 60)
			System.out.println("<B>Time taken to run this query in minutes : " + timeDifference/60 + "</B>");
		else
			System.out.println("Time taken to run this query in seconds : " + timeDifference);
		return resultSet;
	}
	
	/**
	 * Close the database connection, if open.
	 * @author i0465
	 */
	public static void closeDatabaseConnection()
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

	/**
	 * Executes insert query in DB
	 * @param testConfig 	test config instance
	 * @param sqlRow 		row number of sql query in excel
	 * @param dbType		type of DB
	 * @author i0465
	 * @return
	 */
	public static int executeInsertQuery(Config testConfig, int sqlRow, DatabaseType dbType)
	{
		// Read the Query column of SQL sheet of Test data excel
		TestDataReader sqlData = testConfig.getCachedTestDataReaderObject("SQL");
		String insertQuery = sqlData.GetData(sqlRow, "Query");

		return executeUpdateQuery(testConfig, insertQuery, dbType);
	}
	
	/**
	 * Executes select query in DB. Prepared statement is used which aids in making query more dynamic in nature
	 * @param testConfig 	test config instance
	 * @param selectQuery 	Skeleton Query
	 * @param dbType		type of DB
	 * arg1					array of arguments to prepare the final query
	 * @author 				i0556
	 * @return
	 */
	
	
	public static ResultSet executeSelectQuery_prep(Config testConfig, String selectQuery, DatabaseType dbType, String arg1[])
	{
		Date startDate = new Date();
		
		selectQuery = Helper.replaceArgumentsWithRunTimeProperties(testConfig, selectQuery);
		testConfig.logComment(" Start to execute SQL query : " + selectQuery);
		testConfig.logComment("=============================================================>>>>>>>>>>>>");
		ResultSet resultSet = null;
		try
		{
			PreparedStatement stmt=getConnection(testConfig, dbType).prepareStatement(selectQuery);
			for(int i=0;i<arg1.length;i++)
			{	
				stmt.setString(i+1,arg1[i]);
			}
			resultSet = stmt.executeQuery();
		}
		catch (SQLException e)
		{
			testConfig.logException(e);
		}

		if (null == resultSet)
			testConfig.logWarning("No data was returned for this query");

		Date endDate = new Date();
		double timeDifference = (endDate.getTime() - startDate.getTime()) / 1000.00;

		if(timeDifference > 60)
			testConfig.logComment("<B>Time taken to run this query in minutes : " + timeDifference/60 + "</B>");
		//else
		//testConfig.logComment("Time taken to run this query in seconds : " + timeDifference);

		return resultSet;
	}
}