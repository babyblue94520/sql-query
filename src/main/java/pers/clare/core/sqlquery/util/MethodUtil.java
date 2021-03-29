package pers.clare.core.sqlquery.util;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

public class MethodUtil {

    public static Class<?> getReturnClass(Method method, int index) {
        Type type = getReturnType(method, index);
        if (type == null) {
            return null;
        } else {
            return (Class<?>) type;
        }
    }

    public static Type getReturnType(Method method, int index) {
        Type type = method.getGenericReturnType();
        if (type instanceof ParameterizedType) {
            return getType(type, index);
        } else {
            return null;
        }
    }

    public static Type getType(Type type, int index) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] types = parameterizedType.getActualTypeArguments();
            type = types[index];
            if (type instanceof ParameterizedType) {
                return ((ParameterizedType) type).getRawType();
            }
            return type;
        }
        return null;
    }

    public static ParameterizedType getParameterizedType(Type type, int index) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] types = parameterizedType.getActualTypeArguments();
            type = types[index];
            if (type instanceof ParameterizedType) {
                return (ParameterizedType) type;
            }
        }
        return null;
    }

    public Set<Map> method() {
        return null;
    }

}
