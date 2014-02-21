/**
 * 
 */
package info.sollie.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import info.sollie.db.DatabaseTools;

import org.apache.log4j.Logger;
import org.joda.time.LocalDate;

/**
 * @author Andre Sollie
 *
 */
public class DatabaseUtils {
	
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private static final Logger logger = Logger.getLogger(DatabaseUtils.class);
	
	//Static method factory
	private DatabaseUtils(){
	}

	/**
	 * Get a int value from column with name sum. Typical use
	 * would be to rename a field with AS sum. If something
	 * is wrong it will return {@link=Integer#MIN_VALUE}.
	 * 
	 * @param resultSet that contains a sum field.
	 * @return the sum as integer. 
	 * 
	 */
	public static int getNumberFromSum(final ResultSet resultSet) {
		int result = Integer.MIN_VALUE;
		try {
			while (resultSet.next()) {
				result = resultSet.getInt("sum");
			}
		} catch (SQLException e) {
			// Skip. Will return null
		}
		return result;
	}
	
	/**
	 * Get a int value from column with name sum. Typical use
	 * would be to rename a field with AS sum. If something
	 * is wrong it will return {@link=Integer#MIN_VALUE}.
	 * 
	 * @param sql
	 * @return the sum as integer. 
	 * 
	 */
	public static int getNumberFromSum(final String sql) {
		return getNumberFromSum(sql, new Object[0]);
	}
	
	/**
	 * Get a int value from column with name sum. Typical use
	 * would be to rename a field with AS sum. If something
	 * is wrong it will return {@link=Integer#MIN_VALUE}.
	 * 
	 * @param sql
	 * @return the sum as integer. 
	 * 
	 */
	public static int getNumberFromSum(final String sql, Object... objects) {
		int result = Integer.MIN_VALUE;
		ResultSet resultSet;
		if (objects == null || objects.length == 0) {
			resultSet = DatabaseTools.DB.read(sql);
		} else {
			resultSet = DatabaseTools.DB.read(sql, objects);
		}
		try {
			while (resultSet.next()) {
				result = resultSet.getInt("sum");
			}
		} catch (SQLException e) {
			// Skip. Will return null
		}
		return result;
	}
	
	
	public static HashMap<String, Integer> getNumberFromSumRelated(final String sql) {
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		ResultSet resultSet = DatabaseTools.DB.read(sql);
		try {
			while (resultSet.next()) {
				String key = resultSet.getString(2);
				int value = resultSet.getInt(1);
				result.put(key, value);
			}
		} catch (SQLException e) {
			// Skip. Will return null
		}
		return result;
	}

	/**
	 * Get a List of Integer from a result set. It is important to know that it will only take the column 1 and return it as a list.
	 * If the values contains other than Integers it will just return a empty result. If any thing goes wrong a empty list will be 
	 * returned.
	 * 
	 * @param resultSet that must contain a integer at 1 column.
	 * @return a list of Integers.
	 */
	public static List<Integer> getListFromSingleIntegerResult(final ResultSet resultSet) {
		List<Integer> result = new ArrayList<Integer>();
		try {
			while (resultSet.next()) {
				result.add(resultSet.getInt(1));
			}
		} catch (SQLException e) {
			// Skip. Will return a empty list.
		} catch (Exception e) {
			// Skip. Will return a empty list.
		}
		return result;
	}
	
	public static String[][] resultSetToStringArray(String sql, int size) {
		String[][] result = new String[size][2];
		for (int i = 0 ; i < result.length ; i++) {
			result[i][0] = "";
			result[i][1] = "";
		}
		ResultSet resultSet = DatabaseTools.DB.read(sql);
		int i = 0;
		try {
			while (resultSet.next()) {
				result[i][0] = resultSet.getString(1);
				result[i][1] = resultSet.getString(2);
				i++;
			}
		} catch (SQLException e) {
			// Skip. Will return a empty list.
		} catch (Exception e) {
			// Skip. Will return a empty list.
		}
		return result;
	}

	public static String[] indexToSumArray(int top, String sql, boolean date) {
		String[] result = new String[top];
		for (int i = 0 ; i < result.length ; i++) {
			result[i] = "0";
		}
		ResultSet resultSet = DatabaseTools.DB.read(sql);
		try {
			while (resultSet.next()) {
				String value = resultSet.getString(1);
				int index = 0;
				String indexString = resultSet.getString(2);
				if (date) {
					index = DatabaseUtils.getIndexFromDate(indexString, top - 1);
				} else {
					index = Integer.valueOf(indexString);
				}
				if (index >= 0 && index < top) {
					result[index] = value;
				}
			}
		} catch (SQLException e) {
			logger.error("Error creating array.", e);
		} catch (Exception e) {
			logger.error("Error creating array.", e);
		}
		return result;
	}
	
	private static int getIndexFromDate(String date, int daysBack) {
		LocalDate localDate = new LocalDate(date.replace('.', '-'));
		LocalDate now = new LocalDate();
		for (int i = 0 ; i <= daysBack ; i++ ) {
			if (localDate.equals(now.minusDays(i))) {
				return i;
			}
		}
		return -1;
	}
	
	public static Date formatDate(String date, String mask) {
		Date dato = null;
		DateFormat formatter = new SimpleDateFormat(mask);
		try {
			dato = formatter.parse(date);
		} catch (ParseException e) {
			logger.warn("Could not parse date.", e);
		}
		return dato;
	}
	
	public static DateFormat getSqlDateFormater() {
		return  new SimpleDateFormat();
	}
}
