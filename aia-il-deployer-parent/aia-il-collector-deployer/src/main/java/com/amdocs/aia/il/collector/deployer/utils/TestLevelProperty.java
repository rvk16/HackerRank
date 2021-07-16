package com.amdocs.aia.il.collector.deployer.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation (to be used on test methods) that allow specifying method-level spring properties
 * (unfortunately we needed to implement this annotation and infrastructure by ourselves, since spring test
 * currently does not support it... see https://github.com/spring-projects/spring-framework/issues/18951)
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TestLevelProperty {
    String key();
    String value();
}
