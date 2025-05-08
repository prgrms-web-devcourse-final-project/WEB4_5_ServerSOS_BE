package com.pickgo.global.logging.annotation;

import com.pickgo.domain.log.enums.ActorType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogPayment {
    ActorType action();
}
