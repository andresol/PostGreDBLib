/**
 * 
 */
package info.sollie.db.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import info.sollie.db.interfaces.Retrievable;
import info.sollie.db.interfaces.Retriver;

import org.apache.commons.dbutils.ResultSetHandler;

/**
 * 
 * Get a single object form the ResultSet. It uses annotations and reflections. 
 * 
 * Important if resultset contains multiple values. Only the first will be returned. 
 * 
 * @author Andre Sollie
 *
 */
public class AnnotationObjectHandler<T extends Retrievable> implements ResultSetHandler<T> {

	private final AnnotationObjectListHandler<T> resultSetHandler; 
	
	private final Retriver retriver;
	
	private final Class<T> clazz;
	
	public AnnotationObjectHandler(final Class<T> type, final boolean follow, Retriver retriver) {
		this.resultSetHandler = new AnnotationObjectListHandler<T>(type, follow, retriver);
		this.retriver = retriver;
		this.clazz = type;
	}
	
	@Override
	public T handle(ResultSet rs) throws SQLException {
		List<T> result = this.resultSetHandler.handle(rs);
		if (!result.isEmpty()) {
			return result.get(0);
		} else {
			return null;
		}
	}

	/**
	 * @return the retriver
	 */
	public Retriver getRetriver() {
		return retriver;
	}

	/**
	 * @return the clazz
	 */
	public Class<T> getClazz() {
		return clazz;
	}


}
