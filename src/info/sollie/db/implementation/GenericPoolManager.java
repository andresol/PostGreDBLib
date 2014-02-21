package info.sollie.db.implementation;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import info.sollie.db.Nullable;
import info.sollie.db.interfaces.ConnectionManager;

import org.apache.log4j.Logger;

public class GenericPoolManager implements ConnectionManager {
	
	private static final Logger logger = Logger.getLogger(GenericPoolManager.class); 
    
    private final DataSource dataSource;
    
	private final boolean connected;
	
    public GenericPoolManager(DataSource dataSource) {
    	if (dataSource != null) {
			this.connected = true;
		} else {
			this.connected = false;
		}
    	this.dataSource = dataSource;
    }
    
    public DataSource getDataSource() {
        return this.dataSource;
    }
    
    @Nullable
    public Connection getConnection() {
    	try {
			Connection connection = this.dataSource.getConnection();
			if (connection.isClosed()) { //try another. Needs only check tow.
				connection = this.dataSource.getConnection();
				logger.warn("We got a closed connection from the datasource. Another one was request. State was " + connection.isClosed());
			}
			return connection;
		} catch (SQLException e) {
			logger.error("Could not get connection.", e);
		}
		return null;
    }

	@Override
	public boolean connected() {
		return this.connected;
	}

	@Override
	public String getServer() {
		return "";
	}
    
}
