/**
 * 
 */
package info.sollie.db.mssql;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import info.sollie.db.annotations.BigInt;
import info.sollie.db.annotations.Clob;
import info.sollie.db.annotations.DBBoolean;
import info.sollie.db.annotations.DBDouble;
import info.sollie.db.annotations.DBInteger;
import info.sollie.db.annotations.Decimal;
import info.sollie.db.annotations.ForeignKey;
import info.sollie.db.annotations.ManyToMany;
import info.sollie.db.annotations.Numeric;
import info.sollie.db.annotations.PrimaryKey;
import info.sollie.db.annotations.Real;
import info.sollie.db.annotations.TableName;
import info.sollie.db.annotations.Timestamp;
import info.sollie.db.annotations.Varchar;
import info.sollie.db.interfaces.Retrievable;
import info.sollie.db.interfaces.Writeable;

import org.apache.log4j.Logger;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;
import org.joda.time.base.BaseSingleFieldPeriod;


/**
 * Class to generate common used MSSQL query from a class. From the class it will find the
 * respective table. If the table name is not the same as the class use the TableName annotation.
 * 
 * @author Andre Sollie
 *
 */
public final class SqlGenerator {

	private final static Logger logger = Logger.getLogger(SqlGenerator.class);

	private final static String sep = " ";

	public static final String DATO_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String DATO_FORMAT = "yyyy-MM-dd";

	/**
	 * Static method factory
	 */
	private SqlGenerator() {
	}

	public static final <E extends Retrievable> String getRetrivable(final E e) {
		return SqlGenerator.getRetrivableByTablename(e, SqlGenerator.getIDName(e.getClass()));
	}

	protected static final <E extends Retrievable> String getRetrivableByTablename(final E e, final String idName) {
		return SqlGenerator.getRetrivableByTableAndID(e, idName, Integer.valueOf(e.getID()));
	}

	public static final <E extends Retrievable> String getRetrivableByTableAndID(final E e, final String idName, final int id) {
		StringBuffer result = new StringBuffer(128);
		result.append(Syntax.SELECT).append(" * ");
		result.append(Syntax.FROM).append(sep).append("[").append(SqlGenerator.getDatabaseName(e.getClass())).append("] ");
		result.append(Syntax.WHERE).append(sep).append(idName).append("=").append(id).append(sep);
		result.append(Syntax.ORDER).append(sep).append(Syntax.BY).append(sep).append(getIDName(e.getClass())).append(sep).append(Syntax.DESC);
		return result.toString();
	}

	protected static final <E> String getDatabaseName(final Class<?> clazz) {
		for (Annotation annotation : clazz.getDeclaredAnnotations()) {
			if (annotation instanceof TableName) {
				TableName tableName = (TableName) annotation;
				if (!tableName.tableName().equals("")) {
					return tableName.tableName();
				}
			}
		}
		return clazz.getSimpleName();
	}

	public static final <E> String getIDName(final Class<?> clazz) {
		String result = clazz.getSimpleName() + Syntax.ID.name();
		for (Annotation annotation : clazz.getDeclaredAnnotations()) {
			if (annotation instanceof PrimaryKey) {
				PrimaryKey primaryKey = (PrimaryKey) annotation;
				if (!primaryKey.id().equals("")) {
					return primaryKey.id();
				}
			}
		}
		return result;
	}

	public static final <E> String getRemoteID(final Class<?> mainClass, final Class<?> remoteClass) {
		String result = remoteClass.getSimpleName();
		for(Field field : mainClass.getDeclaredFields()){
			Class<?> type = field.getType();
			if (type.equals(remoteClass)) {
				for (Annotation annotation : field.getDeclaredAnnotations()) {
					if (annotation instanceof ForeignKey) {
						ForeignKey foreignKey = (ForeignKey) annotation;
						if (!foreignKey.id().equals("")) {
							return foreignKey.id();
						}
					}
				}
			}
		}

		return result;
	}

	public static final <E extends Writeable> String writeSql(final E e) {
		StringBuffer result = new StringBuffer(400);
		Writeable w = (Writeable) e;
		int id = 0;
		if(w.getID() != null && w.getID().length() > 0) {
			id = Integer.valueOf(w.getID());
		}
		if (id > 0) {
			result.append(SqlGenerator.updateWritable(w));
		} else {
			// new object
			result.append(SqlGenerator.insertObject(w));
		}
		return result.toString();
	}

