package pers.clare.core.sqlquery.old;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SQLQueryBuilder {

    // SQL 字串切割陣列
    private final char[][] sqlParts;

    // 需要被替換成SQL的key陣列
    private final Map<String, Integer> keyIndex;

    // 替換的位置對應到的 in key index
    private final Integer[] inIndex;

    public SQLQueryBuilder(String sql) {
        this(sql.toCharArray());
    }

    public SQLQueryBuilder(char[] sqlChars) {
        int count = findKeyCount(sqlChars);
        keyIndex = new HashMap<>(count);
        inIndex = new Integer[count + count + 1];
        sqlParts = new char[inIndex.length][];
        char c, p1 = 0, p2 = 0, p3 = 0, p4 = 0;
        int l = sqlChars.length, listCount = 0, keyCount = 0, tempLength = 0, keyLength;
        char[] temp = new char[l];
        char[] key = new char[l];
        for (int i = 0; i < l; i++) {
            c = sqlChars[i];
            switch (c) {
                case ':':
                    if (sqlChars[i + 1] != '=') {
                        if (p1 == ' ' && p2 == 'i' && p3 == 'n' && p4 == ' ') {
                            sqlParts[listCount] = new char[tempLength];
                            System.arraycopy(temp, 0, sqlParts[listCount++], 0, tempLength);
                            inIndex[listCount] = keyCount;
                            sqlParts[listCount++] = null;
                            tempLength = 0;
                        } else {
                            temp[tempLength++] = '?';
                        }
                        keyLength = 0;
                        i++;
                        boolean b = false;
                        for (; i < l; i++) {
                            c = sqlChars[i];
                            switch (c) {
                                case ' ':
                                case ',':
                                case ')':
                                    temp[tempLength++] = c;
                                    b = true;
                                    break;
                                default:
                                    key[keyLength++] = c;

                            }
                            if (b) {
                                break;
                            }
                        }
                        keyIndex.put(new String(key, 0, keyLength), keyCount++);
                        break;
                    }
                case ' ':
                    if (p4 == ' ') {
                        break;
                    }
                default:
                    p1 = p2;
                    p2 = p3;
                    p3 = p4;
                    p4 = c;
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

    public SQLQuery build() {
        return new SQLQuery(sqlParts, keyIndex, inIndex);
    }

    public int keySize(String key){
        return keyIndex.size();
    }

    public boolean hasKey(String key){
        return keyIndex.containsKey(key);
    }

    public static int findKeyCount(char[] sqlChars) {
        int count = 0;
        for (int i = 0, l = sqlChars.length; i < l; i++) {
            if (sqlChars[i] == ':' && sqlChars[i + 1] != '=') {
                count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        System.out.println(
                new SQLQueryBuilder("select * from user where id=:id and name like :name" +
                        " and age in :age" +
                        " and bb in :bb" +
                        " and cc in :cc"
                )
                        .build()
                        .value("id", 1)
                        .value("name", "tes%")
                        .value("age", 1)
                        .value("bb", new int[]{1, 2}, new int[]{1, 2})
                        .value("cc", Arrays.asList(new int[]{1, 2}, new int[]{1, 2}))
        );
    }

}
