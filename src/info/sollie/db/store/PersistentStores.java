package info.sollie.db.store;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.List;


import info.sollie.db.DatabaseTools;
import info.sollie.db.Nullable;
import info.sollie.db.annotations.ManyToMany;
import info.sollie.db.interfaces.DatabaseTool;
import info.sollie.db.interfaces.Retrievable;
import info.sollie.db.interfaces.Writeable;
import info.sollie.db.mssql.SqlGenerator;

import org.apache.log4j.Logger;

//@Immutable
public class PersistentStores {
	
	public static final PersistentStore PS = new MssqlStore();
	
	/**
	 * Get the Mssql persistent store. 
	 * 
	 * @return the mssql persistent store.
	 */
	public static PersistentStore getMssqlStore() {
		return PS;
	}
	
	private static class MssqlStore implements PersistentStore {
		
		/** Log errors */
		private static final Logger logger = Logger.getLogger(MssqlStore.class);
		
		/** Static method factory */
		private MssqlStore(){
		}
		
		/** The only implemented database tool. */
		private final DatabaseTool mssqlTool = DatabaseTools.getMssqlDatabaseTool();

		/**
		 * Get object from the mssql database. If id is null. It will try to create a new object with no 
		 * content.
		 */
		@Override
		@Nullable
		public final <E extends Retrievable> E getObject(final Class<E> clazz, final int id, boolean follow) {
			if (clazz == null) {
				throw new IllegalArgumentException("Class cannot be null");
			}
			if (id < 0) {
				try {
					return clazz.newInstance();
				} catch (InstantiationException e) {
					// Don't care.
				} catch (IllegalAccessException e) {
					// Don't care.
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Getting object of type " + clazz.getSimpleName() + " with ID: " + id );
			}
			return mssqlTool.getObject(clazz, id, follow);
		}

		@Override
		public final <E extends Retrievable> E getObject(final E e, boolean follow) {
			if (e == null) {
				throw new IllegalArgumentException("Object to set cannot be null.");	
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to get object with ID: " + e.getID());
			}
			String sql = SqlGenerator.getRetrivable((Retrievable) e);
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to get object with SQL: " + sql);
			}
			mssqlTool.setObject(sql, e, follow);
			return e;
		}

		/**
		 * Writes object to the Mssql database. It will store the object to the database, but not
		 * objects that references to this object.
		 */
		@Override
		public final <E extends Writeable> void storeObject(final E e) {
			if (e != null) {
				String sql = SqlGenerator.writeSql(e);
				mssqlTool.storeObject(sql, e);
				try {
					this.storeManyToManyRelation(e);
				} catch (InstantiationException e1) {
					logger.error("Cannot create a instance of the ManyToMany relation object", e1);
				} catch (IllegalAccessException e1) {
					logger.error("Cannot accesss the field to the ManyToMany relation object", e1);
				}
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Cannot store object. Object to store is null.");
				}
			}
		}
		
		public final <E extends Writeable> void storeManyToManyRelation(final E e) throws InstantiationException, IllegalAccessException {
			for (Field field : e.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				if (field.getType().equals(List.class)) {
					for (Annotation annotation: field.getAnnotations()) {
						if (annotation instanceof ManyToMany) {
							ManyToMany manyToMany = (ManyToMany) annotation;
							Type type = field.getGenericType();
							if (type instanceof ParameterizedType) {
								ParameterizedType pType = (ParameterizedType) type;
								for (Type clazz : pType.getActualTypeArguments()) {
									if (clazz instanceof Class) {
										Retrievable relationObject = (Retrievable) ((Class<Retrievable>) clazz).newInstance();
										String sql = SqlGenerator.getManyToManyWriteSQL(e, field, manyToMany, relationObject);
										if (sql.length() > 0 ) {
											String delteSql = SqlGenerator.getManyToManyDeleteRefSQL(e, field, manyToMany, relationObject);
											//TODO:Sm.DB.write(delteSql);
											//TOD0:Sm.DB.write(sql);
										}
									}
								}
							}
						}
					}
				}
			}
		}

		/**
		 * Not implemented yet. Should store a lot of objects.
		 */
		@Override
		@Deprecated
		public final <E extends Writeable> void storeObjects(List<E> e) {
		}

		@Override
		@Nullable
		public <E extends Retrievable> E getObject(Class<E> clazz, int id) {
			return this.getObject(clazz, id, true);
		}

		@Override
		public <E extends Retrievable> E getObject(E e) {
			return this.getObject(e, true);
		}

		@Override
		public <E extends Writeable> void deleteObject(String id, Class<E> clazz) {
			try {
				this.deleteObject(Integer.valueOf(id), clazz);
			} catch (NumberFormatException e) {
				logger.error("id is not a valid number. Cannot delete object", e);
			}
		}

		@Override
		public <E extends Writeable> void deleteObject(E e) {
			this.deleteObject(e.getID(), e.getClass());
		}

		@Override
		public <E extends Retrievable> List<E> getObjects(Class<E> clazz, int... IDs) {
			return this.getObjects(clazz, true ,IDs);
		}

		@Override
		public <E extends Retrievable> List<E> getObjects(Class<E> clazz, boolean ordering, int... IDs) {
			String sql = SqlGenerator.getRetriveablesByIDs(clazz, ordering, IDs);
			ResultSet resultSet;
			if (IDs == null || IDs.length == 0) {
				resultSet = this.mssqlTool.read(sql);
			} else {
				Object[] objects = new Object[IDs.length];
				for (int i = 0; i < IDs.length ; i++) {
					objects[i] = new Integer(IDs[i]);
				}
				resultSet = this.mssqlTool.read(sql, objects);
			}
			return this.mssqlTool.getObjects(resultSet, clazz, true);
		}

		@Override
		public <E extends Writeable> void deleteObject(int id, Class<E> clazz) {
			try {
				String sql = SqlGenerator.deleteStatement((Writeable) clazz.newInstance(), id);
				mssqlTool.write(sql);
			} catch (Exception e ) {
				logger.error("Cannot delete object. Something is wrong.", e);
			}
			
		}
		
	}

}