	private static final Object updateWritable(final Writeable w) {
		StringBuffer result = new StringBuffer(378);
		String primaryKeyName = SqlGenerator.getPrimaryKeyTableName(w);
		result.append(Syntax.UPDATE).append(sep);
		result.append("[").append(SqlGenerator.getDatabaseName(w.getClass())).append("] ");
		String primaryKey = SqlGenerator.getPrimaryKeyTableName(w);
		result.append(Syntax.SET).append(sep).append(SqlGenerator.updateWritable(w, primaryKey)).append(sep);
		result.append(Syntax.WHERE).append(sep).append(primaryKeyName).append("=").append(w.getID()).append(sep);
		return result.toString();
	}


	private static final String updateWritable(final Writeable w, final String primaryKey){
		StringBuffer result = new StringBuffer(256);
		Map<String, String> list = SqlGenerator.getAttributeArray(w);
		Set<Entry<String, String>> entrySet = list.entrySet();
		if (entrySet.size() > 0 ) {
			@SuppressWarnings("unchecked")
			Entry<String, String>[] entries = entrySet.<Entry<String, String>>toArray(new Entry[list.size()]);
			for (int i = 0; i < entries.length; i++) {
				Entry<String, String> entry = entries[i];
				if (!primaryKey.equalsIgnoreCase(entry.getKey())) {
					result.append(entry.getKey()).append("=").append(entry.getValue()).append(", ");
				}
			}
		}
		String resultString = result.substring(0, result.length() -2);
		return resultString;
	}

	private static final String insertObject(final Writeable w) {
		StringBuffer result = new StringBuffer(378);
		String primaryKey = getPrimaryKeyTableName(w);
		String[] values = SqlGenerator.getValue(w, primaryKey);
		result.append(Syntax.INSERT).append(sep).append(Syntax.INTO).append(sep);
		result.append("[").append(SqlGenerator.getDatabaseName(w.getClass())).append("]");
		result.append(values[0]).append(sep);
		result.append(Syntax.VALUES).append(values[1]).append(sep);
		result.append(Syntax.SELECT).append(sep).append(Syntax.SCOPE_IDENTITY).append("()").append(sep).append(Syntax.AS);
		result.append(sep).append("ID");;
		return result.toString();
	}

	/**
	 * Tries to find the name of the primary key. If it table is not spesifing it with
	 * an annotation it will use class simple name and id. Eg. class product.class + ID
	 * ProductID.
	 * @param w of an writable.
	 * @return the primary key name.
	 */
	private static String getPrimaryKeyTableName(final Writeable w) {
		Annotation annotation = w.getClass().getAnnotation(PrimaryKey.class);
		String primaryKey;
		if (annotation == null) {
			primaryKey = "";
		} else {
			primaryKey = ((PrimaryKey) annotation).id();
		}
		if (primaryKey.equals("")) {
			primaryKey = w.getClass().getSimpleName() + "ID";
		}
		return primaryKey;
	}

	private static final String[] getValue(final Writeable w, final String primaryKey) {
		String result[] = new String[2];
		StringBuffer attribute = new StringBuffer(128);
		StringBuffer value = new StringBuffer(128);
		Map<String, String> list = SqlGenerator.getAttributeArray(w);
		attribute.append(" (");
		value.append(" (");
		Set<Entry<String, String>> entrySet = list.entrySet();
		if (entrySet.size() > 0 ) {
			@SuppressWarnings("unchecked")
			Entry<String, String>[] entries = entrySet.<Entry<String, String>> toArray(new Entry[list.size()]);
			for (int i = 0; i < entries.length; i++) {
				Entry<String, String> entry = entries[i];
				if (!primaryKey.equalsIgnoreCase(entry.getKey())) {					
					attribute.append(entry.getKey());
					value.append(entry.getValue());
					attribute.append(", ");
					value.append(", ");
				} 
			}
			String atrib = attribute.substring(0, attribute.length() -2);
			String val = value.substring(0, value.length() -2);
			atrib += (" )");
			val += (" )");
			result[0] = atrib;
			result[1] = val;
		}
		return result;
	}

