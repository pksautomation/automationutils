package com.innovaccer.utils.dbconnection;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.BsonDocument;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import com.innovaccer.utils.AesEncrypter;
import com.innovaccer.utils.Config;
import com.innovaccer.utils.Helper;
import com.innovaccer.utils.Log;
import com.innovaccer.utils.TestDataReader;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

/**
 * 
 * @author pramod.singh
 *
 */
public class MongoConnection {

	public static Connection connection;
	public static MongoClient mongoClientConnection;
	public static MongoDatabase mongoAdminDatabase;

	/**
	 * Making Connection Of mongo DB using mongo client and return DataBase
	 * Connection
	 * 
	 * @param testConfig
	 * @param userName
	 * @param password
	 * @author i0465
	 */
	private static MongoClient getMongoServerConnection(Config testConfig, String userName, String password,
			String mongoDatabaseName) {
		MongoDatabase mongoDBConnection = null;
		MongoCredential credential;
		MongoClient mongoClientConnection;
		String mongoHost = testConfig.getRunTimeProperty("mongo.host");
		int mongoPort = Integer.valueOf(testConfig.getRunTimeProperty("mongo.port"));

		if (testConfig.getRunTimeProperty("isMongoPasswordRequired").trim().equalsIgnoreCase("false")) {
			mongoClientConnection = new MongoClient(new ServerAddress(mongoHost, mongoPort));
		} else {
			// create credential for mongo db connection
			credential = MongoCredential.createCredential(userName, mongoDatabaseName, password.toCharArray());
			// Creating a Mongo client
			mongoClientConnection = new MongoClient(new ServerAddress(mongoHost, mongoPort), Arrays.asList(credential));
		}
		return mongoClientConnection;
	}

	/**
	 * Get connection with mongo DB
	 * 
	 * @param testConfig
	 * @param dbType
	 * @author i0465
	 * @return
	 */
	private static MongoClient getMongoDBConnection(Config testConfig) {
		MongoDatabase mongoDataBaseConnection = null;
		String userName = testConfig.getRunTimeProperty("MongoUserName");
		String password = testConfig.getRunTimeProperty("MongoPassword");
		String isDBCredentialEncrypted = testConfig.getRunTimeProperty("isDBCredentialEncrypted");
		
		if(isDBCredentialEncrypted != null && isDBCredentialEncrypted.equalsIgnoreCase("true")) {
			userName = AesEncrypter.decryptString(testConfig,userName);
			password = AesEncrypter.decryptString(testConfig,password);
		}
		
		if (testConfig.mongoClientConnection != null)
			return testConfig.mongoClientConnection;

		else {
			testConfig.mongoClientConnection = getMongoServerConnection(testConfig, userName, password,
					testConfig.getRunTimeProperty("MongoAdminDataBase"));
			mongoClientConnection = testConfig.mongoClientConnection;
			testConfig.logComment("Connected to the Mongo database successfully");
		}
		return testConfig.mongoClientConnection;
	}

	/**
	 * Execute command to fetch records from mongo data base and return first
	 * records only
	 * 
	 * @param testConfig
	 * @param commandRow
	 * @param sheetname
	 * @param dbType
	 * @author i0465
	 * @return JSONObject
	 */
	public static JSONObject executeMongoQuery(Config testConfig, int rowNum) {
		ArrayList<JSONObject> arrayOfJSONObject = executeMongoQueryAndReturnArrayOfJSON(testConfig, rowNum);
		if (arrayOfJSONObject.size() == 0) {
			return null;
		}
		else
			return executeMongoQueryAndReturnArrayOfJSON(testConfig, rowNum).get(0);

	}

