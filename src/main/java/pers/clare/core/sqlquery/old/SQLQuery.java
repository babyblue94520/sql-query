package pers.clare.core.sqlquery.old;

import lombok.extern.log4j.Log4j2;

import java.util.Collection;
import java.util.Map;

/**
 * 建造動態產生PreparedStatement，主要是因為Mysql不支持 setArray，
 * 當條件中有in時，則無法透過PreparedStatement優化。
 * 是執行緒不安全的class，所以多執行續環境下，必須透過SQLQueryBuilder重新建造
 */
@Log4j2
public class SQLQuery {
    private final char[][] sqlParts;

    private final Map<String, Integer> keyIndex;

    private final Integer[] inIndex;

    private final int keyCount;

    final Object[] values;


    SQLQuery(char[][] sqlParts, Map<String, Integer> keyIndex, Integer[] inIndex) {
        this.sqlParts = sqlParts;
        this.keyIndex = keyIndex;
        this.inIndex = inIndex;
        this.keyCount = keyIndex.size();
        this.values = new Object[keyCount];
    }

    public SQLQuery value(String key, Object value) {
        if (value == null || key == null) return this;
        Integer index = keyIndex.get(key);
        if (index == null) return this;
        values[index] = value;
        return this;
    }

    public SQLQuery value(String key, Object... value) {
        if (value == null || key == null) return this;
        Integer index = keyIndex.get(key);
        if (index == null) return this;
        values[index] = value;
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        char[] cs;
        Integer in;
        for (int i = 0, l = sqlParts.length; i < l; i++) {
            cs = sqlParts[i];
            if (cs == null) {
                in = inIndex[i];
                if (in == null) continue;
                appendIn(sb, values[in]);
            } else {
                sb.append(cs);
            }
        }
        return sb.toString();
    }

    private static void appendIn(
            StringBuilder sb
            , Object value
    ) {
        if (value == null) return;
        Class<?> valueClass = value.getClass();
        if (valueClass.isArray() || Collection.class.isAssignableFrom(valueClass)) {
            appendInValue(sb, value);
            sb.deleteCharAt(sb.length() - 1);
        } else {
            sb.append('(');
            appendInValue(sb, value);
            sb.deleteCharAt(sb.length() - 1).append(')');
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
                if (vs.length == 0) throw new IllegalArgumentException("SQL WHERE IN does't empty value");
                for (Object v : vs) appendInValue(sb, v);
            } else if (value instanceof int[]) {
                int[] vs = (int[]) value;
                if (vs.length == 0) throw new IllegalArgumentException("SQL WHERE IN does't empty value");
                for (int v : vs) appendInValue(sb, v);
            } else if (value instanceof long[]) {
                long[] vs = (long[]) value;
                if (vs.length == 0) throw new IllegalArgumentException("SQL WHERE IN does't empty value");
                for (long v : vs) appendInValue(sb, v);
            } else if (value instanceof char[]) {
                char[] vs = (char[]) value;
                if (vs.length == 0) throw new IllegalArgumentException("SQL WHERE IN does't empty value");
                for (char v : vs) appendInValue(sb, v);
            }
            sb.deleteCharAt(sb.length() - 1).append(')');
        } else if (Collection.class.isAssignableFrom(valueClass)) {
            Collection<Object> vs = (Collection<Object>) value;
            if (vs.size() == 0) throw new IllegalArgumentException("SQL WHERE IN does't empty value");
            sb.append('(');
            for (Object v : vs) appendInValue(sb, v);
            sb.deleteCharAt(sb.length() - 1).append(')');
        } else {
            sb.append('?');
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
