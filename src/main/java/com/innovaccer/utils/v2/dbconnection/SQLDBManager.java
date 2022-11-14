package com.innovaccer.utils.v2.dbconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.innovaccer.utils.v2.Config;
import com.innovaccer.utils.v2.Encryptions;
import com.innovaccer.utils.Helper;
import com.innovaccer.utils.Log;
import com.innovaccer.utils.v2.dataHelper.TestDataReader;
import com.innovaccer.utils.dbconnection.DataBaseEnumConstants.DatabaseType;
import com.innovaccer.utils.v2.BrowserUtils;
import com.innovaccer.utils.v2.LoggerUtils;
import com.innovaccer.utils.v2.fileutils.JSONUtils;
import com.innovaccer.utils.v2.fileutils.YamlUtils;
import com.innovaccer.utils.v2.dbconnection.*;

public class SQLDBManager {
	
	private Config configInstance;
	private LoggerUtils loggerUtils;
	private Connection connection;
    private Encryptions encryption;
    
    public SQLDBManager() {
        init(Config.getConfig());
    }

    public SQLDBManager(Config testConfig) {
        init(testConfig);
    }

    private void init(Config testConfig) {
        this.configInstance = testConfig;
        loggerUtils = new LoggerUtils(configInstance);
        encryption = new Encryptions(configInstance);
    }
    
