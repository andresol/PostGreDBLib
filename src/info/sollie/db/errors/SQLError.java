/**
 * 
 */
package info.sollie.db.errors;

/**
 * Error that is thrown if something goes wrong under normal run 
 * and is executed on the server.
 * 
 * @author Andre Sollie
 *
 */
public class SQLError extends RuntimeException {
	
	private static final long serialVersionUID = -8739806576420056598L;

	private final String sql;
	
	public SQLError(String sql, String message, Throwable cause) {
		super(message, cause);
		this.sql = sql;
	}
	
	public SQLError(String message, Throwable cause) {
		super(message, cause);
		this.sql = "";
	}

	public SQLError(String message) {
		super(message);
		this.sql = "";
	}

	public SQLError(Throwable cause) {
		super(cause);
		this.sql = "";
	}
	
	public SQLError() {
		this.sql = "";
	}
	
	public String toString() {
		return "Sql that failed :" + sql + ". Error " + super.toString();
	}
	
}
