/**
 * 
 */
package info.sollie.db.handlers;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import info.sollie.db.Nullable;
import info.sollie.db.annotations.BigInt;
import info.sollie.db.annotations.Clob;
import info.sollie.db.annotations.DBBoolean;
import info.sollie.db.annotations.DBDouble;
import info.sollie.db.annotations.DBInteger;
import info.sollie.db.annotations.Decimal;
import info.sollie.db.annotations.ForeignKey;
import info.sollie.db.annotations.ManyToMany;
import info.sollie.db.annotations.Numeric;
import info.sollie.db.annotations.OneToMany;
import info.sollie.db.annotations.Real;
import info.sollie.db.annotations.Timestamp;
import info.sollie.db.annotations.Varchar;
import info.sollie.db.interfaces.Retrievable;
import info.sollie.db.interfaces.Retriver;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.log4j.Logger;

/**
 * Get every object in the resultset based upon reflection and annotations.
 *  
 * @author Andre Sollie
 *
 */
public class AnnotationObjectListHandler<T extends Retrievable> implements ResultSetHandler<List<T>> {

	private static final Logger logger = Logger.getLogger(AnnotationObjectListHandler.class);

	private final Class<T> type;

	private boolean follow;

	private final Retriver retriver;

	private T t;

	public AnnotationObjectListHandler(final Class<T> type, final boolean follow, Retriver retriver) {
		this.type = type;
		this.follow = follow;
		this.retriver = retriver;
		this.t = null;
	}

	@SuppressWarnings("unchecked")
	public AnnotationObjectListHandler(final T t, final boolean follow, Retriver retriver) {
		this.type =  (Class<T>) t.getClass();
		this.follow = follow;
		this.retriver = retriver;
		this.t = t;
	}

	@Override
	public List<T> handle(final ResultSet resultSet) throws SQLException {
		List<T> list = new ArrayList<T>(50);
		while(resultSet.next()) {
			if (this.t == null) {
				this.t = this.createInstance(type);
			}
			t = this.createObject(resultSet, t, follow);
			list.add(t);
			t = null;
		}
		return list;
	}

