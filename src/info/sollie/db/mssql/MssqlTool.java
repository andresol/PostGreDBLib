package info.sollie.db.mssql;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import javax.sql.rowset.serial.SerialClob;


import info.sollie.db.DatabaseUtils;
import info.sollie.db.Nullable;
import info.sollie.db.annotations.ManyToMany;
import info.sollie.db.annotations.OneToMany;
import info.sollie.db.implementation.DefaultDatabaseTool;
import info.sollie.db.interfaces.Retrievable;
import info.sollie.db.interfaces.Retriver;

import org.apache.log4j.Logger;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;

/**
 * Class for easy access to MSSQL objects.
 * 
 * @author Andre Sollie
 *
 * @param <E> the object to get from the database.
 */
//@Immutable
public class MssqlTool implements Retriver {

	private static final MssqlTool instance = new MssqlTool();

	private MssqlTool(){
	}

	/**
	 * @return the iNSTANCE
	 */
	public final static MssqlTool getInstance() {
		return instance;
	}

	/** Log what errors and other useful things about this class */
	private final static Logger logger = Logger.getLogger(MssqlTool.class); 

	/**
	 * Set objects that are as ForeignKey in the database. It will not follow these objects an create the complex objects here. It means
	 * that other ForeignKey, OneToMany and ManyToMany relations will not be created.
	 * 
	 * @param <E> that is retrievable.
	 * @param field to be retrieved.
	 * @param object that is the object to be retrieved.
	 * @param e the field to be set.
	 * @return a retrieved object.
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public <E extends Retrievable> Object setForeignObject(Field field, Object object, E e, boolean follow) throws IllegalAccessException, InstantiationException {
		@SuppressWarnings("unchecked")
		Object fobject = this.createInstance((Class<? extends Retrievable>) field.getType());
		Retrievable retrivable = (Retrievable) fobject;
		if (object instanceof Long) {
			object = ((Long) object).intValue(); 
		}
		if (retrivable != null && object != null) {
			retrivable.setID((Integer) object);
			if (follow) {
				retrivable.populateObject(false);
			}
			object = fobject;
			field.set(e, object);
		}
		return e;
	}

	/**
	 * @param <E>
	 * @param field
	 * @param object
	 * @param e
	 * @return
	 * @throws IllegalAccessException
	 */
	public <E> Object setBigInt(Field field, Object object, E e) throws IllegalAccessException {
		if ((field.getType().equals(int.class) || field.getType().equals(Integer.class)) && object instanceof Long) {
			object = ((Long) object).intValue(); 
		} else if (field.getType().equals(String.class) && object instanceof Long) {
			object = String.valueOf(((Long) object));
		}
		field.set(e, object);
		return object;
	}

	/**
	 * Set the boolean field. 
	 * 
	 * @param <E>
	 * @param field 
	 * @param object
	 * @param e
	 * @return
	 * @throws IllegalAccessException
	 */
	public <E> Object setBoolean(Field field, Object object, E e)
			throws IllegalAccessException {
		if (object instanceof Integer) {
			object = (((Integer) object) == 1 ? true : false); 
		} else if (object == null) {
			object = false;
		}
		field.set(e, object);
		return object;
	}

	/**
	 * Set the clob field.
	 * 
	 * @param <E>
	 * @param field
	 * @param object
	 * @param e
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 * @throws IllegalAccessException
	 */
	public <E> Object setClob(Field field, Object object, E e)	throws IOException, SQLException, IllegalAccessException {
		if(field.getType().equals(String.class)) {
//			if (object instanceof ClobImpl) {
//				ClobImpl clobImpl = (ClobImpl) object;
//				byte[] bytes = new byte[clobImpl.getAsciiStream().available()];
//				clobImpl.getAsciiStream().read(bytes);
//				object = new String(bytes);
//			} else
				if (object instanceof SerialClob ) {
				SerialClob clob = (SerialClob) object;
				byte[] bytes = new byte[clob.getAsciiStream().available()];
				clob.getAsciiStream().read(bytes);
				object = new String(bytes);
			}
		} 
		field.set(e, object);
		return object;
	}


