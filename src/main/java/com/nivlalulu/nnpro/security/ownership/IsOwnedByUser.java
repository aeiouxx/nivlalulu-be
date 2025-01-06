package com.nivlalulu.nnpro.security.ownership;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IsOwnedByUser {
    /**
     * The entity class of the resource.
     * @return
     */
    Class<?> entityClass();


    /**
     * The getName of the method parameter that has the resource ID,
     * typically "id" if you have @PathVariable UUID id.
     */
    String resourceIdParam() default "id";
}
