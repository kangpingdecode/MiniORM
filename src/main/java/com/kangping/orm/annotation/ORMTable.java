package com.kangping.orm.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注对应的表信息
 */
@Retention(RetentionPolicy.RUNTIME) //运行时该注解还存在
@Target(ElementType.TYPE) //用于类上
public @interface ORMTable {

    public String name() default "";
}
