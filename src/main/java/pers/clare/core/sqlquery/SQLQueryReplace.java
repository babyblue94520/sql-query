package pers.clare.core.sqlquery;

import java.nio.CharBuffer;

/**
 * SQL動態替換SQL工廠，是執行緒不安全的class，所以多執行續環境下，必須透過SQLQueryReplaceBuilder重新建造.
 */
public class SQLQueryReplace {
    private final char[][] sqlParts;

    private final String[] keys;

    private final Integer[] keyIndex;

    private final String[] values;

    private final int keyCount;

    /**
     * Instantiates a new SQL query factory builder.
     *
     * @param sqlParts the sql list
     * @param keys     the keys
     * @param keyIndex the key indexs
     */
    public SQLQueryReplace(char[][] sqlParts, String[] keys, Integer[] keyIndex) {
        this.sqlParts = sqlParts;
        this.keys = keys;
        this.keyIndex = keyIndex;
        this.keyCount = keys.length;
        this.values = new String[keyCount];
    }

    public SQLQueryReplace replace(String key, String sql) {
        return replace(key, sql, false);
    }

    public SQLQueryReplace replace(String key, String sql, Boolean ignore) {
        if (key == null || sql == null || ignore) return this;
        for (int i = 0; i < keyCount; i++) {
            if (key.equals(keys[i])) {
                values[i] = sql;
            }
        }
        return this;
    }

    public SQLQueryBuilder buildQueryBuilder() {
        return new SQLQueryBuilder(toString());
    }

    public SQLQuery buildQuery() {
        return buildQueryBuilder().build();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        char[] cs;
        String str;
        Integer key;
        for (int i = 0, l = sqlParts.length; i < l; i++) {
            cs = sqlParts[i];
            if (cs == null) {
                key = keyIndex[i];
                if (key == null) continue;
                str = values[key];
                if (str == null) continue;
                sb.append(str);
            } else {
                sb.append(cs);
            }
        }
        return sb.toString();
    }
}