	/**
	 * Execute command to fetch records from mongo data base
	 * 
	 * @param testConfig
	 * @param commandRow
	 * @param sheetname
	 * @param dbType
	 * @author i0465
	 * @return
	 */
	public static ArrayList<JSONObject> executeMongoQueryAndReturnArrayOfJSON(Config testConfig, int rowNum) {
		BsonDocument filterQuery = null;
		BsonDocument projectionQuery = null;
		BsonDocument SortQuery = null;
		String mongoDatabaseName;
		String collectionName;
		FindIterable<Document> iterDoc;
		ArrayList<Document> docs = new ArrayList<Document>();

		JSONObject jsonObject = null;
		ArrayList<JSONObject> jsonObjectList;
		Map<String, String> queryDataInfo = new HashMap<String, String>();

		String mongoQueryExcelSheetPath =  testConfig.getRunTimeProperty("TestDataSheet");

		TestDataReader testDataReader = testConfig.getCachedTestDataReaderObject("MongoQuery",
				mongoQueryExcelSheetPath);

		testConfig.mongoClientConnection = getMongoDBConnection(testConfig);
		// testConfig.mongoClientConnection.getDatabaseNames();
		if(testConfig.getRunTimeProperty("RunTimeMongoDataBaseName") == null)
			mongoDatabaseName = testDataReader.GetData(rowNum, "MongoDataBaseName");
		else
			mongoDatabaseName= testConfig.getRunTimeProperty("RunTimeMongoDataBaseName");
		
		collectionName = testDataReader.GetData(rowNum, "CollectionName");
		testConfig.logComment("Avalable DataBase names are :" + testConfig.mongoClientConnection.getDatabaseNames());
		testConfig.logComment("Conenct to data Base mongoDatabaseName : " + mongoDatabaseName);
		testConfig.mongoAdminDatabase = testConfig.mongoClientConnection.getDatabase(mongoDatabaseName);
		// testConfig.mongoAdminDatabase =
		// testConfig.mongoClientConnection.getDatabase(testDataReader.GetData(rowNum,
		// "MongoDataBaseName"));
		testConfig.logComment("Start connecting collection Names : " + collectionName);
		// testConfig.logComment("Available collection names : " +
		// testConfig.mongoAdminDatabase.listCollectionNames());
		MongoCollection<Document> mongoCollection = testConfig.mongoAdminDatabase.getCollection(collectionName);

		if (!testDataReader.GetData(rowNum, "ProjectionQuery").equalsIgnoreCase("NA"))
			projectionQuery = Helper.convertJSONStringIntoBsonDocument(testConfig,
					Helper.replaceArgumentsWithRunTimeProperties(testConfig,
							testDataReader.GetData(rowNum, "ProjectionQuery")));
		if (!testDataReader.GetData(rowNum, "FindQuery").equalsIgnoreCase("NA"))
			filterQuery = Helper.convertJSONStringIntoBsonDocument(testConfig, Helper
					.replaceArgumentsWithRunTimeProperties(testConfig, testDataReader.GetData(rowNum, "FindQuery")));
		if (!testDataReader.GetData(rowNum, "SortQuery").equalsIgnoreCase("NA"))
			SortQuery = Helper.convertJSONStringIntoBsonDocument(testConfig, Helper
					.replaceArgumentsWithRunTimeProperties(testConfig, testDataReader.GetData(rowNum, "SortQuery")));

		if (filterQuery == null) {
			iterDoc = mongoCollection.find();
			testConfig.logComment("Execute find query --> " + Helper.replaceArgumentsWithRunTimeProperties(testConfig,
					testDataReader.GetData(rowNum, "FindQuery")));
			;
		} else {
			testConfig.logComment(" Find Query : " + filterQuery);
			iterDoc = mongoCollection.find(filterQuery);

		}
		if (projectionQuery != null) {
			testConfig.logComment(" Projection Query : " + projectionQuery);
			iterDoc = iterDoc.projection(projectionQuery);
		}
		if (SortQuery != null) {
			testConfig.logComment(" Sort Query : " + SortQuery);
			iterDoc = iterDoc.sort(SortQuery);
		}

		iterDoc.into(docs);
		jsonObjectList = new ArrayList<JSONObject>();
		try {
			for (Document doc : docs) {
				jsonObject = new JSONObject(doc.toJson());
				jsonObjectList.add(jsonObject);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			testConfig.logException(e);
		}

		return jsonObjectList;

	}

	/**
	 * 
	 */
	public static boolean isActiveMongoDbConnection(MongoDatabase mongoDataBase) {
		boolean status = false;
		if (mongoDataBase != null) {
			try {
				mongoDataBase.listCollectionNames();
				status = true;

			} catch (Exception e) {

			}
		}
		return status;

	}

	/**
	 * Update document in Mongo DB collection
	 * 
	 * @param testConfig
	 * @param mongoQueryRowNum
	 * @param many
	 * @author i0465
	 * @return
	 */

	public static int updateDocument(Config testConfig, int mongoQueryRowNum, boolean many) {
		BsonDocument filterBson = null;
		BsonDocument updateBson = null;
		String mongoDatabaseName;
		String collectionName;
		String mongoQueryExcelSheetPath =  testConfig.getRunTimeProperty("TestDataSheet");
		TestDataReader testDataReader = testConfig.getCachedTestDataReaderObject("MongoQuery",
				mongoQueryExcelSheetPath);

		// testConfig.mongoClientConnection.getDatabaseNames();
		mongoDatabaseName = testDataReader.GetData(mongoQueryRowNum, "MongoDataBaseName");
		collectionName = testDataReader.GetData(mongoQueryRowNum, "CollectionName");

		if (testDataReader.GetData(mongoQueryRowNum, "FindQuery") != null && !testDataReader.GetData(mongoQueryRowNum, "FindQuery").equalsIgnoreCase("NA"))
			filterBson = Helper.convertJSONStringIntoBsonDocument(testConfig,Helper.replaceArgumentsWithRunTimeProperties(testConfig,testDataReader.GetData(mongoQueryRowNum, "FindQuery")));
		if (testDataReader.GetData(mongoQueryRowNum, "UpdateQuery") != null&& !testDataReader.GetData(mongoQueryRowNum, "UpdateQuery").equalsIgnoreCase("NA"))
			updateBson = Helper.convertJSONStringIntoBsonDocument(testConfig,Helper.replaceArgumentsWithRunTimeProperties(testConfig,testDataReader.GetData(mongoQueryRowNum, "UpdateQuery")));

		return MongoConnection.updateDocument(testConfig, filterBson, updateBson, mongoDatabaseName, collectionName,many);

	}

	/**
	 * Update document in Mongo DB collection
	 * 
	 * @param testConfig
	 * @param filterBson     --> filter query in form of BSON
	 * @param updateBson     ---> update Query in form of BSON
	 * @param dataBaseName   ---> Mongo DataBase Name
	 * @param collectionName ---> Mongo collection Name
	 * @param many
	 * @author i0465
	 * @return
	 */
	public static int updateDocument(Config testConfig, BsonDocument filterBson, BsonDocument updateBson,
			String dataBaseName, String collectionName, boolean many) {
		UpdateResult updateResult = null;
		try {
			testConfig.mongoClientConnection = getMongoDBConnection(testConfig);
			testConfig
					.logComment("Avalable DataBase names are :" + testConfig.mongoClientConnection.getDatabaseNames());
			testConfig.logComment("Conenct to data Base mongoDatabaseName : " + dataBaseName);
			testConfig.mongoAdminDatabase = testConfig.mongoClientConnection.getDatabase(dataBaseName);
			testConfig.logComment("Start connecting collection Names : " + collectionName);

			MongoCollection<Document> mongoCollection = testConfig.mongoAdminDatabase.getCollection(collectionName);

			if (filterBson != null && updateBson != null) {
				if (!many) {
					updateResult = mongoCollection.updateOne(filterBson, updateBson);
				} else {
					updateResult = mongoCollection.updateMany(filterBson, updateBson);
				}
			} else
				testConfig.logFail("Either Filter query or update query is null, Filer query " + filterBson
						+ "  Update query : " + updateBson, false);

			if (updateResult == null || (updateResult.wasAcknowledged() && updateResult.getMatchedCount() == 0)) {
				testConfig.logComment("No records are updated");
				return 0;
			} else
				return (int) updateResult.getModifiedCount();

		} catch (RuntimeException e) {
			testConfig.logException(e);
			return -1;
		}
	}

	/**
	 * Remove document in Mongo DB collection
	 * 
	 * @param testConfig
	 * @param mongoQueryRowNum
	 * @param many
	 * @author i0465
	 * @return
	 */
	public static int removeDocument(Config testConfig, int mongoQueryRowNum, boolean many) {
		BsonDocument filterBson = null;
		String mongoDatabaseName;
		String collectionName;
		String mongoQueryExcelSheetPath =  testConfig.getRunTimeProperty("TestDataSheet");
		TestDataReader testDataReader = testConfig.getCachedTestDataReaderObject("MongoQuery",
				mongoQueryExcelSheetPath);

		// testConfig.mongoClientConnection.getDatabaseNames();
		mongoDatabaseName = testDataReader.GetData(mongoQueryRowNum, "MongoDataBaseName");
		collectionName = testDataReader.GetData(mongoQueryRowNum, "CollectionName");

		if (testDataReader.GetData(mongoQueryRowNum, "FindQuery") != null
				&& !testDataReader.GetData(mongoQueryRowNum, "FindQuery").equalsIgnoreCase("NA"))
			filterBson = Helper.convertJSONStringIntoBsonDocument(testConfig,
					Helper.replaceArgumentsWithRunTimeProperties(testConfig,
							testDataReader.GetData(mongoQueryRowNum, "FindQuery")));

		return MongoConnection.removeDocuments(testConfig, filterBson, mongoDatabaseName, collectionName, many);

	}

	/**
	 * Update document in Mongo DB collection
	 * 
	 * @param testConfig
	 * @param filterBson     --> filter query in form of BSON
	 * @param updateBson     ---> update Query in form of BSON
	 * @param dataBaseName   ---> Mongo DataBase Name
	 * @param collectionName ---> Mongo collection Name
	 * @param many
	 * @author i0465
	 * @return
	 */
	public static int removeDocuments(Config testConfig, BsonDocument filterBson, String dataBaseName,
			String collectionName, boolean many) {
		DeleteResult deleteResult = null;
		try {
			testConfig.mongoClientConnection = getMongoDBConnection(testConfig);
			testConfig
					.logComment("Avalable DataBase names are :" + testConfig.mongoClientConnection.getDatabaseNames());
			testConfig.logComment("Conenct to data Base mongoDatabaseName : " + dataBaseName);
			testConfig.mongoAdminDatabase = testConfig.mongoClientConnection.getDatabase(dataBaseName);
			testConfig.logComment("Start connecting collection Names : " + collectionName);

			MongoCollection<Document> mongoCollection = testConfig.mongoAdminDatabase.getCollection(collectionName);

			if (filterBson != null) {
				if (many) {
					deleteResult = mongoCollection.deleteMany(filterBson);
				} else {
					deleteResult = mongoCollection.deleteOne(filterBson);
				}
			} else
				testConfig.logFail("Filter query is null", false);
			if (deleteResult == null || (deleteResult.wasAcknowledged() && deleteResult.getDeletedCount() == 0)) {
				testConfig.logComment("No records are deleted");
				return 0;
			} else
				return (int) deleteResult.getDeletedCount();

		} catch (RuntimeException e) {
			testConfig.logException(e);
			return -1;
		}
	}
	
	/**
	 * insert Document in given collection and database name in Mongo DataBase
	 * 
	 * @param testConfig
	 * @param commandRow
	 * @param sheetname
	 * @param dbType
	 * @author i0465
	 * @return
	 */
	public static boolean insertMultipleDocuments(Config testConfig,List<JSONObject> listOfJSONObject, String mongoDatabaseName, String collectionName) {
		boolean isInsertedSuccessful=true;
		ArrayList<Document> docs = new ArrayList<Document>();
		Document doc;
		
		try {
		
		for(int i=0; i<listOfJSONObject.size(); i++) {
			doc=Document.parse(listOfJSONObject.get(i).toString());
			docs.add(doc);
		}
		testConfig.mongoClientConnection = getMongoDBConnection(testConfig);
		
		testConfig.logComment("Avalable DataBase names are :" + testConfig.mongoClientConnection.getDatabaseNames());
		testConfig.logComment("Conenct to data Base mongoDatabaseName : " + mongoDatabaseName);
		testConfig.mongoAdminDatabase = testConfig.mongoClientConnection.getDatabase(mongoDatabaseName);
		// testConfig.mongoAdminDatabase =
		// testConfig.mongoClientConnection.getDatabase(testDataReader.GetData(rowNum,
		// "MongoDataBaseName"));
		testConfig.logComment("Start connecting collection Names : " + collectionName);
		// testConfig.logComment("Available collection names : " +
		// testConfig.mongoAdminDatabase.listCollectionNames());
		MongoCollection<Document> mongoCollection = testConfig.mongoAdminDatabase.getCollection(collectionName);
		mongoCollection.insertMany(docs);
		testConfig.logComment("Documents inserted successfully");
		
		}
		catch(Exception e) {
			testConfig.logComment("Something went wrong to insert documents in mongo database");
			testConfig.logException(e);
			isInsertedSuccessful=false;
		}
		return isInsertedSuccessful;
	}

}