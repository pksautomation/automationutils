package com.innovaccer.utils.v2.dbconnection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.BsonDocument;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;
import com.innovaccer.utils.v2.Config;
import com.innovaccer.utils.v2.EncryptionUtils;
import com.innovaccer.utils.v2.LoggerUtils;
import com.innovaccer.utils.v2.fileutils.JSONUtils;
import com.innovaccer.utils.v2.dataHelper.ExcelDataReader;
import com.innovaccer.utils.v2.dbconnection.*;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

public class MongoDBManager {
	
	private Config configInstance;
	private LoggerUtils loggerUtils;
	private JSONUtils jsonUtils;
	private EncryptionUtils encryption; 
	private ExcelDataReader excelDataReader;
	
	public MongoDBManager() {
        init(Config.getConfig());
    }

    public MongoDBManager(Config testConfig) {
        init(testConfig);
    }

    private void init(Config testConfig) {
        this.configInstance = testConfig;
        loggerUtils = new LoggerUtils(configInstance);
        jsonUtils = new JSONUtils(configInstance);
        excelDataReader = new ExcelDataReader(configInstance);
        encryption = new EncryptionUtils(configInstance);
    }
	
	/**
	 * Execute command to fetch records from mongo data base and return first
	 * records only
	 * @param commandRow
	 * @param sheetname
	 * @param dbType
	 * @author i0465
	 * @return JSONObject
	 */
	public JSONObject executeMongoQuery(int rowNum) {
		ArrayList<JSONObject> arrayOfJSONObject = executeMongoQueryAndReturnArrayOfJSON(rowNum);
		if (arrayOfJSONObject.size() == 0) {
			return null;
		}
		else
			return executeMongoQueryAndReturnArrayOfJSON(rowNum).get(0);

	}
	
	/**
	 * Execute command to fetch records from mongo data base
	 * @param commandRow
	 * @param sheetname
	 * @param dbType
	 * @author i0465
	 * @return
	 */
	public ArrayList<JSONObject> executeMongoQueryAndReturnArrayOfJSON(int rowNum) {
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

		String mongoQueryExcelSheetPath =  configInstance.getRunTimeProperty("TestDataSheet");

		ExcelDataReader testDataReader = excelDataReader.getCachedTestDataReaderObject("MongoQuery",
				mongoQueryExcelSheetPath);

		configInstance.setMongoClientConnection(getMongoDBConnection());
		// testConfig.mongoClientConnection.getDatabaseNames();
		if(configInstance.getRunTimeProperty("RunTimeMongoDataBaseName") == null)
			mongoDatabaseName = testDataReader.GetData(rowNum, "MongoDataBaseName");
		else
			mongoDatabaseName= configInstance.getRunTimeProperty("RunTimeMongoDataBaseName");
		
		collectionName = testDataReader.GetData(rowNum, "CollectionName");
		loggerUtils.logComment("Avalable DataBase names are :" + configInstance.getMongoClientConnection().getDatabaseNames());
		loggerUtils.logComment("Conenct to data Base mongoDatabaseName : " + mongoDatabaseName);
		configInstance.setMongoAdminDatabase(configInstance.getMongoClientConnection().getDatabase(mongoDatabaseName));
		// testConfig.mongoAdminDatabase =
		// testConfig.mongoClientConnection.getDatabase(testDataReader.GetData(rowNum,
		// "MongoDataBaseName"));
		loggerUtils.logComment("Start connecting collection Names : " + collectionName);
		// testConfig.logComment("Available collection names : " +
		// testConfig.mongoAdminDatabase.listCollectionNames());
		MongoCollection<Document> mongoCollection = configInstance.getMongoAdminDatabase().getCollection(collectionName);

		if (!testDataReader.GetData(rowNum, "ProjectionQuery").equalsIgnoreCase("NA"))
			projectionQuery = jsonUtils.convertJSONStringIntoBsonDocument(
					configInstance.replaceArgumentsWithRunTimeProperties(testDataReader.GetData(rowNum, "ProjectionQuery")));
		if (!testDataReader.GetData(rowNum, "FindQuery").equalsIgnoreCase("NA"))
			filterQuery = jsonUtils.convertJSONStringIntoBsonDocument(configInstance
					.replaceArgumentsWithRunTimeProperties(testDataReader.GetData(rowNum, "FindQuery")));
		if (!testDataReader.GetData(rowNum, "SortQuery").equalsIgnoreCase("NA"))
			SortQuery = jsonUtils.convertJSONStringIntoBsonDocument(configInstance
					.replaceArgumentsWithRunTimeProperties(testDataReader.GetData(rowNum, "SortQuery")));

		if (filterQuery == null) {
			iterDoc = mongoCollection.find();
			loggerUtils.logComment("Execute find query --> " + configInstance.replaceArgumentsWithRunTimeProperties(
					testDataReader.GetData(rowNum, "FindQuery")));
			;
		} else {
			loggerUtils.logComment(" Find Query : " + filterQuery);
			iterDoc = mongoCollection.find(filterQuery);

		}
		if (projectionQuery != null) {
			loggerUtils.logComment(" Projection Query : " + projectionQuery);
			iterDoc = iterDoc.projection(projectionQuery);
		}
		if (SortQuery != null) {
			loggerUtils.logComment(" Sort Query : " + SortQuery);
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
			loggerUtils.logException(e);
		}

		return jsonObjectList;

	}

