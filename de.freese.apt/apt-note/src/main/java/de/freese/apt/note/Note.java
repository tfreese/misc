/**
 * Created: 05.12.2011
 */

package de.freese.apt.note;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Thomas Freese
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(
{
		ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.FIELD
})
public @interface Note
{
	/**
	 * @return String
	 */
	String value();
}
