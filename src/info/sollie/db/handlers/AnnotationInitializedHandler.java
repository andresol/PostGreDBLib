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
 * @author Andre Sollie
 *
 */
public class AnnotationInitializedHandler<T extends Retrievable> implements ResultSetHandler<T> {

	private final ResultSetHandler<List<T>> resultSetHandler; 
	

	public  AnnotationInitializedHandler(final T t, final boolean follow, Retriver retriver) {
		this.resultSetHandler = new AnnotationObjectListHandler<T>(t, follow, retriver);
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


}
