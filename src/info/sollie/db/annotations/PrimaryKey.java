package info.sollie.db.annotations;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to help identify the primary key(s) in a table. 
 * 
 * @author Andre Sollie (andresol@idium.no)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PrimaryKey {
    /**
     * The attributes that is defined to be the primary keys. 
     * 
     * @return the list of primary keys in a database table. 
     */
    public String id() default "";
}
