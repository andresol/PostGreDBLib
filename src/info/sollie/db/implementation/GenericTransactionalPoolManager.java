package info.sollie.db.implementation;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

public class GenericTransactionalPoolManager extends GenericPoolManager {
	

	public GenericTransactionalPoolManager(final DataSource dataSource) {
		super(dataSource);
	}

	/* (non-Javadoc)
	 * @see info.sollie.db.implementation.GenericPoolManager#getConnection()
	 */
	@Override
	public Connection getConnection() {
//		Context context = Contexts.getContext();
//		if (context instanceof PersistentContext) {
//			PersistentContext persistentContext = (PersistentContext) context;
//			if (!persistentContext.autoCommit()) {
//				Connection connection = persistentContext.getConnection();
//				if (connection != null) {
//					return connection;
//				} else {
//					connection = super.getConnection();
//					if (connection != null) {
//						try {
//							connection.setAutoCommit(false);
//						} catch (SQLException e) {
//							//Skip.
//						}
//					}
//					persistentContext.setConnection(connection);
//					return connection;
//				}
//			} else {
//				return super.getConnection(); 
//			}
//		} else {
//			return super.getConnection();
//		}
		return super.getConnection();
	}
}
