/**
 * 
 */
package info.sollie.db.interfaces;

import java.sql.ResultSet;
import java.util.List;

import info.sollie.db.annotations.ForeignKey;
import info.sollie.db.annotations.ManyToMany;
import info.sollie.db.annotations.OneToMany;

import org.apache.commons.dbutils.QueryRunner;

/**
 * Database tools for a specific class. It provides easy access to objects in a database.
 * 
 * @author Andre Sollie
 */
public interface DatabaseTool {

	
	/**
	 * Get all objects from the database. It should retrieve all information and put it into the database. It is important
	 * that the name in the database and the name in the class matches. It is not case sensitive. It will default follow
	 * complex objects.
	 * 
	 * This method will close the prepared statement and the connection.
	 * 
	 * @param The SQL query to be executed..
	 * @param clazz to be retrieved.
	 * @return of all classes that is retrieved.
	 */
	public <E extends Retrievable> List<E> getObjects(String sql, Class<E> clazz);
	
	/**
	 * Get a single object from the database. It should retrieve all information and put it into the database. It is important
	 * that the name in the database and the name in the class matches. It is not case sensitive. It will follow complex objects.
	 * 
	 * This method will close the prepared statement and the connection.
	 * 
	 * @param The SQL query to be executed.
	 * @param clazz to be retrieved.
	 * @return a instanced object.
	 */
	public <E extends Retrievable> E getObject(String sql, Class<E> clazz);
	
	/**
	 * Get a single object from the database. It should retrieve all information and put it into the database. It is important
	 * that the name in the database and the name in the class matches. It is not case sensitive. It will follow complex objects.
	 * 
	 * This method will close the prepared statement and the connection.
	 * 
	 * @param The SQL query to be executed.
	 * @param clazz to be retrieved.
	 * @param id of the object to be retrieved.
	 * @return a instanced object.
	 */
	public <E extends Retrievable> E getObject(Class<E> clazz, int id);
	
	/**
	 * Set a single object from the values. It should retrieve all information and put it into the database. It is important
	 * that the name in the database and the name in the class matches. It is not case sensitive. It will follow complex objects.
	 * 
	 * This method will close the prepared statement and the connection.
	 * 
	 * @param The SQL query to be executed.
	 * @param clazz to be retrieved.
	 * @return a instanced object.
	 */
	public <E extends Retrievable> void setObject(String sql, E e);
	
	/**
	 * Get all objects from the database. It should retrieve all information and put it into the database. It is important
	 * that the name in the database and the name in the class matches. It is not case sensitive.
	 * 
	 * This method will close the prepared statement and the connection.
	 * 
	 * @param The SQL query to be executed..
	 * @param clazz to be retrieved.
	 * @param follow complex objects as {@link ForeignKey}, {@link OneToMany} and {@link ManyToMany} 
	 * @return of all classes that is retrieved.
	 */
	public <E extends Retrievable> List<E> getObjects(String sql, Class<E> clazz, boolean follow);
	
	/**
	 * Get a single object from the database. It should retrieve all information and put it into the database. It is important
	 * that the name in the database and the name in the class matches. It is not case sensitive.
	 * 
	 * This method will close the prepared statement and the connection.
	 * 
	 * @param The SQL query to be executed.
	 * @param clazz to be retrieved.
	 * @param follow complex objects as {@link ForeignKey}, {@link OneToMany} and {@link ManyToMany} 
	 * @return a instanced object.
	 */
	public <E extends Retrievable> E getObject(String sql, Class<E> clazz, boolean follow);
	
	/**
	 * Get a single object from the database. It should retrieve all information and put it into the database. It is important
	 * that the name in the database and the name in the class matches. It is not case sensitive.
	 * 
	 * This method will close the prepared statement and the connection.
	 * 
	 * @param The SQL query to be executed.
	 * @param clazz to be retrieved.
	 * @param id of the object to be retrieved.
	 * @param follow complex objects as {@link ForeignKey}, {@link OneToMany} and {@link ManyToMany} 
	 * @return a instanced object.
	 */
	public <E extends Retrievable> E getObject(Class<E> clazz, int id, boolean follow);
	
	/**
	 * Set a single object from the values. It should retrieve all information and put it into the database. It is important
	 * that the name in the database and the name in the class matches. It is not case sensitive.
	 * 
	 * This method will close the prepared statement and the connection.
	 * 
	 * @param The SQL query to be executed.
	 * @param clazz to be retrieved.
	 * @param follow complex objects as {@link ForeignKey}, {@link OneToMany} and {@link ManyToMany} 
	 * @return a instanced object.
	 */
	public <E extends Retrievable> void setObject(String sql, E e, boolean follow);
	
	/**
	 * This will help write a object to a persistent store. 
	 * 
	 * 
	 * @param The SQL query to be executed.
	 * @param clazz to be retrieved.
	 * @return a instanced object.
	 */
	public <E extends Writeable> void storeObject(String sql, E e);
	
	/**
	 * This will remove a object from the database. 
	 * 
	 * 
	 * @param The SQL query to be executed.
	 * @param clazz to be retrieved.
	 * @return a instanced object.
	 */
	public <E extends Writeable> void write(String sql);

	/**
	 * Read from the database. It will return a cached result set. This result set is closed
	 * and does not keep connection to the database.
	 * 
	 * @param sql to be executed.
	 * @return a cached result set.
	 */
	public <E extends Writeable> ResultSet read(CharSequence sql);
	
	/**
	 * Read from the database. It will first create a prepared statement before doing the read.
	 * 
	 * @param sql to be executed. It must contain the amount ? as the length of the objects.
	 * @return a cached result set.
	 */
	public <E extends Retrievable> ResultSet read(CharSequence sql, Object... objects);
	
	public <E extends Retrievable> List<E> getObjects(ResultSet resultSet, Class<E> clazz, boolean follow);
	
	public <E extends Retrievable> E getObject(ResultSet resultSet, Class<E> clazz, boolean follow);
	
	/**
	 * @param sql that is update/insert or both
	 * @param objects that is to be used in store as parameters to the sql..
	 * @return number of rows updated.
	 */
	public int write(String sql, Object... objects);
	
	public int writeGetID(String sql, Object... objects);
	
	public QueryRunner getQueryRunner();
	
	public Retriver getRetriver();

	public <E extends Retrievable> E getObject(String sql, Class<E> clazz, Object... objects);

	public <E extends Retrievable> List<E> getObjects(String sql, Class<E> class1, Object... objects);

	public <E> E getValue(final String sql, final Class<E> clazz, final String column, final Object... objects);
	
	public List<Object> getValues(final String sql, final Object... objects);
	
	public String getServer();
}
