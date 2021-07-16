package com.amdocs.aia.il.configuration.discovery.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DiscoveryParameter {
    /**
     * The name of the parameter. If left empty, then the name of the underlying field will be assumed as the parameter
     * name
     */
    String name() default "";

    /**
     * Indicates whether the discoverer must receive a value for this parameter
     */
    boolean required() default true;

}
