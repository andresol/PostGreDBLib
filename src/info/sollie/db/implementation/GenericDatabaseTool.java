/**
 * 
 */
package info.sollie.db.implementation;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Generic database tools used in common situations.
 * 
 * @author Andre Sollie
 *
 */
public class GenericDatabaseTool {

	protected GenericDatabaseTool(){
	}

	/**
	 * Close a statement. Will check for null.
	 * @param statement to close.
	 * @throws SQLException error if something went wrong.
	 */
	public static final void closeStatement(Statement statement) throws SQLException {
		if (statement != null) {
			statement.close();
		}
	}

	/**
	 * Close a statement silent. If anything is wrong nothing happens.
	 * @param statement to close.
	 */
	public static final void closeStatementSilent(Statement statement) {
		try {
			GenericDatabaseTool.closeStatement(statement);
		} catch (SQLException e) {
			// Do nothing. Silent close.
		}
	}

	/**
	 * Close a statement silent. If anything is wrong nothing happens.
	 * @param statement to close.
	 */
	public static final void close(final Closeable... closeable) {
		if (closeable != null && closeable.length > 0) {
			for(int i = 0; i < closeable.length; i++) {
				try {
					closeable[i].close();
				} catch (Exception e) {
					// Do nothing. Silent close.
				}
			}
		}
	}

	/**
	 * Close a preparedStatement. Will check for null.
	 * @param preparedStatement to close.
	 * @throws SQLException error if something went wrong.
	 */
	public static final void closePreparedStatement(PreparedStatement preparedStatement) throws SQLException {
		if (preparedStatement != null) {
			preparedStatement.close();
		}
	}

	/**
	 * Close a preparedStatement silent. If anything is wrong nothing happens.
	 * @param preparedStatement to close.
	 */
	public static final void closePreparedStatementSilent(PreparedStatement preparedStatement) {
		try {
			GenericDatabaseTool.closeStatement(preparedStatement);
		} catch (SQLException e) {
			// Do nothing. Silent close.
		}
	}

	/**
	 * Close a connection. Will check for null.
	 * @param connection the statement to close.
	 * @throws SQLException error if something went wrong.
	 */
	public static void closeConnection(Connection connection) throws SQLException {
		if (connection != null) {
			connection.close();
		}
	}

	/**
	 * Close a connection silent. If anything is wrong nothing happens.
	 * @param connection to close.
	 */
	public static final void closeConnectionSilent(Connection connection) {
//		Context context = Contexts.getContext(); //XXX:Whole method must be rewritten to interface.
//		if (context != null && context instanceof PersistentContext) {
//			PersistentContext persistentContext = (PersistentContext) context;
//			if (!persistentContext.autoCommit()) {
//				return; // Skip close connection if not autocommit.
//			}
//		}
//		try {
//			GenericDatabaseTool.closeConnection(connection);
//		} catch (SQLException e) {
//			// Do nothing. Silent close.
//		}
	}

	/**
	 * Close a resultSet. Will check for null.
	 * @param resultSet to close.
	 * @throws SQLException error if something went wrong.
	 */
	public static final void closeResultSet(ResultSet resultSet) throws SQLException {
		if (resultSet != null) {
			resultSet.close();
		}
	}

	/**
	 * Close a resultSet silent. If anything is wrong nothing happens.
	 * @param resultSet to close.
	 */
	public static final void closeResultSetSilent(ResultSet resultSet) {
		try {
			GenericDatabaseTool.closeResultSet(resultSet);
		} catch (SQLException e) {
			// Do nothing. Silent close.
		}
	}

	/**
	 * Close result set, prepared statement and connection in silent in the right order.
	 * @param resultSet to close silent.
	 * @param preparedStatement to close silent.
	 * @param connection to close silent.
	 */
	public static final void closeSilent(ResultSet resultSet, Statement statement, Connection connection) {
		GenericDatabaseTool.closeResultSetSilent(resultSet);
		GenericDatabaseTool.closeStatementSilent(statement);
		GenericDatabaseTool.closeConnectionSilent(connection);
	}

	/**
	 * Close result set, prepared statement and connection in silent in the right order.
	 * @param resultSet to close silent.
	 * @param preparedStatement to close silent.
	 */
	public static final void closeSilent(ResultSet resultSet, Statement statement) {
		GenericDatabaseTool.closeResultSetSilent(resultSet);
		GenericDatabaseTool.closeStatementSilent(statement);
	}
}
