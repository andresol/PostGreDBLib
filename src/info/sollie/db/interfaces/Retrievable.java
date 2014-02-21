package info.sollie.db.interfaces;

/**
 * Help a object to be retrieved from a persistent store.
 * 
 * @author Andre Sollie
 *
 */
public interface Retrievable {
	/**
	 * Set a retrievable object. 
	 * 
	 * @param follow complext objects.
	 */
    
	public void populateObject(boolean follow);
    /**
     * Get the ID of the object. 
     * @return the objects id.
     */
    public String getID();
    
    /**
     * Set the ID of the object.
     * @param id
     */
    public void setID(int id);
}
