package info.sollie.db;

import info.sollie.db.interfaces.JDBCDriver;

public class JDBCDrivers {
	
	private final static JTDSDriver JTDSDRIVER = new JTDSDriver();
	
	private JDBCDrivers() {
	}
	
	public final static String getDatabaseDriver() {
		return JTDSDRIVER.getDatabaseDriver();
	}
	
	private static final class JTDSDriver implements JDBCDriver {
		
		private JTDSDriver(){
		}
		
		@Override
		public String getDatabaseDriver() {
			return "net.sourceforge.jtds.jdbc.Driver";
		}
	}
}
