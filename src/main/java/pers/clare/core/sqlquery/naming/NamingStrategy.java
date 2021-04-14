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
        sb.append(toLowerCase(c));
        for (int i = 1; i < l; i++) {
            c = cs[i];
            if (c > 64 && c < 91) {
                c = toLowerCase(c);
                sb.append('_');
            }
            sb.append(c);
        }
        return sb;
    }

    public static StringBuilder sortTurnCamelCase(StringBuilder sb, String name) {
        int l = name.length();
        char[] cs = name.toCharArray();
        // 避開開頭空白或者換行
        int start = 0;
        for (char c : cs) {
            if (c != ' ' || c != '\n') break;
            start++;
        }
        char c = cs[start++];
        sb.append(toLowerCase(c));
        boolean turn = true;
        for (int i = start; i < l; i++) {
            c = cs[i];
            if (c == ' ') turn = false; // stop when blank
            if (turn && c > 64 && c < 91) {
                c = toLowerCase(c);
                sb.append('_');
            }
            sb.append(c);
        }
        return sb;
    }

    private static char toLowerCase(char c) {
        return Character.toLowerCase(check(c));
    }

    private static char check(char c) {
        switch (c) {
            case ';':
                throw new SQLQueryException("Not a legal character ';'");
        }
        return c;
    }
}
