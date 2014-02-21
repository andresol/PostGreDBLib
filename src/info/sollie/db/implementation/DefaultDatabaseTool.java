/**
 * 
 */
package info.sollie.db.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import info.sollie.db.ConnectionManagers;
import info.sollie.db.Nullable;
import info.sollie.db.errors.SQLError;
import info.sollie.db.handlers.AnnotationInitializedHandler;
import info.sollie.db.handlers.AnnotationObjectHandler;
import info.sollie.db.handlers.AnnotationObjectListHandler;
import info.sollie.db.handlers.CachedResultSetHandler;
import info.sollie.db.interfaces.ConnectionManager;
import info.sollie.db.interfaces.DatabaseTool;
import info.sollie.db.interfaces.Retrievable;
import info.sollie.db.interfaces.Retriver;
import info.sollie.db.interfaces.Writeable;
import info.sollie.db.mssql.MssqlTool;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayHandler;

import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.Logger;

/**
 * @author Andre Sollie
 *
 */
public class DefaultDatabaseTool implements DatabaseTool {

	private final ConnectionManager poolManager;

	private final QueryRunner queryRunner;

	private static final DatabaseTool instance = new DefaultDatabaseTool();
	
	private final boolean isTest;
	
	protected DefaultDatabaseTool(){
		this.poolManager = ConnectionManagers.getPoolManager();
		 //isTest = SystemManagerService.getSystemManager().isTestEnvironment();
		isTest = false;
		if (isTest) {
			this.queryRunner = new PerformanceQueryRunner(this.poolManager.getDataSource());
		} else {
			this.queryRunner = new QueryRunner(this.poolManager.getDataSource());
		}
	}

	/** Log what errors and other useful things about this class */
	private final static Logger logger = Logger.getLogger(MssqlTool.class); 


