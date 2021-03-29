package pers.clare.core.sqlquery.naming;

public class NamingStrategy {

    public static String turnCamelCase(String name) {
        return turnCamelCase(new StringBuilder(), name).toString();
    }

    public static StringBuilder turnCamelCase(StringBuilder sb, String name) {
        int l = name.length();
        char[] cs = name.toCharArray();
        char c = cs[0];
        sb.append(c < 97 ? Character.toLowerCase(c) : c);
        for (int i = 1; i < l; i++) {
            c = cs[i];
            if (c < 97) {
                c = Character.toLowerCase(c);
                sb.append('_');
            }
            sb.append(c);
        }
        return sb;
    }
}