	private static final Map<String, String> getAttributeArray(final Writeable w) {
		HashMap<String, String> list = new HashMap<String, String>();
		SqlGenerator.getAttributeArrayWithSubclasses(w, list);
		return list;
	}

	private static final Map<String, String> getAttributeArrayWithSubclasses(final Writeable w, Map<String, String> list) {
		Field[] fiels =  w.getClass().getDeclaredFields();
		SqlGenerator.addField(w, list, fiels);
		Class<?> clazz =  w.getClass().getSuperclass();
		SqlGenerator.addFieldsToMap(w, list, clazz);
		return list;
	}

	/**
	 * @param w
	 * @param list
	 * @param clazz
	 */
	private static void addFieldsToMap(final Writeable w, Map<String, String> list, Class<?> clazz) {
		while (clazz != null && !clazz.equals(Object.class)) {
			SqlGenerator.addField(w, list, clazz.getDeclaredFields());
			Type type = clazz.getGenericSuperclass();
			if (type != null) {
				if (type instanceof Class) {
					Class<?> genericSuperClass = (Class<?>) type;
					SqlGenerator.addFieldsToMap(w, list, genericSuperClass);
				} else {
					logger.trace("Type:" + type.toString() + " is not a instance of class. Will not be used.");
				}
			}
			clazz = clazz.getClass().getSuperclass();

		}
	}

	/**
	 * @param w
	 * @param list
	 * @param fiels
	 */
	private static void addField(final Writeable w,	Map<String, String> list, Field[] fiels) {
		for (Field field : fiels) {
			field.setAccessible(true);
			for (Annotation annotation : field.getAnnotations()) {
				if (SqlGenerator.isDatabaseAnnotation((annotation))) {
					try {
						String columnName = field.getName();
						if (annotation instanceof ForeignKey) {
							columnName = ((ForeignKey) annotation).id();
						}
						list.put(columnName, SqlGenerator.fieldToSQL(field.get(w), annotation));
					} catch (Exception e) {
						logger.trace("Not possible to create a sql name value par", e);
					}
				}
			}
		}
	}

	/**
	 * Check if a annotation is a database annotation.
	 * 
	 * @param annotation to be checked for. 
	 * @return true if it is a database annotation.
	 */
	private static final boolean isDatabaseAnnotation(final Annotation annotation) {
		boolean result = false;
		if (annotation instanceof BigInt) {
			result = true;
		} else if (annotation instanceof DBBoolean) {
			result = true;
		} else if (annotation instanceof Clob) {
			result = true;
		} else if (annotation instanceof Decimal) {
			result = true;
		} else if (annotation instanceof DBDouble) {
			result = true;
		} else if (annotation instanceof DBInteger) {
			result = true;
		} else if (annotation instanceof Numeric) {
			result = true;
		} else if (annotation instanceof Real) {
			result = true;
		} else if (annotation instanceof Timestamp) {
			result = true;
		} else if (annotation instanceof Varchar) {
			result = true;
		} else if (annotation instanceof ForeignKey) {
			result = true;
		}
		return result;
	}

	/**
	 * Convert a field to SQL. It will put convert time stamps, varchar and booleans to right format.
	 * 
	 * @param value to be converted.
	 * @param annotation that the value should be converted to.
	 * @return a converted value as a String. E.g "'value'" or "12"
	 */
	private static final String fieldToSQL(final Object value, final Annotation annotation) {
		if (value == null) {
			return null; 
		}
		boolean utf8 = false;
		boolean wrap = false; 
		String result = null;
		if (annotation instanceof Timestamp) {
			result = SqlGenerator.convertToTimestamp(value);
			wrap = true;
		} else if (annotation instanceof Varchar) {
			if (value instanceof Days || value instanceof Hours || value instanceof Minutes) {
				result = String.valueOf(((BaseSingleFieldPeriod) value).getValue(0));
			} else {
				result = value.toString();
			}
			wrap = true;
			utf8 = true;
		} else if (annotation instanceof DBInteger && value instanceof Boolean) {
			result = ((Boolean)value).booleanValue() ? "1" : "0";
		} else if (annotation instanceof DBBoolean) {
			result = ((Boolean) value).booleanValue() ? "1" : "0";
		} else if (annotation instanceof ForeignKey) {
			result = ((Writeable) value).getID();
			if ( result != null && result.isEmpty()) { // "" is translated into null.
				result = null;
			}
		} else if (annotation instanceof Clob) {
			result = value.toString();
			wrap = true;
			utf8 = true;
		} else {
			result = value.toString();
		}
		if (wrap && utf8) {
			return "N'" + SqlGenerator.escape(result) + "'";
		} else if (wrap) {
			return "'" + SqlGenerator.escape(result) + "'";
		} else {
			return result;
		}
	}