	/**
	 * {@inheritDoc}
	 */
	@Override
	@Nullable
	public <E extends Retrievable> E getObject(String sql, Class<E> clazz, boolean follow) {
		try {
			return this.queryRunner.query(sql, new AnnotationObjectHandler<E>(clazz, follow, this.getRetriver()));
		} catch (SQLException e) {
			logger.error("Could not create a Prepared statement. " + e.getMessage());
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Nullable
	public <E extends Retrievable> List<E> getObjects(String sql, Class<E> clazz, boolean follow) {
		try {
			return this.queryRunner.query(sql, new AnnotationObjectListHandler<E>(clazz, follow, this.getRetriver()));
		} catch (SQLException e) {
			logger.error("Could not create a Prepared statement. " + e.getMessage());
		}
		return Collections.emptyList();
	}

	@Override
	public <E extends Retrievable> void setObject(String sql, E e, boolean follow) {
		try {
			if (e != null) {
				this.queryRunner.query(sql, new AnnotationInitializedHandler<E>(e, follow, this.getRetriver()));
			}
		} catch (SQLException e1) {
			logger.error("Could not create a Prepared statement. " + e1.getMessage());
		} 
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public <E extends Retrievable> E getObject(Class<E> clazz, int id, boolean follow) {
		Retrievable r = null;
		try {
			r = (Retrievable) clazz.newInstance();
			r.setID(id);
			r.populateObject(follow);
		} catch (InstantiationException e) {
			logger.error("Could not create a new object of " + clazz.getSimpleName(), e);
		} catch (IllegalAccessException e) {
			logger.error("Could not access class " + clazz.getSimpleName(), e);
		}
		return (E) r;
	}

	@Override
	public <E extends Writeable> void storeObject(final String sql, final E e) {
		final long start = System.currentTimeMillis();
		PreparedStatement preparedStatement = null;
		Connection connection = poolManager.getConnection();
		
		boolean newObject = sql.contains("INSERT");
		ResultSet resultSet = null;
		try {
			if (connection == null) {
				throw new SQLException("Got null from connection manager. Possible error with db or all connections used.");
			}
			preparedStatement = connection.prepareStatement(sql);
			if (newObject) {
				resultSet = preparedStatement.executeQuery();
				while(resultSet.next()) {
					int id = resultSet.getInt("ID");
					e.setID(id);
					if (logger.isDebugEnabled()) {
						logger.debug("Stores a object to the database. Sql: " + sql + " ID: " + id);
					}
				} 
			} else {
				preparedStatement.executeUpdate();
				logger.debug("Updates a object to the database. Sql: " + sql);
			}
		} catch (SQLException e1) {
			logger.error("Something wrong with the update. Sql: " + sql + " Message: "  + e1.getMessage(), e1);
			throw new SQLError(sql, e1.getMessage(), e1);
		} finally {
			GenericDatabaseTool.closeSilent(resultSet, preparedStatement, connection);
		}
		if (this.isTest) {
			final long stop = System.currentTimeMillis();
			//this.updateTime(stop - start);
		}
	}

	@Override
	@Nullable
	public <E extends Retrievable> E getObject(String sql, Class<E> clazz) {
		return this.getObject(sql, clazz, true);
	}

	@Override
	@Nullable
	public <E extends Retrievable> E getObject(Class<E> clazz, int id) {
		return this.getObject(clazz, id, true);
	}

	@Override
	public <E extends Retrievable> List<E> getObjects(String sql, Class<E> clazz) {
		return this.getObjects(sql, clazz, true);
	}

	@Override
	public <E extends Retrievable> void setObject(String sql, E e) {
		this.setObject(sql, e, true);
	}

	@Override
	public <E extends Writeable> void write(String sql) {
		final long start = System.currentTimeMillis();
		PreparedStatement preparedStatement;
		Connection connection = poolManager.getConnection();
		try {
			if (connection == null) {
				throw new SQLException("Got null from connection manager. Possible error with db or all connections used.");
			}
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.executeUpdate();
		} catch (SQLException e1) {
			logger.error("Could not create a prepared statement. " + e1.getMessage());
		} finally {
			GenericDatabaseTool.closeConnectionSilent(connection);
		}
		if (this.isTest) {
			final long stop = System.currentTimeMillis();
			//this.updateTime(stop - start);
		}
	}

	/**
	 * Implementation of the DatabaseTool read. It is important to know that CachedRowSetImpl() is 
	 * not the best way to implement it.
	 * 
	 * @see DatabaseTool#read(String)
	 */
	@Override
	@Nullable
	public <E extends Writeable> ResultSet read(final CharSequence sql) {
		return this.read(sql, new Object[] {});
	}

	@Nullable
	public <E extends Retrievable> ResultSet read(final CharSequence sql, Object... objects) {
		if (sql == null) {
			throw new NullPointerException("SQL cannot be null.");
		}
		ResultSet result = null;
		try {
			result = this.queryRunner.query(sql.toString(), new CachedResultSetHandler(), objects);
		} catch (SQLException e1) {
			logger.error("Error with the SQL. Message was " + e1.getMessage());
		} 
		return result;
	}

	public <E extends Retrievable> List<E> getObjects(ResultSet resultSet, Class<E> clazz, boolean follow) {
		if (logger.isTraceEnabled()) {
			logger.trace("Trying to get objects of type: " + clazz.getClass().getSimpleName() + " from a resultset." );
		}
		if (resultSet == null) {
			throw new IllegalArgumentException("ResultSet cannot be null");
		}
		List<E> result = Collections.emptyList();
		try {
			result = new AnnotationObjectListHandler<E>(clazz, follow, this.getRetriver()).handle(resultSet);
		} catch (SQLException e1) {
			logger.error("Some thing wrong with the SQL query. Message: " + e1.getMessage());
		} 

		if (logger.isTraceEnabled()) {
			logger.trace("PreparedStatement contained " + result.size() + " elements" );
		}
		return (result.size() != 0 ? result : Collections.<E>emptyList());
	}

	@Nullable
	public <E extends Retrievable> E getObject(ResultSet resultSet, Class<E> clazz, boolean follow) {
		if (logger.isTraceEnabled()) {
			logger.trace("Trying to get object of type: " + clazz.getSimpleName() + " from a result set." );
		}
		if (resultSet == null) {
			throw new IllegalArgumentException("Resultset cannot be null");
		}
		E e = null;
		try {
			e = new AnnotationObjectHandler<E>(clazz, follow, this.getRetriver()).handle(resultSet);
		} catch (SQLException e1) {
			logger.error("Some thing wrong with the SQL query. Message: " + e1.getMessage());
		} finally {
			GenericDatabaseTool.closeResultSetSilent(resultSet);
		}
		if (logger.isTraceEnabled()) {
			if (e != null) {
				logger.trace("This object was retrived. " + e.toString());
			} else {
				logger.trace("This was not found.");
			}
		}
		return e;
	}

	public int write(String sql, Object... objects) {
		final long start = System.currentTimeMillis();
		PreparedStatement preparedStatement = null;
		Connection connection = poolManager.getConnection();
		int updated = 0;
		try {
			preparedStatement = connection.prepareStatement(sql);
			int index = 1;
			for (Object object : objects) {
				preparedStatement.setObject(index, object);
				index++;
			}
			updated = preparedStatement.executeUpdate();
		} catch (SQLException e1) {
			logger.error("Could not create a Prepared statement. " + e1.getMessage());
		} finally {
			GenericDatabaseTool.closePreparedStatementSilent(preparedStatement);
			GenericDatabaseTool.closeConnectionSilent(connection);
		}
		if (this.isTest) {
			final long stop = System.currentTimeMillis();
			//this.updateTime(stop - start);
		}
		return updated;
	}

	public int writeGetID(String sql, Object... objects) {
		int autonumber = -1;
		try {
			ResultSet resultSet = this.queryRunner.query(sql + " SELECT SCOPE_IDENTITY()", new CachedResultSetHandler(), objects);
			if (resultSet != null) {
				while(resultSet.next()) {
					autonumber = resultSet.getInt(1);
				}
			}
		} catch (SQLException e1) {
			logger.error("Could not create a Prepared statement. " + e1.getMessage());
		} 
		return autonumber;
	}

	public QueryRunner getQueryRunner() {
		return this.queryRunner;
	}

	@Override
	public Retriver getRetriver() {
		return MssqlTool.getInstance();
	}

	@Override
	@Nullable
	public <E extends Retrievable> E getObject(String sql, Class<E> clazz, Object... objects) {
		if (sql == null) {
			throw new NullPointerException("SQL cannot be null.");
		}
		E e = null;
		try {
			e = this.queryRunner.query(sql, new AnnotationObjectHandler<E>(clazz, false, this.getRetriver()), objects);
		} catch (SQLException e1) {
			logger.error("Error with the SQL. Message was " + e1.getMessage());
		} 
		return e;
	}

	/**
	 * @return the instance
	 */
	public static DatabaseTool getInstance() {
		return instance;
	}

	@Override
	public <E extends Retrievable> List<E> getObjects(final String sql, final Class<E> clazz, final Object... objects) {
		if (logger.isTraceEnabled()) {
			logger.trace("Trying to get objects of type: " + clazz.getClass().getSimpleName() + " from a resultset." );
		}

		List<E> result = Collections.emptyList();
		try {
			result = this.queryRunner.query(sql, new AnnotationObjectListHandler<E>(clazz, false, this.getRetriver()), objects);
		} catch (SQLException e1) {
			logger.error("Some thing wrong with the SQL query. Message: " + e1.getMessage());
		} 

		if (logger.isTraceEnabled()) {
			logger.trace("PreparedStatement contained " + result.size() + " elements" );
		}
		return (result.size() != 0 ? result : Collections.<E>emptyList());
	}

	
	@Override
	@Nullable
	public <E> E getValue(final String sql, final Class<E> clazz, final String column, final Object... objects) {
		if (logger.isTraceEnabled()) {
			logger.trace("Trying to get objects a single object from a column from a resultset." );
		}
		E result = null;
		try {
			result = this.queryRunner.query(sql, new ScalarHandler<E>(column), objects);
		} catch (SQLException e1) {
			logger.error("Some thing wrong with the SQL query. Message: " + e1.getMessage());
		} 

		if (logger.isTraceEnabled()) {
			logger.trace("Object returned was " + result + "." );
		}
		return result;
	}
	
	@Nullable
	public List<Object> getValues(final String sql, final Object... objects) {
		if (logger.isTraceEnabled()) {
			logger.trace("Trying to get objects a single array list from a column from a resultset." );
		}
		
		List<Object> result = null;
		try {
			Object[] objs = this.queryRunner.query(sql, new ArrayHandler(), objects);
			if(objs != null) {
				result = Arrays.asList(objs);
			}
		}
		catch(SQLException e1) {
			logger.error("Some thing wrong with the SQL query. Message: " + e1.getMessage());
		}
		
		if (logger.isTraceEnabled()) {
			logger.trace("Object returned was " + result + "." );
		}
		return result;
	}
	

	@Override
	public String getServer() {
		return this.poolManager.getServer();
	}
}
