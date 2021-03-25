package pers.clare.core.sqlquery;


import lombok.Getter;
import pers.clare.core.sqlquery.function.FieldGetHandler;
import pers.clare.core.sqlquery.function.FieldSetHandler;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

@Getter
public class SQLStore<T> {
    Constructor<T> constructor;
    boolean crud = false;
    Map<String, FieldSetHandler> fieldSetMap;
    Map<String, FieldGetHandler> fieldGetMap;
    Field autoKey;
    Field[] keyFields;
    Field[] insertFields;
    Field[] updateFields;
    String count;
    SQLQueryBuilder countById;
    String select;
    SQLQueryBuilder selectById;
    SQLQueryBuilder insertAutoKey;
    SQLQueryBuilder insert;
    SQLQueryBuilder update;
    String deleteAll;
    SQLQueryBuilder deleteById;

    public SQLStore(Constructor<T> constructor
            , Map<String, FieldSetHandler> fieldSetMap
            , Map<String, FieldGetHandler> fieldGetMap
    ) {
        this.constructor = constructor;
        this.fieldSetMap = fieldSetMap;
        this.fieldGetMap = fieldGetMap;
    }

    public SQLStore(
            Constructor<T> constructor
            , boolean crud
            , Map<String, FieldSetHandler> fieldSetMap
            , Map<String, FieldGetHandler> fieldGetMap
            , Field autoKey
            , Field[] keyFields
            , Field[] insertFields
            , Field[] updateFields
            , String count
            , SQLQueryBuilder countById
            , String select
            , SQLQueryBuilder selectById
            , SQLQueryBuilder insertAutoKey
            , SQLQueryBuilder insert
            , SQLQueryBuilder update
            , String deleteAll
            , SQLQueryBuilder deleteById
    ) {
        this.constructor = constructor;
        this.crud = crud;
        this.fieldSetMap = fieldSetMap;
        this.fieldGetMap = fieldGetMap;
        this.autoKey = autoKey;
        this.keyFields = keyFields;
        this.insertFields = insertFields;
        this.updateFields = updateFields;
        this.count = count;
        this.countById = countById;
        this.select = select;
        this.selectById = selectById;
        this.insertAutoKey = insertAutoKey;
        this.insert = insert;
        this.update = update;
        this.deleteAll = deleteAll;
        this.deleteById = deleteById;
    }
}
