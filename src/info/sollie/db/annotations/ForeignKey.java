/**
 * 
 */
package info.sollie.db.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation to help identify object in other tables. It should may be called ManyToOne Or OneToOne 
 * 
 * @author Andre Sollie (andresol@idium.no)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ForeignKey {
	/** Name of the ID in the local database. */ //NOTE: Should be found automatic.
	String id();
}