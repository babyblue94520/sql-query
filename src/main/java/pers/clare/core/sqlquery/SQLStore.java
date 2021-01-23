package pers.clare.core.sqlquery;




import lombok.Getter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

@Getter
public class SQLStore<T> {
    Map<Integer, Constructor<T>> constructorMap;
    boolean crud = false;
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

    public SQLStore(Map<Integer, Constructor<T>> constructorMap) {
        this.constructorMap = constructorMap;
    }

    public SQLStore(
            Map<Integer, Constructor<T>> constructorMap
            , boolean crud
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
        this.constructorMap = constructorMap;
        this.crud = crud;
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
