package info.sollie.db.interfaces;


/**
 * Handles connections to a database server. Every connections that
 * is returned is a pooled connection. It is important to close the 
 * connection if it is to be returned to the pool.
 * 
 * @author Andre Sollie
 *
 */
public interface ConnectionPoolManager extends ConnectionManager {
	
	/**
	 * @return maximum numbers of possible connections.
	 */
	public int getMaxConnections();
	
	/**
	 * @return the numbers of active connections to the database now.
	 */
	public int getActiveConnections();
	

}
