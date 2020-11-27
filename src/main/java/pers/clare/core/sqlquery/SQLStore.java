package pers.clare.core.sqlquery;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class SQLStore<T> {
    Map<Integer, Constructor<T>> constructorMap;
    boolean crud = false;
    Field autoKey;
    Method[] insertMethods;
    Method[] updateMethods;
    Method[] deleteMethods;
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
            , Method[] insertMethods
            , Method[] updateMethods
            , Method[] deleteMethods
            , String count
            , String select
            , String insert
            , String update
            , String delete
    ) {
        this.constructorMap = constructorMap;
        this.crud = crud;
        this.autoKey = autoKey;
        this.insertMethods = insertMethods;
        this.updateMethods = updateMethods;
        this.deleteMethods = deleteMethods;
        this.count = count;
        this.select = select;
        this.insert = insert;
        this.update = update;
        this.delete = delete;
    }
}
