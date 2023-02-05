package io.github.apricotfarmer11.mods.tubion.event.api;

import io.github.apricotfarmer11.mods.tubion.event.api.types.Priority;

import java.lang.annotation.*;

/**
 * Marks a method so that the EventManager knows that it should be registered.
 * The priority of the method is also set with this.
 *
 * @author DarkMagician6
 * @see io.github.apricotfarmer11.mods.tubion.event.api.types.Priority
 * @since July 30, 2013
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventTarget {

    byte value() default Priority.MEDIUM;
}
