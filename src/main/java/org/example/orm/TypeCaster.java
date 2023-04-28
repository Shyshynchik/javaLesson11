package org.example.orm;

import org.hibernate.annotations.Type;

import java.lang.reflect.Field;

final class TypeCaster {

    static String castType(Field field) {
        if (field.isAnnotationPresent(Type.class)) {
            String type = field.getAnnotation(Type.class).type();
            SpecialTypeCaster specialTypeCaster = SpecialTypeCaster.findCasterByType(type);

            return specialTypeCaster.castType();
        }

        if (field.getType() == String.class) {
            return "?";
        }

        return "?::" + field.getType().getSimpleName().toLowerCase();
    }

}
