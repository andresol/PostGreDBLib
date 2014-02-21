package info.sollie.db.store;

import java.util.List;

import info.sollie.db.annotations.ForeignKey;
import info.sollie.db.interfaces.Retrievable;
import info.sollie.db.interfaces.Writeable;

/**
 * Interface to interact with object that is retrievable or Writable in an persistent store. It will
 * give easy access to the persistent store. 
 * 
 * @author Andre Sollie
 *
 */
public interface PersistentStore {
	
	/**
	 * Get an object from the the persistent store. It will follow complex objects.
	 * 
	 * @param <E> that is retrievable.
	 * @param clazz to be retrieved.
	 * @param id of the object to be retrieved.
	 * @return the a new object.
	 */
	public <E extends Retrievable> E getObject(Class<E> clazz, int id);
	
	/**
	 * Get an object from the persistent store that is instanced and contains an id. It will follow
	 * complext objects.
	 * 
	 * @param <E> that is retrievable.
	 * @param e that is instanced and contains and id that is in the database. 
	 * @return the objected retrieved from the persistent store.
	 */
	public <E extends Retrievable> E getObject(E e);
	
	/**
	 * Get an object from the the persistent store.
	 * 
	 * @param <E> that is retrievable.
	 * @param clazz to be retrieved.
	 * @param id of the object to be retrieved.
	 * @param follow complex objects as {@link ForeignKey}, {@link OneToMany} and {@link ManyToMany} 
	 * @return the a new object. If not found it will return null.
	 */
	public <E extends Retrievable> E getObject(Class<E> clazz, int id, boolean follow);
	
	/**
	 * Get an object from the persistent store that is instanced and contains an id.
	 * @param <E> that is retrievable.
	 * @param e that is instanced and contains and id that is in the database. 
	 * @param follow complex objects as {@link ForeignKey}, {@link OneToMany} and {@link ManyToMany} 
	 * @return the objected retrieved from the persistent store.
	 */
	public <E extends Retrievable> E getObject(E e, boolean follow);
	
	/**
	 * Store the object to the persistent store.
	 * 
	 * @param <E> that is writable.
	 * @param e that is to be written to the persistent store.
	 */
	public <E extends Writeable> void storeObject(E e);
	
	/**
	 * Stores multiple object to the persistent store. 
	 * 
	 * @param <E> that implements writable.
	 * @param e a list of writable objects.
	 */
	public <E extends Writeable> void storeObjects(List<E> e);
	
	/**
	 * Delete object from the persistent store. 
	 * 
	 * @param <E> that implements writable.
	 * @param e a list of writable objects.
	 */
	public <E extends Writeable> void deleteObject(String id, Class<E> clazz);
	
	/**
	 * Delete object from the persistent store. 
	 * 
	 * @param <E> that implements writable.
	 * @param e a list of writable objects.
	 */
	public <E extends Writeable> void deleteObject(int id, Class<E> clazz);
	
	/**
	 * Deletes object to the persistent store. 
	 * 
	 * @param <E> that implements writable.
	 */
	public <E extends Writeable> void deleteObject(E e);

	/**
	 * Get a list of objects by its id's. It is uses the database default order.
	 * 
	 * @param <E> that implements Retrievable.
	 */
	public <E extends Retrievable> List<E> getObjects(Class<E> clazz, int ...IDs);
	
	/**
	 * Get a list of objects by its id's. It is uses the database default order.
	 * 
	 * @param <E> that implements Retrievable.
	 * @param ordering true for asc, false for desc
	 */
	public <E extends Retrievable> List<E> getObjects(Class<E> clazz, boolean ordering, int ...IDs);
}
