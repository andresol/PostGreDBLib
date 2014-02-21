package info.sollie.db.interfaces;
import java.sql.Connection;

import javax.sql.DataSource;

/**
 * Manager class that handles the connection to a database server.
 * 
 * @author Andre Sollie
 */
public interface ConnectionManager {
    
	/**
	 * Get a Connections. Typical a pooled connection.
	 * @return a connection.
	 */
    public Connection getConnection();
    
    /**
     * @return true if connected else false.
     */
    public boolean connected();
    
    /**
     * @return get the datasouce used for connections.
     */
	public DataSource getDataSource();
	
	/** Name of the server */
	public String getServer();
}
