package pers.clare.core.sqlquery;

import lombok.extern.log4j.Log4j2;
import pers.clare.core.sqlquery.page.Pagination;

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
                appendInValue(sb, value);
                sb.deleteCharAt(sb.length() - 1);
            } else {
                appendValue(sb, value);
            }
        }
    }

    private static void appendValue(
            StringBuilder sb
            , Object value
    ) {
        if (value instanceof String) {
            sb.append('\'');
            char[] cs = ((String) value).toCharArray();
            for (char c : cs) {
                sb.append(c);
                if (c == '\'') sb.append('\'');
            }
            sb.append('\'');
        } else {
            sb.append(value);
        }
    }

    /**
     * appendIn
     * 依陣列數量，動態產生 (?,?,?,?) or ((?,?),(?,?))
     *
     * @param sb
     * @param value
     */
    private static void appendInValue(
            StringBuilder sb
            , Object value
    ) {
        Class<?> valueClass = value.getClass();
        if (valueClass.isArray()) {
            sb.append('(');
            if (value instanceof Object[]) {
                Object[] vs = (Object[]) value;
                if (vs.length == 0) throw new IllegalArgumentException("SQL WHERE IN doesn't empty value");
                for (Object v : vs) appendInValue(sb, v);
            } else if (value instanceof int[]) {
                int[] vs = (int[]) value;
                if (vs.length == 0) throw new IllegalArgumentException("SQL WHERE IN doesn't empty value");
                for (int v : vs) appendInValue(sb, v);
            } else if (value instanceof long[]) {
                long[] vs = (long[]) value;
                if (vs.length == 0) throw new IllegalArgumentException("SQL WHERE IN doesn't empty value");
                for (long v : vs) appendInValue(sb, v);
            } else if (value instanceof char[]) {
                char[] vs = (char[]) value;
                if (vs.length == 0) throw new IllegalArgumentException("SQL WHERE IN doesn't empty value");
                for (char v : vs) appendInValue(sb, v);
            }
            sb.deleteCharAt(sb.length() - 1).append(')');
        } else if (Collection.class.isAssignableFrom(valueClass)) {
            Collection<Object> vs = (Collection<Object>) value;
            if (vs.size() == 0) throw new IllegalArgumentException("SQL WHERE IN doesn't empty value");
            sb.append('(');
            for (Object v : vs) appendInValue(sb, v);
            sb.deleteCharAt(sb.length() - 1).append(')');
        } else {
            appendValue(sb, value);
        }
        sb.append(',');
    }

    public static void main(String[] args) {
        test(new Integer[]{1});
    }

    public static void test(Object value) {
        System.out.println(value instanceof int[]);
        System.out.println(value instanceof Object[]);
    }
}
