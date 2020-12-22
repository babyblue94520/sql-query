package pers.clare.core.sqlquery;

import java.util.Arrays;

/**
 * 解析SQL需要被替換的資料，建造不需要重新解析SQL的SQL替換工廠
 */
public class SQLQueryReplaceBuilder {

    // SQL 字串切割陣列
    private final char[][] sqlParts;

    // 需要被替換成SQL的key陣列
    private final String[] keys;

    // 替換的位置對應到的key index
    private final Integer[] keyIndex;

    public SQLQueryReplaceBuilder(String sql) {
        this(sql.toCharArray());
    }

    public SQLQueryReplaceBuilder(char[] sqlChars ) {
        int count = findKeyCount(sqlChars);
        keys = new String[count];
        keyIndex = new Integer[count + count + 1];
        sqlParts = new char[keyIndex.length][];
        char c;
        int l = sqlChars.length, listCount = 0, keyCount = 0, tempLength = 0, keyLength;
        char[] temp = new char[l];
        char[] key = new char[l];
        for (int i = 0; i < l; i++) {
            c = sqlChars[i];
            if (c == '{') {
                sqlParts[listCount] = new char[tempLength];
                System.arraycopy(temp, 0, sqlParts[listCount++], 0, tempLength);
                keyIndex[listCount] = keyCount;
                sqlParts[listCount++] = null;
                tempLength = 0;
                keyLength = 0;
                i++;
                for (; i < l; i++) {
                    c = sqlChars[i];
                    if (c == '}') {
                        keys[keyCount++] = new String(key, 0, keyLength);
                        keyLength = 0;
                        break;
                    }
                    key[keyLength++] = c;
                }
                if (keyLength > 0) {
                    sqlParts[listCount] = new char[keyLength];
                    System.arraycopy(key, 0, sqlParts[listCount++], 0, keyLength);
                }
            } else {
                temp[tempLength++] = c;
            }
        }
        if (tempLength > 0) {
            sqlParts[listCount] = new char[tempLength];
            System.arraycopy(temp, 0, sqlParts[listCount], 0, tempLength);
        } else {
            sqlParts[listCount] = null;
        }
    }

    public SQLQueryReplace build() {
        return new SQLQueryReplace(sqlParts, keys, keyIndex);
    }

    public static int findKeyCount(char[] sqlChars) {
        int count = 0;
        for (int i = 0, l = sqlChars.length; i < l; i++) {
            if (sqlChars[i] == '{') {
                count++;
            }
        }
        return count;
    }


    public static void main(String[] args) {
        System.out.println(
                new SQLQueryReplaceBuilder("select * from user where {id} {name} " +
                        " and age in :age" +
                        " and bb in :bb" +
                        " and cc in :cc"
                ).build()
                        .replace("id","id=:id")
                        .replace("name","and name like :name")
                        .buildQuery()
                        .value("id", 1)
                        .value("name", "tes%")
                        .value("age", 1)
                        .value("bb", new int[]{1, 2}, new int[]{1, 2})
                        .value("cc", Arrays.asList(new int[]{1, 2}, new int[]{1, 2})).toString()
        );
    }
}
