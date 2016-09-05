package pt.isel.pdm.g04.pf.data.thoth.models.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Attributes {

    boolean primaryKey() default false;

    boolean unique() default false;

    boolean notMapped() default false;
}
