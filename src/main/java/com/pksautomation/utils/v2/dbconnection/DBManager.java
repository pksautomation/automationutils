package com.innovaccer.utils.v2.dbconnection;

import java.sql.Connection;
import com.innovaccer.utils.v2.Config;
import com.innovaccer.utils.v2.LoggerUtils;

public class DBManager {
	
	private Config configInstance;
    private SQLDBManager sqlDBInstance;
    private MongoDBManager mongoDBInstance;
    
    public DBManager() {
        init(Config.getConfig());
    }

    public DBManager(Config testConfig) {
        init(testConfig);
    }

    private void init(Config testConfig) {
        this.configInstance = testConfig;
    }
    
    public SQLDBManager getsqlDBInstance() {
    	if(sqlDBInstance==null) {
    		this.sqlDBInstance = new SQLDBManager(configInstance);
    	}
    		return this.sqlDBInstance;
    }
    
	 public MongoDBManager getMongoDBInstance() { 
		 if(mongoDBInstance==null)
			 this.mongoDBInstance = new MongoDBManager(configInstance);
		
		 return this.mongoDBInstance; 
	}
	
}
