package pers.clare.core.sqlquery;

import lombok.extern.log4j.Log4j2;
import pers.clare.core.sqlquery.page.Pagination;
import pers.clare.core.sqlquery.util.SQLUtil;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 建造動態產生PreparedStatement，主要是因為Mysql不支持 setArray，
 * 當條件中有in時，則無法透過PreparedStatement優化。
 * 是執行緒不安全的class，所以多執行續環境下，必須透過SQLQueryBuilder重新建造
 */
@Log4j2
public class SQLQuery {
    private static final String NULL = "null";

    private final char[][] sqlParts;

    private final Map<String, List<Integer>> keyIndex;

    final Object[] values;

    SQLQuery(char[][] sqlParts, Map<String, List<Integer>> keyIndex) {
        this.sqlParts = sqlParts;
        this.keyIndex = keyIndex;
        this.values = new Object[sqlParts.length];
    }

    public SQLQuery value(String key, Object... value) {
        return value(key, value);
    }

    public SQLQuery value(String key, Object value) {
        if (key == null) return this;
        List<Integer> list = keyIndex.get(key);
        if (list == null || list.size() == 0) return this;
        if (value == null) {
            value = NULL;
        }
        for (Integer index : list) {
            values[index] = value;
        }
        return this;
    }

    @Override
    public String toString() {
        return toString(null);
    }

    public String toString(Pagination pagination) {
        StringBuilder sb = new StringBuilder();
        char[] cs;
        for (int i = 0, l = sqlParts.length; i < l; i++) {
            cs = sqlParts[i];
            if (cs == null) {
                append(sb, values[i]);
            } else {
                sb.append(cs);
            }
        }
        if (pagination != null) {
            SQLUtil.buildPaginationSQL(pagination, sb);
        }
        return sb.toString();
    }

    private static void append(
            StringBuilder sb
            , Object value
    ) {
        if (value == null) return;
        if (value == NULL) {
            sb.append(NULL);
        } else {
            Class<?> valueClass = value.getClass();
            if (valueClass.isArray() || Collection.class.isAssignableFrom(valueClass)) {
                SQLUtil.appendInValue(sb, value);
                sb.deleteCharAt(sb.length() - 1);
            } else {
                SQLUtil.appendValue(sb, value);
            }
        }
    }



    public static void main(String[] args) {
        test(new Integer[]{1});
    }

    public static void test(Object value) {
        System.out.println(value instanceof int[]);
        System.out.println(value instanceof Object[]);
    }
}
