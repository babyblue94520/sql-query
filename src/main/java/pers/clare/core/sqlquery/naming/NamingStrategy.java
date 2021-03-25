package pers.clare.core.sqlquery.naming;

public class NamingStrategy {
    private static final boolean camelCase = true;
    private static final char[] get = new char[]{'g', 'e', 't'};

    public static StringBuilder turnCamelCase(
            StringBuilder sb
            , String str
    ) {
        char[] bs = str.toCharArray();
        int l = bs.length, c = -1;
        char[] nb = new char[l * 2];
        char b;
        if (camelCase) {
            for (char value : bs) {
                b = value;
                // 移除 ';' 結束字元
                if (b == 59) continue;
                // 紀錄最後的 '.'
                if (b == 46) {
                    c = -1;
                    continue;
                }
                // 駝峰換底線
                if (b > 64 && b < 91) {
                    nb[++c] = '_';
                    b = Character.toLowerCase(b);
                }
                nb[++c] = b;
            }
        } else {
            for (char value : bs) {
                b = value;
                // 移除 ';' 結束字元
                if (b == 59) continue;
                // 紀錄最後的 '.'
                if (b == 46) {
                    c = -1;
                    continue;
                }
                nb[++c] = b;
            }
        }
        sb.append(nb, 1, c);
        return sb;
    }

    public static String convert(String name) {
        int l = name.length();
        char[] cs = name.toCharArray();
        char[] rs = new char[l * 2 + 2];
        char c = cs[0];
        rs[0] = (c < 97 ? Character.toLowerCase(c) : c);
        int index = 1;
        for (int i = 1; i < l; i++) {
            c = cs[i];
            if (c < 97) {
                rs[index++] = '_';
                rs[index++] = Character.toLowerCase(c);
            } else {
                rs[index++] = c;
            }
        }
        return new String(rs, 0, index);
    }

    public static String toGetterName(String name) {
        int l = name.length();
        char[] rs = new char[l + 3];
        System.arraycopy(get, 0, rs, 0, 3);
        name.getChars(0, l, rs, 3);
        rs[3] = Character.toUpperCase(rs[3]);
        return new String(rs);
    }
}
