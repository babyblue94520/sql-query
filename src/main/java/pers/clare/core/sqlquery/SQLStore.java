package pers.clare.core.sqlquery;




import lombok.Getter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

@Getter
public class SQLStore<T> {
    Map<Integer, Constructor<T>> constructorMap;
    boolean crud = false;
    Field autoKey;
    Method[] keyMethods;
    Method[] insertMethods;
    Method[] updateMethods;
    String count;
    String all;
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
