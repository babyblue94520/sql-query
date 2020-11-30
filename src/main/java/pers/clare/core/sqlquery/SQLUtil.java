package pers.clare.core.sqlquery;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import java.util.Iterator;


public abstract class SQLUtil {
    private static final boolean camelCase = true;
    private static final char[] get = new char[]{'g', 'e', 't'};

    public static String buildTotalSQL(String sql) {
        return new StringBuilder("select count(*) from(").append(sql)
                .append(")t")
                .toString();
    }

    public static String buildPaginationSQL(
            Pageable pageable
            , String sql
    ) {
        StringBuilder sb = new StringBuilder(sql);
        return toOrder(sb, pageable.getSort())
                .append(" limit ?,?")
                .toString();
    }

    /**
     * 取得排序字串.
     *
     * @param sort the sort
     * @return the order
     */
    public static String toOrder(Sort sort) {
        if (sort == null) return "";
        return toOrder(new StringBuilder(), sort).toString();
    }

    public static StringBuilder toOrder(StringBuilder sb, Sort sort) {
        if (sort == null) return sb;
        Order order;
        Iterator<Order> iterator = sort.iterator();
        while (iterator.hasNext()) {
            order = iterator.next();
            turnCamelCase(sb, order.getProperty())
                    .append(' ')
                    .append(order.getDirection())
                    .append(',');
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - 1, sb.length())
                    .append(" order by ");
        }
        return sb;
    }

    private static StringBuilder turnCamelCase(
            StringBuilder sb
            , String str
    ) {
        char[] bs = str.toCharArray();
        int l = bs.length, c = -1;
        char[] nb = new char[l * 2];
        char b;
        if (camelCase) {
            for (int i = 0; i < l; i++) {
                b = bs[i];
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
            for (int i = 0; i < l; i++) {
                b = bs[i];
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
        rs[0] = '`';
        char c = cs[0];
        rs[1] =(c < 97 ? Character.toLowerCase(c) : c);
        int index = 2;
        for (int i = 1; i < l; i++) {
            c = cs[i];
            if (c < 97) {
                rs[index++] = '_';
                rs[index++] = Character.toLowerCase(c);
            } else {
                rs[index++] = c;
            }
        }
        rs[index++] = '`';
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