	/**
	 * @param value
	 * @return
	 */
	public static String convertToTimestamp(final Object value) {
		String result;
		if (value instanceof LocalDateTime) {
			LocalDateTime localDateTime = (LocalDateTime) value;
			result = localDateTime.toString(SqlGenerator.DATO_TIME_FORMAT);
		} else if (value instanceof LocalDate) {
			LocalDate localDate = (LocalDate)value;
			result = localDate.toString(SqlGenerator.DATO_FORMAT);
		} else {
			result = new SimpleDateFormat(DATO_TIME_FORMAT).format((java.util.Date) value);
		}
		return result;
	}

	/**
	 * Escapes database chars like ' [ and ].
	 * 
	 * @param value to escape every chars in.
	 * @return a escaped String.
	 */
	public static final String escape(String value) {
		value = value.replaceAll("'", "''");
		value = value.replaceAll("\\]", "\\]");
		value = value.replaceAll("\\[", "\\[");
		return value;
	}

	public static final String deleteStatement(final Writeable w, int id) {
		StringBuffer result = new StringBuffer();
		result.append(Syntax.DELETE).append(sep).append(Syntax.FROM).append(sep);
		result.append("[").append(SqlGenerator.getDatabaseName(w.getClass())).append("]").append(sep);
		result.append("WHERE").append(sep).append(SqlGenerator.getIDName(w.getClass())).append("=").append(id);
		return result.toString();
	}

	/**
	 * Get a SELECT * FROM [TABLE] WHERE ID IN (?,...,?) clause
	 * 
	 * @param w class that will be the table tame
	 * @param size of the ?s
	 * @return the sql.
	 */
	public static final String getInStatement(final Class<? extends Retrievable> clazz, int size) {
		StringBuffer result = new StringBuffer(256);
		result.append(Syntax.SELECT).append(sep).append("*").append(sep).append(Syntax.FROM).append(sep);
		result.append("[").append(SqlGenerator.getDatabaseName(clazz)).append("]").append(sep);
		result.append("WHERE").append(sep).append(SqlGenerator.getIDName(clazz)).append(" IN (");
		while (size-- > 0) {
			if (size == 0) {
				result.append("?");
			} else {
				result.append("?,");
			}
		}
		result.append(")");
		return result.toString();
	}

	public static final String getRetriveablesByIDs(final Class<? extends Retrievable> clazz, boolean ordering, int ...IDs) {
		if (IDs == null || IDs.length == 0) {
			return SqlGenerator.getAllSQL(clazz);
		} else {
			return SqlGenerator.getInStatement(clazz, IDs.length) + " ORDER BY " + SqlGenerator.getIDName(clazz) + " "  + (ordering ? "ASC" : "DESC");
		}
	}

	private static String getAllSQL(Class<? extends Retrievable> clazz) {
		StringBuffer result = new StringBuffer(256);
		result.append(Syntax.SELECT).append(sep).append("*").append(sep).append(Syntax.FROM).append(sep);
		result.append("[").append(SqlGenerator.getDatabaseName(clazz)).append("] ").append(sep);
		return result.toString();
	}

	public static final String getInByLength(int size) {
		StringBuilder result = new StringBuilder(64);
		result.append(" IN (");
		while (size-- > 0) {
			if (size == 0) {
				result.append("?");
			} else {
				result.append("?,");
			}
		}
		result.append(")");
		return result.toString();
	}

