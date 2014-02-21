package info.sollie.db;

import java.sql.Connection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import info.sollie.db.implementation.GenericPoolManager;
import info.sollie.db.implementation.GenericTransactionalPoolManager;
import info.sollie.db.interfaces.ConnectionManager;
import info.sollie.db.interfaces.ConnectionPoolManager;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;

public class ConnectionManagers {
	
	//private static final SystemPropertyManager properymanager = new SystemPropertyManager(new ClassPathResource("db.properties"), false);
	private final static String prefix = "sqlserver";
	
	private ConnectionManagers() {
	}

	public final static ConnectionManager getPoolManager() {
		ConnectionManager result = null;
		String lookupstring = "";//properymanager.getProperty("datasource.string");
		boolean connected = false;
		if (lookupstring != null && !lookupstring.equals("")) {
			result = new JNDIPoolManager(lookupstring);
			connected = result.connected();
		}
		
		if (connected == false) {
			String server = "";//properymanager.getProperty(prefix + ".server");
			String username = ""; // properymanager.getProperty(prefix + ".username");
			String password = ""; //properymanager.getProperty(prefix + ".password");
			result = new DatabaseDriverPoolManager(username, password, server, 30);
			connected = result.connected();
		}

		if (connected) {
			return result;
		} else {
			return null;
		}
	}
	
	private static class DatabaseDriverPoolManager implements ConnectionPoolManager {

		private static final Logger logger = Logger.getLogger(DatabaseDriverPoolManager.class); 
		
		public final GenericPoolManager genericPoolManager;
		
		private final GenericObjectPool connectionPool;
		
		private final PoolingDataSource dataSource;
		
		private final String server;
		
		/**
		 * Creates a Connection pool for a choosen driver. 
		 * @param username to the database.
		 * @param password to the database.
		 * @param server connection url.
		 * @param maxActive connections.
		 */
	    public DatabaseDriverPoolManager(String username, String password, String server, int maxActive){
	    	 try {
	    	 		Class.forName(JDBCDrivers.getDatabaseDriver());
	    	} catch (ClassNotFoundException e) {
	    			logger.error("Could not load the db driver. Please put it in classpath.", e);
	    	}
	    	this.connectionPool = new GenericObjectPool(null);
	    	connectionPool.setMaxActive(maxActive);
	    	this.server = server;
			ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(server, username, password); 
			@SuppressWarnings("unused")
			PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, connectionPool,
					null, null, false, true);
			this.dataSource = new PoolingDataSource(connectionPool);
	        this.genericPoolManager = new GenericTransactionalPoolManager(dataSource);
	    }

		@Override
		public Connection getConnection() {
			return this.genericPoolManager.getConnection();
		}

		@Override
		public boolean connected() {
			return this.genericPoolManager.connected();
		}

		@Override
		public int getActiveConnections() {
			return this.connectionPool.getNumActive();
		}

		@Override
		public int getMaxConnections() {
			return this.connectionPool.getMaxActive();
		}

		@Override
		public DataSource getDataSource() {
			return this.dataSource;
		}

		@Override
		public String getServer() {
			return this.server;
		}

	}
	
	/**
	 * Pool configured by the Web server
	 * @author Andre Sollie
	 */
	private static class JNDIPoolManager implements ConnectionManager {
		
		public final GenericPoolManager genericPoolManager;
		
		private final String server; 
		
		private DataSource dataSource = null;
		
		private static final Logger logger = Logger.getLogger(JNDIPoolManager.class); 
		
		public JNDIPoolManager(String lookupstring) {
			Context ic = null;
			try {
				ic = new InitialContext();
			} catch (NamingException e) {
				logger.error("Could not init context", e);
			}
				
			try {
				if (ic != null) {
					this.dataSource = (DataSource) ic.lookup(lookupstring);
				} else {
					throw new NullPointerException("InitialContext cannot be null. Please check context");
				}
			} catch (NamingException e) {
				logger.error("Check the datasource.string in db.props.");
			}
			this.server = lookupstring;
			this.genericPoolManager = new GenericTransactionalPoolManager(dataSource);
		}

		@Override
		public Connection getConnection() {
			return this.genericPoolManager.getConnection();
		}
		
		@Override
		public boolean connected() {
			return this.genericPoolManager.connected();
		}

		@Override
		public DataSource getDataSource() {
			return this.dataSource;
		}

		@Override
		public String getServer() {
			return this.server;
		}
	}
}