	/**
	 * Set a varchar object.
	 * 
	 * @param <E>
	 * @param field
	 * @param object
	 * @param e
	 * @throws IllegalAccessException
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <E> Object setVarchar(Field field, Object object, E e)
			throws IllegalAccessException {
		if (object instanceof Long || object instanceof Integer || object instanceof Double){
			object = String.valueOf(object);
			//joda time periods.
		} else if (field.getType().equals(Minutes.class)) {
			String value = ((String) object).trim();
			object = Minutes.minutes(Integer.valueOf(value));
		} else if (field.getType().equals(Hours.class)) {
			String value = ((String) object).trim();
			object = Hours.hours(Integer.valueOf(value));
		} else if (field.getType().equals(Days.class)) {
			String value = ((String) object).trim();
			object = Days.days(Integer.valueOf(value));
		}  else if (object instanceof BigDecimal) {
			object = ((BigDecimal) object).toString();
//		}  else if (object instanceof ClobImpl) {
//			ClobImpl clobImpl = (ClobImpl) object;
//			try {
//				byte[] bytes;
//				bytes = new byte[clobImpl.getAsciiStream().available()];
//				clobImpl.getAsciiStream().read(bytes);
//				object = new String(bytes);
//			} catch (IOException e1) {
//				logger.error("Cannot convert clob to String. Error was ", e1);
//			} catch (SQLException e1) {
//				logger.error("Cannot convert clob to String. Error was ", e1);
//			}
		} else if (object instanceof SerialClob ) {
			SerialClob clob = (SerialClob) object;
			try {
				byte[] bytes = new byte[clob.getAsciiStream().available()];
				clob.getAsciiStream().read(bytes);
				object = new String(bytes);
			} catch (IOException e1) {
				logger.error("Cannot convert clob to String. Error was ", e1);
			} catch (SQLException e1) {
				logger.error("Cannot convert clob to String. Error was ", e1);
			}
		} else if (field.getType().isEnum() && object instanceof String) {
			Class<? extends Enum> enumClass = (Class<? extends Enum>) field.getType();
			String stringValue = (String) object; 
			object = Enum.valueOf(enumClass, stringValue);
		}
		field.set(e, object);
		return object;
	}

	/**
	 * Set a timestamp object.
	 * 
	 * @param <E>
	 * @param field
	 * @param object
	 * @param e
	 * @return
	 * @throws IllegalAccessException
	 */
	public <E> Object setTimeStamp(Field field, Object object, E e) throws IllegalAccessException {
		if (object instanceof String && field.getType().equals(LocalDate.class)) {
			object = new LocalDate(DatabaseUtils.formatDate((String) object, "yy-MM-dd"));

		} else if (object instanceof String) {
			object = DatabaseUtils.formatDate((String) object, "yy-MM-dd HH:mm:ss");

		} else if (field.getType().equals(LocalDate.class)) {
			java.sql.Timestamp timeStamp = (java.sql.Timestamp) object;
			if (timeStamp != null) {
				String time = timeStamp.toString();
				try {
//					object = new LocalDate(Util.formatDate(time, "yy-MM-dd"));
				} catch (Exception e1) {
					logger.debug("Could not get the date. ");
				}
			}
		} else if (field.getType().equals(LocalDateTime.class)) {
			java.sql.Timestamp timeStamp = (java.sql.Timestamp) object;
			if (timeStamp != null) {
				String time = timeStamp.toString();
				try {
	//				object = new LocalDateTime(Util.formatDate(time, "yy-MM-dd HH:mm:ss"));
				} catch (Exception e1) {
					logger.debug("Could not get the date and time. ");
				}
			}

		} 

		field.set(e, object);
		return object;
	}

