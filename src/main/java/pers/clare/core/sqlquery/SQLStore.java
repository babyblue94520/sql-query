package pers.clare.core.sqlquery;


import sun.reflect.FieldAccessor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.*;

public class SQLStore<T> {
    Map<Integer, Constructor<T>> constructorMap;
    boolean crud = false;
    Field autoKey;
    FieldAccessor autoKeyAccessor;
    Method[] keyMethods;
    Method[] insertMethods;
    Method[] updateMethods;
    String count;
    String select;
    String insert;
    String update;
    String delete;

    public SQLStore(Map<Integer, Constructor<T>> constructorMap) {
        this.constructorMap = constructorMap;
    }

    public SQLStore(
            Map<Integer, Constructor<T>> constructorMap
            , boolean crud
            , Field autoKey
            , FieldAccessor autoKeyAccessor
            , Method[] keyMethods
            , Method[] insertMethods
            , Method[] updateMethods
            , String count
            , String select
            , String insert
            , String update
            , String delete
    ) {
        this.constructorMap = constructorMap;
        this.crud = crud;
        this.autoKey = autoKey;
        this.autoKeyAccessor = autoKeyAccessor;
        this.keyMethods = keyMethods;
        this.insertMethods = insertMethods;
        this.updateMethods = updateMethods;
        this.count = count;
        this.select = select;
        this.insert = insert;
        this.update = update;
        this.delete = delete;
    }
}
