package info.sollie.db.interfaces;


import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.SQLException;

/**
 * Interface for a class that is to be a database retriver. It must be able to 
 * retrive any {@link Retrievable} objects.
 *  
 * @author Andre Sollie
 *
 */
public interface Retriver {

	/**
	 * Create object for many to many relations.
	 * @param annotation
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public <E> void createObjectForManyToManyRelation(E e, Field field, Annotation annotation) throws InstantiationException, IllegalAccessException;
	
	/**
	 * Create object for one to many relations. 
	 * 
	 * @param <E>
	 * @param e
	 * @param field
	 * @param annotation
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public <E> void createObjectForOneToManyRelation(E e, Field field,	Annotation annotation) throws InstantiationException, IllegalAccessException;
	
	/**
	 * Set objects that are as ForeignKey in the database. It will not follow these objects an create the complex objects here. It means
	 * that other ForeignKey, OneToMany and ManyToMany relations will not be created.
	 * 
	 * @param <E> that is retrievable.
	 * @param field to be retrieved.
	 * @param object that is the object to be retrieved.
	 * @param e the field to be set.
	 * @return a retrieved object.
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public <E extends Retrievable> Object setForeignObject(Field field, Object object, E e, boolean follow) throws IllegalAccessException, InstantiationException;
	
	/**
	 * @param <E>
	 * @param field
	 * @param object
	 * @param e
	 * @return
	 * @throws IllegalAccessException
	 */
	public <E> Object setBigInt(Field field, Object object, E e) throws IllegalAccessException;
	
	/**
	 * Set the boolean field. 
	 * 
	 * @param <E>
	 * @param field 
	 * @param object
	 * @param e
	 * @return
	 * @throws IllegalAccessException
	 */
	public <E> Object setBoolean(Field field, Object object, E e) throws IllegalAccessException;
	
	/**
	 * Set the clob field.
	 * 
	 * @param <E>
	 * @param field
	 * @param object
	 * @param e
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 * @throws IllegalAccessException
	 */
	public <E> Object setClob(Field field, Object object, E e)	throws IOException, SQLException, IllegalAccessException;
	
	/**
	 * Set a varchar object.
	 * 
	 * @param <E>
	 * @param field
	 * @param object
	 * @param e
	 * @throws IllegalAccessException
	 */
	public <E> Object setVarchar(Field field, Object object, E e) throws IllegalAccessException;
	
	/**
	 * Set a timestamp object.
	 * 
	 * @param <E>
	 * @param field
	 * @param object
	 * @param e
	 * @return
	 * @throws IllegalAccessException
	 */
	public <E> Object setTimeStamp(Field field, Object object, E e) throws IllegalAccessException;
	
	/**
	 * @param <E>
	 * @param field
	 * @param object
	 * @param e
	 * @return
	 * @throws IllegalAccessException
	 */
	public <E> Object setNumericAndDecimal(Field field, Object object, E e) throws IllegalAccessException;
	
	/**
	 * Set a database double to a field.
	 * 
	 * @param <E>
	 * @param field
	 * @param object
	 * @param e
	 * @return
	 * @throws IllegalAccessException
	 */
	public <E> Object setDouble(Field field, Object object, E e) throws IllegalAccessException;
	
	/**
	 * Set a database double to a field.
	 * 
	 * @param <E>
	 * @param field
	 * @param object
	 * @param e
	 * @return
	 * @throws IllegalAccessException
	 */
	public <E> Object setInteger(Field field, Object object, E e) throws IllegalAccessException;
	
	public String getDateTimeFormat();
	
}