	/**
	 * @param <E>
	 * @param field
	 * @param object
	 * @param e
	 * @return
	 * @throws IllegalAccessException
	 */
	public <E> Object setNumericAndDecimal(Field field, Object object, E e)
			throws IllegalAccessException {
		if (field.getType().equals(double.class) && object instanceof BigDecimal) {
			object = ((BigDecimal) object).doubleValue();
		} 
		field.set(e, object);
		return object;
	}

	/**
	 * @param <E>
	 * @param field
	 * @param object
	 * @param e
	 * @return
	 * @throws IllegalAccessException
	 */
	public <E> Object setDouble(Field field, Object object, E e) throws IllegalAccessException {
		if (object instanceof BigDecimal && (field.getType().equals(Long.class) || field.getType().equals(long.class))) {
			object = ((BigDecimal) object).longValue();
		} else if (object instanceof BigDecimal) {
			object = ((BigDecimal) object).doubleValue();
		} else if (field.getType().equals(String.class)) {
			object = String.valueOf(object);
		} 
		if (object == null) {
			object = 0;
		}
		field.set(e, object);
		return object;
	}

	/**
	 * @param annotation
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public <E> void createObjectForManyToManyRelation(E e, Field field, Annotation annotation) throws InstantiationException, IllegalAccessException {
		ManyToMany manyToMany = (ManyToMany) annotation;
		Type type = field.getGenericType();
		if (type instanceof ParameterizedType) {
			ParameterizedType pType = (ParameterizedType) type;
			for (Type clazz : pType.getActualTypeArguments()) {
				if (clazz instanceof Class) {
					@SuppressWarnings("unchecked")
					Retrievable object = (Retrievable) ((Class<Retrievable>) clazz).newInstance();
					String sql = SqlGenerator.getManyToManyRetrivableByTableAndID(object, manyToMany.id(),
							Integer.valueOf(((Retrievable)e).getID()), (Retrievable) e);
					List<?> list = (List<?>) DefaultDatabaseTool.getInstance().getObjects(sql, object.getClass(), false);
					field.set(e, list);
				}
			}
		}
	}

	/**
	 * @param <E>
	 * @param e
	 * @param field
	 * @param annotation
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public final <E> void createObjectForOneToManyRelation(E e, Field field, Annotation annotation) throws InstantiationException,
	IllegalAccessException {
		OneToMany oneToMany = (OneToMany) annotation;
		Type type = field.getGenericType();
		if (type instanceof ParameterizedType) {
			ParameterizedType pType = (ParameterizedType) type;
			for (Type clazz : pType.getActualTypeArguments()) {
				if (clazz instanceof Class) {
					@SuppressWarnings("unchecked")
					Retrievable object = (Retrievable) ((Class<Retrievable>) clazz).newInstance();
					String sql = SqlGenerator.getRetrivableByTableAndID(object, oneToMany.id(), Integer.valueOf(((Retrievable)e).getID()));
					List<?> list = (List<?>) DefaultDatabaseTool.getInstance().getObjects(sql, object.getClass(), false);
					field.set(e, list);
				}
			}
		}
	}

	@Override
	public <E> Object setInteger(Field field, Object object, E e) throws IllegalAccessException {
		if (object instanceof BigDecimal) {
			object = ((BigDecimal) object).intValue();

		} else if (field.getType().equals(boolean.class)) {
			object = ((Integer)object == 1);

		} else if (field.getType().equals(String.class)) {
			object = String.valueOf(object);

		} else if (object instanceof Long) {
			object =((Long) object).intValue();

		} else if (object == null) {
			object = 0;
		}
		field.set(e, object);
		return object;
	}

	@Override
	public String getDateTimeFormat() {
		return "yyyy-MM-dd HH:mm:ss";
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
	
	public static final String getDateFormat() {
		return "yyyy-MM-dd";
	}
}
