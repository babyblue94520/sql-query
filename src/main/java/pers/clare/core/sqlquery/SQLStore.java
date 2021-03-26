package pers.clare.core.sqlquery;


import lombok.Getter;
import pers.clare.core.sqlquery.function.FieldSetHandler;

import java.lang.reflect.Constructor;
import java.util.*;

@Getter
public class SQLStore<T> {
    Constructor<T> constructor;
    Map<String, FieldSetHandler> fieldSetMap;

    public SQLStore(Constructor<T> constructor
            , Map<String, FieldSetHandler> fieldSetMap
    ) {
        this.constructor = constructor;
        this.fieldSetMap = fieldSetMap;
    }
}
