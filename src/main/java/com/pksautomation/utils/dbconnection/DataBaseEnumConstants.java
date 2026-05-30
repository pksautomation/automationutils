package com.innovaccer.utils.dbconnection;

public class DataBaseEnumConstants {
	
	/**
	 * For RDBMS Database
	 * @author pramod.singh
	 *
	 */
	public enum DatabaseType
	{
		MNA("mna"),READ_FROM_CONFIG("read_from_config");

		public final String values;

		DatabaseType(final String value){
			this.values = value;
		}
	};
	
	/**
	 * Enum for For No-SQL database
	 * @author I0465
	 *
	 */
	public enum MongoDataBaseType{
		MongoRisk(1);
		public final int values;
		MongoDataBaseType(final int value){
			this.values=value;
		}
	};
	
	
	/**
	 * Enum for DataBase category like SQL, Greenplum
	 * @author I0465
	 *
	 */
	public enum DataBaseCategory{
		GreenplumDB(1),SQLDB(2);
		public final int values;
		DataBaseCategory(final int value){
			this.values=value;
		}
	};
}
