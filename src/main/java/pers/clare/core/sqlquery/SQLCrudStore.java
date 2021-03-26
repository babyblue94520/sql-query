package pers.clare.core.sqlquery;


import lombok.Getter;
import pers.clare.core.sqlquery.function.FieldSetHandler;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;

@Getter
public class SQLCrudStore<T> extends SQLStore<T> {
    String tableName;
    FieldColumn[] fieldColumns;
    Field autoKey;
    Field[] keyFields;
    String count;
    SQLQueryBuilder countById;
    String select;
    SQLQueryBuilder selectById;
    String deleteAll;
    SQLQueryBuilder deleteById;

    public SQLCrudStore(
            Constructor<T> constructor
            , Map<String, FieldSetHandler> fieldSetMap
            , String tableName
            , FieldColumn[] fieldColumns
            , Field autoKey
            , Field[] keyFields
            , String count
            , SQLQueryBuilder countById
            , String select
            , SQLQueryBuilder selectById
            , String deleteAll
            , SQLQueryBuilder deleteById
    ) {
        super(constructor, fieldSetMap);
        this.tableName = tableName;
        this.fieldColumns = fieldColumns;
        this.autoKey = autoKey;
        this.keyFields = keyFields;
        this.count = count;
        this.countById = countById;
        this.select = select;
        this.selectById = selectById;
        this.deleteAll = deleteAll;
        this.deleteById = deleteById;
    }
}
