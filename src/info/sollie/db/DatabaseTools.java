/**
 * 
 */
package info.sollie.db;

import info.sollie.db.implementation.DefaultDatabaseTool;
import info.sollie.db.interfaces.DatabaseTool;



/**
 * Class for easy access of objects in a spesific database. This contains tools for accessing objects easy in MSSQL databases.
 *  
 * @author Andre Sollie 
 * 
 */
public final class DatabaseTools {

	public static final DatabaseTool DB = DefaultDatabaseTool.getInstance();
	/**
	 * Prevent construction of DatabaseTool.
	 */
	private DatabaseTools() {	
	}
	
	/**
	 * Get a DatabaseTool for a Object.˝
	 * 
	 * @param <E> the class to do work on.
	 * @return the database tool for a specific class.
	 */
	public static <E> DatabaseTool getMssqlDatabaseTool() {
		return DatabaseTools.DB;
	}
}