    /**
	 * Creates database connection using the Config parameters -
	 * 'DBConnectionString', 'DBConnectionUsername' and 'DBConnectionPassword'
	 *            @author i0465
	 * @return Db Connection
	 */
	public Connection getConnection(DatabaseType databaseType)
	{
		Connection con = null;
		String connectString = null;
		String userName = null;
		String password = null;
		String relationDataBaseType;
		String dataBaseName =configInstance.getRunTimeProperty("SQLCommonDataBaseName");
		String isDBCredentialEncrypted = configInstance.getRunTimeProperty("isDBCredentialEncrypted");
		if(databaseType.values.equalsIgnoreCase("read_from_config"))
			dataBaseName =configInstance.getRunTimeProperty("SQLCommonDataBaseName");
		else
			dataBaseName=databaseType.values;
		
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
				connection = configInstance.getDBConnection();
				break;

			case "greenplum":
			case "sql":
			case "postgres" :
				
				if(configInstance.getDBConnection() !=null && !configInstance.getDBConnection().isClosed())
					return configInstance.getDBConnection();
				
				connectString = configInstance.getRunTimeProperty("SQLDBConnectionString")+ "/" + dataBaseName;		
				loggerUtils.logComment("Connecting to db :-" + connectString);
				configInstance.setDBConnection(DriverManager.getConnection(connectString, userName, password));
				connection = configInstance.getDBConnection();
				break;

			default:
				connectString = configInstance.getRunTimeProperty("SQLDBConnectionString")+"/" + dataBaseName;
				
				loggerUtils.logComment("Connecting to db :-" + connectString);
				if(configInstance.getDBConnection() !=null && !configInstance.getDBConnection().isClosed())
					return configInstance.getDBConnection();				
				loggerUtils.logComment("Connecting to Test db:-" + connectString);
				connectString = configInstance.getRunTimeProperty("TestDB");
				configInstance.setDBConnection(DriverManager.getConnection(connectString, userName, password));
				connection = configInstance.getDBConnection();
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
	 * Executes the select db query and returns complete
	 * @param sqlRow
	 *            row number of the 'Query' column of 'SQL' sheet of Test data
	 *            excel having the query to be executed
	 * @return ResultSet -- Complete Result which is fetched is returned
	 */
	public ResultSet executeSelectQuery(int sqlRow, DatabaseType dbType)
	{
		// Read the Query column of SQL sheet of Test data excel
		TestDataReader sqlData = configInstance.getCachedTestDataReaderObject("SQL");
		String selectQuery = sqlData.GetData(sqlRow, "Query");
		selectQuery = configInstance.replaceArgumentsWithRunTimeProperties(selectQuery);
		loggerUtils.logComment("Executing the query - '" + selectQuery + "'");
		return executeSelectQuery(selectQuery, dbType);
	}

	/**
	 * Executes the select db query, and saves the result in runtimeProperties as well as returns Map
	 * @param sqlRow
	 *            row number of the 'Query' column of 'SQL' sheet of Test data
	 *            excel having the query to be executed
	 * @param rowNumber
	 *            row number to be returned (use 1 for first row and -1 for last
	 *            row)
	 * @return Map containing key:value pairs of specified row
	 */
	public Map<String, String> executeSelectQuery(int sqlRow, int rowNumber, DatabaseType dbType)
	{
		// Read the Query column of SQL sheet of Test data excel
		TestDataReader sqlData = configInstance.getCachedTestDataReaderObject("SQL");
		String selectQuery = sqlData.GetData(sqlRow, "Query");
		selectQuery = configInstance.replaceArgumentsWithRunTimeProperties(selectQuery);
		loggerUtils.logComment("Executing the query - '" + selectQuery + "'");
		return executeSelectQuery(selectQuery, rowNumber, dbType);
	}
	
	/**
	 * Executes the select db query and return the complete Result Set
	 * @param selectQuery
	 *            query to be executed
	 * @param DatabaseType
	 *            online/offline
	 * @author i0465
	 * @return Resultset
	 */

	public ResultSet executeSelectQuery(String selectQuery, DatabaseType dbType)
	{
		Date startDate = new Date();
		
		selectQuery = configInstance.replaceArgumentsWithRunTimeProperties(selectQuery);
		loggerUtils.logComment(" Start to execute SQL query : " + selectQuery);
		loggerUtils.logComment("=============================================================>>>>>>>>>>>>");
		Statement stmt = null;
		ResultSet resultSet = null;
		try
		{
			stmt = connection.createStatement();
			resultSet = stmt.executeQuery(selectQuery);
		}
		catch (SQLException e)
		{
			loggerUtils.logException(e);
		}

		if (null == resultSet)
			loggerUtils.logWarning("No data was returned for this query");

		Date endDate = new Date();
		double timeDifference = (endDate.getTime() - startDate.getTime()) / 1000.00;

		if(timeDifference > 60)
			loggerUtils.logComment("<B>Time taken to run this query in minutes : " + timeDifference/60 + "</B>");
		return resultSet;
	}
	
	private HashMap<Integer, HashMap<String, String>> executeQueryHelper(ResultSet resultSet)
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
			loggerUtils.logException(e);}
		catch(NullPointerException e){
			loggerUtils.logWarning("No data was returned for this query");
			rowMapData=null;
		}
		return rowMapData;
	}
	
	/**
	 * This Method is used to return all the rows return by a select query in a HashMap Structure
	 * 	Map<String,String> --> Map<Column Name,Column Data>
	 * @param DataBaseType  type 
	 * @param sqlRow Row number of SQl Query in dataSheet 
	 * @author i0465
	 * @return HashMap <Integer, Map<String,String>>
	 * 	Integer --> Row Numbers
	 * 	Map->Column Name And Values 
	 */
	public HashMap<Integer, HashMap<String, String>> executeSelectQuery(DatabaseType type,int sqlRow,String sheetname)
	{	
		// Fetch Complete Result Set

		ResultSet resultSet=executeSelectQuery(sqlRow, type,sheetname);
		return executeQueryHelper(resultSet);
	}
	
	/**
	 * This Method is used to return all the rows return by a select query in a HashMap Structure
	 * 	Map<String,String> --> Map<Column Name,Column Data>
	 * @param DataBaseType  type 
	 * @param sqlRow Row number of SQl Query in dataSheet 
	 * @author i0465
	 * @return HashMap <Integer, Map<String,String>>
	 * 	Integer --> Row Numbers
	 * 	Map->Column Name And Values 
	 */
	public HashMap<Integer, HashMap<String, String>> executeSelectQuery(DatabaseType type,int sqlRow)
	{	
		// Fetch Complete Result Set

		ResultSet resultSet=executeSelectQuery(sqlRow, type);
		return executeQueryHelper(resultSet);
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
	public Map<String, String> executeSelectQuery(String selectQuery, int rowNumber, DatabaseType dbType)
	{
		Date startDate = new Date();
		selectQuery = configInstance.replaceArgumentsWithRunTimeProperties(selectQuery);
		loggerUtils.logComment(" Start to execute SQL query : " + selectQuery);
		loggerUtils.logComment("=============================================================>>>>>>>>>>>>");
		Statement stmt = null;
		ResultSet resultSet = null;
		try
		{
			stmt = connection.createStatement();
			resultSet = stmt.executeQuery(selectQuery);
		}
		catch (SQLException e)
		{
			loggerUtils.logException(e);
		}
		catch (NullPointerException ne) 
		{
			configInstance.setEndExecutionOnfailure(true);
			loggerUtils.logFail("<-----Unable to Create Connection With Database!! Please check your Internet----->");
		}
		Map<String, String> resultMap = null;

		int row = 1;
		try
		{
			if (rowNumber == -1)
			{
				if (resultSet.last())
					resultMap = addToRunTimeProperties(resultSet);
			}
			else
			{
				while (resultSet.next())
				{
					if (row == rowNumber)
					{
						resultMap = addToRunTimeProperties(resultSet);
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
			loggerUtils.logException(e);
		}
		catch (NullPointerException ne) 
		{
			loggerUtils.logWarning("<----------------No Data returned by Query!! Please check---------------->");
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
				loggerUtils.logException(e);
			}
		}

		if (null == resultMap)
			loggerUtils.logWarning("No data was returned for this query");

		Date endDate = new Date();
		double timeDifference = (endDate.getTime() - startDate.getTime()) / 1000.00;

		if(timeDifference > 60)
			loggerUtils.logComment("<B>Time taken to run this query in minutes : " + timeDifference/60 + "</B>");
		//else
		//testConfig.logComment("Time taken to run this query in seconds : " + timeDifference);

		return resultMap;
	}
	
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
	public int executeUpdateQuery(int sqlToUpdate, DatabaseType dbType)
	{		
		// Read the Query column of SQL sheet of Test data excel
		TestDataReader sqlData = configInstance.getCachedTestDataReaderObject("SQL");
		String updateQuery = sqlData.GetData(sqlToUpdate, "Query");

		return executeUpdateQuery(updateQuery, dbType);
	}
	
	/**
	 * @param sqlRow
	 * @param dbType
	 * @author i0465
	 * @return
	 */
	public int executeUpdateQuery(String sheetPath, int sqlRow, DatabaseType dbType)
	{		
		// Read the Query column of SQL sheet of Test data excel
		TestDataReader sqlData = configInstance.getCachedTestDataReaderObject("SQL",sheetPath);
		String updateQuery = sqlData.GetData(sqlRow, "Query");

		return executeUpdateQuery(updateQuery, dbType);
	}
	
	/**
	 * Executes the update db query
	 * @param updateQuery
	 *            query to be executed
	 *            @author i0465
	 * @return number of rows affected
	 */
	public int executeUpdateQuery(String updateQuery, DatabaseType dbType)
	{
		Date startDate = new Date();

		Statement stmt = null;
		int rows = 0;
		try
		{
			stmt = connection.createStatement();
			updateQuery = configInstance.replaceArgumentsWithRunTimeProperties(updateQuery);

			if(configInstance.getRunTimeProperty("replaceNULLInQuery") != null && configInstance.getRunTimeProperty("replaceNULLInQuery").equalsIgnoreCase("true"))
			{
				if(updateQuery.contains("'(null)'") || updateQuery.contains("'(NULL)'") || updateQuery.contains("'null'") || updateQuery.contains("'NULL'"))
				{
					updateQuery = updateQuery.replace("'(null)'", "NULL").replace("'(NULL)'", "NULL").replace("'null'", "NULL").replace("'NULL'", "NULL");
				}
			}

			loggerUtils.logComment("\nExecuting the update query - '" + updateQuery + "'");
			rows = stmt.executeUpdate(updateQuery);
		}
		catch (SQLException e)
		{
			loggerUtils.logException(e);
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
					loggerUtils.logException(e);
				}
			}
		}
		if (0 == rows)
			loggerUtils.logWarning("No rows were updated by this query");

		Date endDate = new Date();
		double timeDifference = (endDate.getTime() - startDate.getTime()) / 1000.00;

		if(timeDifference > 60)
			loggerUtils.logComment("<B>Time taken to run this query in minutes : " + timeDifference/60 + "</B>");

		return rows;
	}
	
	/**
	 * Execute Query From Given Sheet
	 * @param sqlRow
	 * @param dbType
	 * @param sheetname
	 * @author i0465
	 * @return
	 */
	public ResultSet executeSelectQuery( int sqlRow, DatabaseType dbType,String sheetname)
	{
		// Read the Query column of SQL sheet of Test data excel
		TestDataReader sqlData = configInstance.getCachedTestDataReaderObject(sheetname);
		String selectQuery = sqlData.GetData(sqlRow, "Query");
		selectQuery = configInstance.replaceArgumentsWithRunTimeProperties(selectQuery);
		loggerUtils.logComment("Executing the query - '" + selectQuery + "'");
		return executeSelectQuery(selectQuery, dbType);
	}

	/**
	 * Executes detele query in DB
	 * @param sqlRow : row number of sql query in excel
	 * @param dbType : type of DB
	 * @author i0465
	 * @return 
	 */
	public int executeDeleteQuery(int sqlRow, DatabaseType dbType)
	{		
		// Read the Query column of SQL sheet of Test data excel
		TestDataReader sqlData = configInstance.getCachedTestDataReaderObject("SQL");
		String deleteQuery = sqlData.GetData(sqlRow, "Query");
		return executeUpdateQuery(deleteQuery, dbType);
	}

	/**
	 * This method converts resultset to list
	 * 
	 * @param resultset
	 *            SQL resultSet
	 * @author i0465
	 * @return sql data in list<hashmap<string,string>
	 */
	public List<HashMap<String, String>> convertResultSetToList(ResultSet rs)
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
			loggerUtils.logComment(e.getMessage());
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
	public ResultSet executeQueryWithoutClosingConnection(String query, String connectionString, String username, String password)
	{
		Date startDate = new Date();
		Statement stmt = null;
		ResultSet resultSet = null;
		try
		{
			configInstance.setDBConnection(DriverManager.getConnection(connectionString, username, password));
			stmt = configInstance.getDBConnection().createStatement();
			resultSet = stmt.executeQuery(query);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		if (null == resultSet)
			loggerUtils.logComment("No data was returned for this query");

		Date endDate = new Date();
		double timeDifference = (endDate.getTime() - startDate.getTime()) / 1000.00;

		if(timeDifference > 60)
			loggerUtils.logComment("<B>Time taken to run this query in minutes : " + timeDifference/60 + "</B>");
		else
			loggerUtils.logComment("Time taken to run this query in seconds : " + timeDifference);
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
	public ResultSet executePostgresQueryWithoutClosingConnection(String query, String connectionString, String username, String password)
	{
		Date startDate = new Date();
		Statement stmt = null;
		ResultSet resultSet = null;
		try
		{
			Class.forName("org.postgresql.Driver");
			configInstance.setDBConnection(DriverManager.getConnection(connectionString, username, password));
			stmt = configInstance.getDBConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
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
			loggerUtils.logComment("No data was returned for this query");

		Date endDate = new Date();
		double timeDifference = (endDate.getTime() - startDate.getTime()) / 1000.00;

		if(timeDifference > 60)
			loggerUtils.logComment("<B>Time taken to run this query in minutes : " + timeDifference/60 + "</B>");
		else
			loggerUtils.logComment("Time taken to run this query in seconds : " + timeDifference);
		return resultSet;
	}
	
	/**
	 * Executes insert query in DB
	 * @param sqlRow 		row number of sql query in excel
	 * @param dbType		type of DB
	 * @author i0465
	 * @return
	 */
	public int executeInsertQuery(int sqlRow, DatabaseType dbType)
	{
		// Read the Query column of SQL sheet of Test data excel
		TestDataReader sqlData = configInstance.getCachedTestDataReaderObject("SQL");
		String insertQuery = sqlData.GetData(sqlRow, "Query");

		return executeUpdateQuery(insertQuery, dbType);
	}
	
	/**
	 * Executes select query in DB. Prepared statement is used which aids in making query more dynamic in nature
	 * @param selectQuery 	Skeleton Query
	 * @param dbType		type of DB
	 * arg1					array of arguments to prepare the final query
	 * @return
	 */
	
	
	public ResultSet executeSelectQuery_prep(String selectQuery, DatabaseType dbType, String arg1[])
	{
		Date startDate = new Date();
		
		selectQuery = configInstance.replaceArgumentsWithRunTimeProperties(selectQuery);
		loggerUtils.logComment(" Start to execute SQL query : " + selectQuery);
		loggerUtils.logComment("=============================================================>>>>>>>>>>>>");
		ResultSet resultSet = null;
		try
		{
			PreparedStatement stmt=connection.prepareStatement(selectQuery);
			for(int i=0;i<arg1.length;i++)
			{	
				stmt.setString(i+1,arg1[i]);
			}
			resultSet = stmt.executeQuery();
		}
		catch (SQLException e)
		{
			loggerUtils.logException(e);
		}

		if (null == resultSet)
			loggerUtils.logWarning("No data was returned for this query");

		Date endDate = new Date();
		double timeDifference = (endDate.getTime() - startDate.getTime()) / 1000.00;

		if(timeDifference > 60)
			loggerUtils.logComment("<B>Time taken to run this query in minutes : " + timeDifference/60 + "</B>");
	
		return resultSet;
	}
	
	/**
	 * Close the SQL database connection, if open.
	 * @author i0465
	 */
	public void closeSQLDatabaseConnection()
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
