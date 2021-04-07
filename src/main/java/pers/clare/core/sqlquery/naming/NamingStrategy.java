package pers.clare.core.sqlquery.naming;

import pers.clare.core.sqlquery.exception.SQLQueryException;

public class NamingStrategy {

    public static String turnCamelCase(String name) {
        return turnCamelCase(new StringBuilder(), name).toString();
    }

    public static StringBuilder turnCamelCase(StringBuilder sb, String name) {
        int l = name.length();
        char[] cs = name.toCharArray();
        char c = cs[0];
        sb.append(Character.toLowerCase(check(c)));
        for (int i = 1; i < l; i++) {
            c = cs[i];
            if (c > 64 && c < 91) {
                c = Character.toLowerCase(check(c));
                sb.append('_');
            }
            sb.append(c);
        }
        return sb;
    }

    /**
     * @param c
     * @return
     */
    private static char check(char c) {
        switch (c) {
            case ';':
                throw new SQLQueryException("Not a legal character ';'");
        }
        return c;
    }
}
