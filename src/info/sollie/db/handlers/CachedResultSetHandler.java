/**
 * 
 */
package info.sollie.db.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.rowset.CachedRowSet;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.log4j.Logger;

import com.sun.rowset.CachedRowSetImpl;

/**
 * Converts a ResultSet to a offline cacheable resultet. It does not need to be closed.
 * 
 * @author Andre Sollie
 *
 */
public class CachedResultSetHandler implements ResultSetHandler<ResultSet> {

	private final static Logger logger = Logger.getLogger(CachedResultSetHandler.class); 
	
	@Override
	public ResultSet handle(final ResultSet result) throws SQLException {
		CachedRowSet crm = null;
		try {
			crm = new CachedRowSetImpl();
			crm.populate(result);
		} catch (SQLException e1) {
			logger.error("Could not create a Prepared statement. " + e1.getMessage());
		} finally {
			DbUtils.close(result);
		}
		return crm;
	}

}
