
package info.sollie.db.security;


public class MsSQLCodec {

	private MsSQLCodec() {
		
	}
	
	public static String escape(String value) {
		return "'''" + value + "'''";
	}
	
}
