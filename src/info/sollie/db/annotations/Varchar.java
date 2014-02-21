package info.sollie.db.annotations;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for helping to identify the size of nvarchar so that one can possible eliminate the data truncated
 * problem. 
 * 
 * @author Andre Sollie (andresol@idium.no)
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Varchar {
    /**
     * The size of database nvarchar attribute. Default is 50.
     * @return
     */
    int size() default 50;
}
