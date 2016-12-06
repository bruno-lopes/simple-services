package com.vaidegrails.simpleservices.crud

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Created by bruno on 12/4/16.
 */
@Target([ElementType.METHOD])
@Retention(RetentionPolicy.RUNTIME)
@interface MethodValidationAnnotation {
    String appliesTo()
}