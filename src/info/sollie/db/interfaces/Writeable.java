package info.sollie.db.interfaces;
/**
 * Help a object to be written to a persistent store.
 * 
 * @author Andre Sollie
 *
 */
public interface Writeable {
	
	/**
	 * Write the object.
	 */
	public void save(boolean follow);
	
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
    
    /**
     * Deletes a writeable from the database.
     * 
     */
    public void delete();
}