	@Nullable
	protected final <E extends Retrievable> E createInstance(Class<E> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("Clazz could not be null");
		}
		E e = null;
		try {
			e = clazz.newInstance();
		} catch (IllegalAccessException e1) {
			logger.error("Could not create a new instance of class :" + clazz.getSimpleName() , e1);
		} catch (InstantiationException e1) {
			logger.error("Could not instaniate a new class of: " + clazz.getSimpleName(), e1);
		} catch (ClassCastException e1) {
			logger.error("Could not cast object to db object. See if the object implements Retrivable.");
		}
		return e;
	}

	/**
	 * Create a object and match the fields against the fields in the database.
	 * 
	 * @param resultSet the result set.
	 * @param clazz to be instantiated.
	 * @return a created object with values from the database.
	 */
	public final <E extends Retrievable> E createObject(ResultSet resultSet, E e, boolean follow) {
		try {
			this.createObjectFromDatabase(resultSet, e, follow);
			if (follow) {
				this.createObjectFromRelations(e);
			}
		} catch (SQLException e1) {
			logger.error("Some thing wrong with the SQL query. Message: " + e1.getMessage());
		} catch (IllegalArgumentException e1) {
			logger.error("Could not create objects. IllegallArguments. " + e1.getMessage());
		} catch (IllegalAccessException e1) {
			logger.error("Could not create objects. IllegalAcces to a object : " + e1.getMessage());
		} catch (InstantiationException e1) {
			logger.error("Could not create objects. Not possible to inizialise. " + e1.getMessage());
		} 
		return e;
	}


	/**
	 * Method for creating relations between OneToMany and ManyToMany relations between objects. It will look for 
	 * right annotation and if found. This relation will be created as a List.
	 * 
	 * @param <E> class that is retrievable.
	 * @param e the object to create relation from. If it contains OneToMany relation and ManyToMany relation. 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public final <E> void createObjectFromRelations(E e) throws InstantiationException, IllegalAccessException {
		for (Field field : e.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			if (field.getType().equals(List.class)) {
				for (Annotation annotation: field.getAnnotations()) {
					if (annotation instanceof OneToMany) {
						retriver.createObjectForOneToManyRelation(e, field, annotation);
					} else if (annotation instanceof ManyToMany) {
						retriver.createObjectForManyToManyRelation(e, field, annotation); //FIXME: Must implement this one.
					}
				}
			}
		}
	}

	/**
	 * @param <E>
	 * @param resultSet
	 * @param e
	 * @throws SQLException
	 */
	public final <E extends Retrievable> void createObjectFromDatabase(ResultSet resultSet, E e, boolean follow) throws SQLException {
		ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
		int columnCount = resultSetMetaData.getColumnCount();
		Field[] fields = e.getClass().getDeclaredFields();

		for (int i = 1; i <= columnCount; i++ ) {
			String columnName = resultSetMetaData.getColumnName(i);
			boolean found = this.setResultToField(resultSet, e, follow, fields, i, columnName);
			Class<?> clazz =  e.getClass().getSuperclass();
			if (!found && clazz != null && !clazz.equals(Object.class)) {
				Field[] newFields = clazz.getDeclaredFields();
				found = this.setResultToField(resultSet, e, follow, newFields, i, columnName);
			}
			setValue(resultSet, e, follow, i, columnName, found, clazz);
		}
	}

	/**
	 * Recursive set value if not found.
	 * 
	 */
	private <E extends Retrievable> void setValue(ResultSet resultSet, E e, boolean follow, int i, String columnName, boolean found, Class<?> clazz)
			throws SQLException {
		if (clazz != null) {
			clazz = clazz.getSuperclass(); // Safe for nullpointer. But looks ugly.
			if (!found && clazz != null && !clazz.equals(Object.class)) {
				Field[] newFields = clazz.getDeclaredFields();
				found = this.setResultToField(resultSet, e, follow, newFields, i, columnName);
				if (!found) {
					this.setValue(resultSet, e, follow, i, columnName, found, clazz);
				}
			}
		}
	}

	
	private <E extends Retrievable> boolean setResultToField(ResultSet resultSet, E e, boolean follow,
			Field[] fields, int i, String columnName) throws SQLException {
		for (Field field : fields) {
			field.setAccessible(true);
			if (columnName.equalsIgnoreCase(field.getName()) || this.isForeignKeyMatch(field, columnName)) {
				Object object = resultSet.getObject(i);
				this.setField(field, object, e, follow);
				return true;
			}
		}
		return false;
	}
	/**
	 * Set a field into a object. It will do some tricks if the fields does not match each other. 
	 * 
	 * If the object field is Date and it is saved as a String it will try to convert it to date with a rule.
	 * If the object is String field and the values in the database is of integer and long it will be converted to String.
	 * If the object is in the database integer but the field is number. It will be translate in a way that 1 is true.
	 * 
	 * NOTE: Should be switch.
	 * 
	 * @param field to have it field set.
	 * @param e object that contains the field.
	 * 
	 */
	public final <E extends Retrievable> void setField(Field field, Object object, E e, boolean follow) {
		boolean found = false;
		try {
			for (Annotation annotatino : field.getDeclaredAnnotations()) {
				if (annotatino instanceof DBInteger) {
					object = retriver.setInteger(field, object, e);
					found = true;
					break;

				} else if (annotatino instanceof DBDouble) {
					object = retriver.setDouble(field, object, e);
					found = true;
					break;

				} else if (annotatino instanceof Real) {
					field.set(e, object);
					found = true;
					break;

				} else if (annotatino instanceof Numeric || annotatino instanceof Decimal) {
					object = retriver.setNumericAndDecimal(field, object, e);
					found = true;
					break;

				} else if (annotatino instanceof Timestamp) {
					object = retriver.setTimeStamp(field, object, e);
					found = true;
					break;

				} else if (annotatino instanceof Varchar) {
					object = retriver.setVarchar(field, object, e);
					found = true;
					break;

				} else if (annotatino instanceof Clob) {
					object = retriver.setClob(field, object, e);
					found = true;
					break;
				}  else if (annotatino instanceof DBBoolean) {
					object = retriver.setBoolean(field, object, e);
					found = true;
					break;

				} else if (annotatino instanceof BigInt) { 
					object = retriver.setBigInt(field, object, e);
					found = true;
					break;

				} else if (annotatino instanceof ForeignKey) {
					object = retriver.setForeignObject(field, object, e, follow);
					found = true;
					break;

				} else {
					//Do possible things.
				}
			}
			if (!found) {
				if (logger.isTraceEnabled()) {
					logger.trace("Field is not a db field. " + field.getName());
				}
			} 

		} catch (SecurityException e1) {
			logger.error("Some fields does not have access.", e1);
		} catch (IllegalArgumentException e1) {
			logger.error("IllegalArgument. Message: " + e1.getMessage());
		} catch (IllegalAccessException e1) {
			logger.error("Cannot access the field. Message: " + e1.getMessage());
		} catch (IOException e1) {
			logger.error("Some IO exception when creating objects", e1);
		} catch (SQLException e1) {
			logger.error("Error with retriving the object from SQL. Message: " + e1.getMessage());
		} catch (InstantiationException e1) {
			logger.error("Could not instaniate class.", e1);
		}
	}


	private boolean isForeignKeyMatch(Field field, String columnName) {
		boolean result = false;
		Annotation annotation = field.getAnnotation(ForeignKey.class);
		if (annotation != null) {
			ForeignKey foreignKey = (ForeignKey) annotation;
			if (columnName.equalsIgnoreCase(foreignKey.id())) {
				result = true;
			}
		}
		return result;
	}

}
