package imagingbook.pluginutils.annotations;

import static java.lang.annotation.ElementType.TYPE;
//import static java.lang.annotation.RetentionPolicy.RUNTIME;
//import static java.lang.annotation.RetentionPolicy.SOURCE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * Annotation to specify the menu entry (name) of the associated plugin class.
 * Takes a {@link String} argument e.g.,
 * <pre>
 *  {@literal @}IjMenuEntry("My Grand Plugin")
 * public class My_Plugin implements PlugIn {
 *   ...
 * }</pre>
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface IjPluginName {
	public String value();
}
