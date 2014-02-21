package info.sollie.db.annotations;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for helping to identify the unique fields so that one can possible eliminate problems trying to add
 * same value twice. 
 * 
 * @author Andre Sollie (andresol@idium.no)
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Unique {
}
