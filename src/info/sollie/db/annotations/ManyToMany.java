package info.sollie.db.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import info.sollie.db.Persistence;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
/**
 * 
 * @author Andre Sollie
 * 
 * It will retrive object from A Many To Many Relation. It is important to know that the naming convention of 
 * SQL database names is eg. Location_Rute for Many to Many relation where the db name that is less is first.
 * "Location".compareTo(Rute) = minus. 
 *
 */
public @interface ManyToMany {
	Class<? extends Persistence<?>> clazz();
	/** Local ID for connection to the remote field */
	String id(); 
}