	public static String getManyToManyRetrivableByTableAndID(Retrievable resultObject, String id, Integer objectID, Retrievable originalObject) {
		String resultClass = SqlGenerator.getDatabaseName(resultObject.getClass());
		String resultID = getIDName(resultObject.getClass());
		String originalClass = SqlGenerator.getDatabaseName(originalObject.getClass());
		String originalID = getIDName(originalObject.getClass());
		String connectingClass = SqlGenerator.getConnectingManyToMayClass(resultClass, originalClass);
		StringBuffer result = new StringBuffer(128);
		result.append(Syntax.SELECT).append(sep).append(resultClass).append(".*").append(sep);
		result.append(Syntax.FROM).append(sep).append("[").append(resultClass).append("]").append(sep);
		result.append(Syntax.INNER).append(sep).append(Syntax.JOIN).append(sep).append("[").append(connectingClass).append("]").append(sep);
		result.append(Syntax.ON).append(sep).append(resultClass).append(".").append(resultID).append("=").append(connectingClass);
		result.append(".").append(resultID).append(sep);
		result.append(Syntax.WHERE).append(sep).append(connectingClass).append(".").append(originalID).append("=").append(objectID).append(sep);
		result.append(Syntax.ORDER).append(sep).append(Syntax.BY).append(sep).append(resultClass).append(".").append(resultID);
		result.append(sep).append(Syntax.DESC);
		return result.toString();
	}

	public final static String getManyToManyWriteSQL(final Writeable main, final Field field, final ManyToMany manyToMany, final Retrievable retriveable) {
		String resultClass = SqlGenerator.getDatabaseName(main.getClass());
		String resultID = getIDName(main.getClass());
		String originalClass = SqlGenerator.getDatabaseName(retriveable.getClass());
		String originalID = getIDName(retriveable.getClass());
		String connectingClass = SqlGenerator.getConnectingManyToMayClass(resultClass, originalClass);
		LocalDateTime now = new LocalDateTime();
		//		String sql = SqlGenerator.getManyToManyRetrivableByTableAndID(object, manyToMany.id(),
		//				Integer.valueOf(((Retrievable)e).getID()), (Retrievable) e);
		StringBuffer result = new StringBuffer(128);
		result.append(Syntax.INSERT).append(sep).append(Syntax.INTO).append(sep).append("[").append(connectingClass).append("]").append(sep);
		result.append("(").append(resultID).append(", ").append(originalID).append(", ").append("Created").append(")").append(sep);
		result.append(Syntax.VALUES);
		try {
			List<Retrievable> values = (List<Retrievable>) field.get(main);
			if (values != null && values.size() > 0) {
				for (Retrievable value : values) {
					String id = value.getID();
					result.append(" (").append(main.getID()).append(",").append(id).append(",").append("'");
					result.append(now.toString(MssqlTool.getInstance().getDateTimeFormat())).append("'").append("),");
				}
				result.deleteCharAt(result.length() - 1);
			} else return "";
		} catch (IllegalArgumentException e) {
			logger.error("Error getting the field value of ManyToMany relation.", e);
		} catch (IllegalAccessException e) {
			logger.error("Field with ManyToMany relation was not allowed to be accessed.", e);
		}

		return result.toString();
	}
	
	public static String getManyToManyDeleteRefSQL(final Writeable main, final Field field, final ManyToMany manyToMany,
			final Retrievable retriveable) {
		String resultClass = SqlGenerator.getDatabaseName(main.getClass());
		String resultID = getIDName(main.getClass());
		String originalClass = SqlGenerator.getDatabaseName(retriveable.getClass());
		String connectingClass = SqlGenerator.getConnectingManyToMayClass(resultClass, originalClass);
		StringBuilder sql = new StringBuilder(128);
		sql.append(Syntax.DELETE).append(sep).append(Syntax.FROM).append(sep).append("[").append(connectingClass).append("]").append(sep);
		sql.append(Syntax.WHERE).append(sep).append(resultID).append(sep).append("=").append(main.getID());
		return sql.toString();
	}

	private static String getConnectingManyToMayClass(final String resultClass, final String originalClass) {
		if (resultClass.compareTo(originalClass) < 0) {
			return resultClass + "_" + originalClass;
		}
		return originalClass + "_" + resultClass;
	}
}