	public boolean isActiveMongoDbConnection(MongoDatabase mongoDataBase) {
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

	public int updateDocument(int mongoQueryRowNum, boolean many) {
		BsonDocument filterBson = null;
		BsonDocument updateBson = null;
		String mongoDatabaseName;
		String collectionName;
		String mongoQueryExcelSheetPath =  configInstance.getRunTimeProperty("TestDataSheet");
		ExcelDataReader testDataReader = excelDataReader.getCachedTestDataReaderObject("MongoQuery",
				mongoQueryExcelSheetPath);

		// testConfig.mongoClientConnection.getDatabaseNames();
		mongoDatabaseName = testDataReader.GetData(mongoQueryRowNum, "MongoDataBaseName");
		collectionName = testDataReader.GetData(mongoQueryRowNum, "CollectionName");

		if (testDataReader.GetData(mongoQueryRowNum, "FindQuery") != null && !testDataReader.GetData(mongoQueryRowNum, "FindQuery").equalsIgnoreCase("NA"))
			filterBson = jsonUtils.convertJSONStringIntoBsonDocument(configInstance.replaceArgumentsWithRunTimeProperties(testDataReader.GetData(mongoQueryRowNum, "FindQuery")));
		if (testDataReader.GetData(mongoQueryRowNum, "UpdateQuery") != null&& !testDataReader.GetData(mongoQueryRowNum, "UpdateQuery").equalsIgnoreCase("NA"))
			updateBson = jsonUtils.convertJSONStringIntoBsonDocument(configInstance.replaceArgumentsWithRunTimeProperties(testDataReader.GetData(mongoQueryRowNum, "UpdateQuery")));

		return updateDocument(filterBson, updateBson, mongoDatabaseName, collectionName,many);

	}

	/**
	 * Update document in Mongo DB collection
	 * @param filterBson     --> filter query in form of BSON
	 * @param updateBson     ---> update Query in form of BSON
	 * @param dataBaseName   ---> Mongo DataBase Name
	 * @param collectionName ---> Mongo collection Name
	 * @param many
	 * @author i0465
	 * @return
	 */
	public int updateDocument(BsonDocument filterBson, BsonDocument updateBson,
			String dataBaseName, String collectionName, boolean many) {
		UpdateResult updateResult = null;
		try {
			configInstance.setMongoClientConnection(getMongoDBConnection());
			loggerUtils.logComment("Avalable DataBase names are :" + configInstance.getMongoClientConnection().getDatabaseNames());
			loggerUtils.logComment("Conenct to data Base mongoDatabaseName : " + dataBaseName);
			configInstance.setMongoAdminDatabase(configInstance.getMongoClientConnection().getDatabase(dataBaseName));
			loggerUtils.logComment("Start connecting collection Names : " + collectionName);

			MongoCollection<Document> mongoCollection = configInstance.getMongoAdminDatabase().getCollection(collectionName);

			if (filterBson != null && updateBson != null) {
				if (!many) {
					updateResult = mongoCollection.updateOne(filterBson, updateBson);
				} else {
					updateResult = mongoCollection.updateMany(filterBson, updateBson);
				}
			} else
				loggerUtils.logFail("Either Filter query or update query is null, Filer query " + filterBson
						+ "  Update query : " + updateBson, false);

			if (updateResult == null || (updateResult.wasAcknowledged() && updateResult.getMatchedCount() == 0)) {
				loggerUtils.logComment("No records are updated");
				return 0;
			} else
				return (int) updateResult.getModifiedCount();

		} catch (RuntimeException e) {
			loggerUtils.logException(e);
			return -1;
		}
	}

	/**
	 * Remove document in Mongo DB collection
	 * @param mongoQueryRowNum
	 * @param many
	 * @author i0465
	 * @return
	 */
	public int removeDocument(int mongoQueryRowNum, boolean many) {
		BsonDocument filterBson = null;
		String mongoDatabaseName;
		String collectionName;
		String mongoQueryExcelSheetPath =  configInstance.getRunTimeProperty("TestDataSheet");
		ExcelDataReader testDataReader = excelDataReader.getCachedTestDataReaderObject("MongoQuery",
				mongoQueryExcelSheetPath);

		// testConfig.mongoClientConnection.getDatabaseNames();
		mongoDatabaseName = testDataReader.GetData(mongoQueryRowNum, "MongoDataBaseName");
		collectionName = testDataReader.GetData(mongoQueryRowNum, "CollectionName");

		if (testDataReader.GetData(mongoQueryRowNum, "FindQuery") != null
				&& !testDataReader.GetData(mongoQueryRowNum, "FindQuery").equalsIgnoreCase("NA"))
			filterBson = jsonUtils.convertJSONStringIntoBsonDocument(
					configInstance.replaceArgumentsWithRunTimeProperties(testDataReader.GetData(mongoQueryRowNum, "FindQuery")));

		return removeDocuments(filterBson, mongoDatabaseName, collectionName, many);

	}

	/**
	 * Update document in Mongo DB collection
	 * @param filterBson     --> filter query in form of BSON
	 * @param updateBson     ---> update Query in form of BSON
	 * @param dataBaseName   ---> Mongo DataBase Name
	 * @param collectionName ---> Mongo collection Name
	 * @param many
	 * @author i0465
	 * @return
	 */
	public int removeDocuments(BsonDocument filterBson, String dataBaseName,
			String collectionName, boolean many) {
		DeleteResult deleteResult = null;
		try {
			configInstance.setMongoClientConnection(getMongoDBConnection());
			loggerUtils.logComment("Avalable DataBase names are :" + configInstance.getMongoClientConnection().getDatabaseNames());
			loggerUtils.logComment("Conenct to data Base mongoDatabaseName : " + dataBaseName);
			configInstance.setMongoAdminDatabase(configInstance.getMongoClientConnection().getDatabase(dataBaseName));
			loggerUtils.logComment("Start connecting collection Names : " + collectionName);

			MongoCollection<Document> mongoCollection = configInstance.getMongoAdminDatabase().getCollection(collectionName);

			if (filterBson != null) {
				if (many) {
					deleteResult = mongoCollection.deleteMany(filterBson);
				} else {
					deleteResult = mongoCollection.deleteOne(filterBson);
				}
			} else
				loggerUtils.logFail("Filter query is null", false);
			if (deleteResult == null || (deleteResult.wasAcknowledged() && deleteResult.getDeletedCount() == 0)) {
				loggerUtils.logComment("No records are deleted");
				return 0;
			} else
				return (int) deleteResult.getDeletedCount();

		} catch (RuntimeException e) {
			loggerUtils.logException(e);
			return -1;
		}
	}
	
	/**
	 * insert Document in given collection and database name in Mongo DataBase
	 * @param commandRow
	 * @param sheetname
	 * @param dbType
	 * @author i0465
	 * @return
	 */
	public boolean insertMultipleDocuments(List<JSONObject> listOfJSONObject, String mongoDatabaseName, String collectionName) {
		boolean isInsertedSuccessful=true;
		ArrayList<Document> docs = new ArrayList<Document>();
		Document doc;
		
		try {
		
		for(int i=0; i<listOfJSONObject.size(); i++) {
			doc=Document.parse(listOfJSONObject.get(i).toString());
			docs.add(doc);
		}
		configInstance.setMongoClientConnection(getMongoDBConnection());
		
		loggerUtils.logComment("Avalable DataBase names are :" + configInstance.getMongoClientConnection().getDatabaseNames());
		loggerUtils.logComment("Conenct to data Base mongoDatabaseName : " + mongoDatabaseName);
		configInstance.setMongoAdminDatabase(configInstance.getMongoClientConnection().getDatabase(mongoDatabaseName));
		// testConfig.mongoAdminDatabase =
		// testConfig.mongoClientConnection.getDatabase(testDataReader.GetData(rowNum,
		// "MongoDataBaseName"));
		loggerUtils.logComment("Start connecting collection Names : " + collectionName);
		// testConfig.logComment("Available collection names : " +
		// testConfig.mongoAdminDatabase.listCollectionNames());
		MongoCollection<Document> mongoCollection = configInstance.getMongoAdminDatabase().getCollection(collectionName);
		mongoCollection.insertMany(docs);
		loggerUtils.logComment("Documents inserted successfully");
		
		}
		catch(Exception e) {
			loggerUtils.logComment("Something went wrong to insert documents in mongo database");
			loggerUtils.logException(e);
			isInsertedSuccessful=false;
		}
		return isInsertedSuccessful;
	}
	
	/**
	 * Get connection with mongo DB
	 * @param dbType
	 * @author i0465
	 * @return
	 */
	private  MongoClient getMongoDBConnection() {
		MongoDatabase mongoDataBaseConnection = null;
		String userName = configInstance.getRunTimeProperty("MongoUserName");
		String password = configInstance.getRunTimeProperty("MongoPassword");
		String isDBCredentialEncrypted = configInstance.getRunTimeProperty("isDBCredentialEncrypted");
		
		if(isDBCredentialEncrypted != null && isDBCredentialEncrypted.equalsIgnoreCase("true")) {
			userName = encryption.aesDecryption(configInstance,userName);
			password = encryption.aesDecryption(configInstance,password);
		}
		
		if (configInstance.getMongoClientConnection() != null)
			return configInstance.getMongoClientConnection();

		else {
			configInstance.setMongoClientConnection(getMongoServerConnection(userName, password,
					configInstance.getRunTimeProperty("MongoAdminDataBase")));
			configInstance.setMongoClientConnection(configInstance.getMongoClientConnection());
			loggerUtils.logComment("Connected to the Mongo database successfully");
		}
		return configInstance.getMongoClientConnection();
	}
	
	/**
	 * Making Connection Of mongo DB using mongo client and return DataBase
	 * Connection
	 * @param userName
	 * @param password
	 * @author i0465
	 */
	private  MongoClient getMongoServerConnection(String userName, String password,
			String mongoDatabaseName) {
		MongoDatabase mongoDBConnection = null;
		MongoCredential credential;
		MongoClient mongoClientConnection;
		String mongoHost = configInstance.getRunTimeProperty("mongo.host");
		int mongoPort = Integer.valueOf(configInstance.getRunTimeProperty("mongo.port"));

		if (configInstance.getRunTimeProperty("isMongoPasswordRequired").trim().equalsIgnoreCase("false")) {
			mongoClientConnection = new MongoClient(new ServerAddress(mongoHost, mongoPort));
		} else {
			// create credential for mongo db connection
			credential = MongoCredential.createCredential(userName, mongoDatabaseName, password.toCharArray());
			// Creating a Mongo client
			mongoClientConnection = new MongoClient(new ServerAddress(mongoHost, mongoPort), Arrays.asList(credential));
		}
		return mongoClientConnection;
	}


}
