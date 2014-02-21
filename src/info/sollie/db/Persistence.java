/**
 * 
 */
package info.sollie.db;


import java.io.Serializable;

import info.sollie.db.interfaces.Retrievable;
import info.sollie.db.interfaces.Writeable;
import info.sollie.db.store.PersistentStore;
import info.sollie.db.store.PersistentStores;

import org.apache.log4j.Logger;

/**
 * Abstract class for easy handling of object that is to be persistent stored. 
 * 
 * @author Andre Sollie 
 *
 */
public abstract class Persistence<E extends Retrievable> implements Retrievable, Writeable, Serializable,
Comparable<E>  {
	
	/**
	 * Unique serial version UID.
	 */
	private static final long serialVersionUID = 4662029422895286162L;
	
	private static final Logger logger = Logger.getLogger(Persistence.class);
	
	private static final PersistentStore mssqlStore = PersistentStores.getMssqlStore(); 
	
	public Persistence (int id) {
		this.setID(id);
	}
	
	public Persistence () {
	}
	
	@Override
	public void populateObject(boolean follow) {
		String objectID = this.getID();
		if (objectID != null) {
			try {
				int id = Integer.valueOf(objectID);
				this.setObjectById(id, follow);
			} catch (NumberFormatException e) {
				logger.error("Not possible to populateObject. ID is not a number.", e);
			}
		}
	}

	/**
	 * @param id
	 */
	private void setObjectById(int id, boolean follow) {
		if (id > 0 ) {
			mssqlStore.getObject(this, follow);
		}
	}
	
	/**
	 * @param follow is ignored.
	 */
	@Override
	public void save(boolean follow) {
		mssqlStore.storeObject(this);
	}
	
	@Override
	public void delete() {
		mssqlStore.deleteObject(this.getID(), this.getClass());
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(E o) {
		if(this.getID() == null) {
			return -1;
		} else if (o == null || o.getID() == null) {
			return 1;
		} else {
			return this.getID().compareTo(o.getID());
		}
	}

	
}
