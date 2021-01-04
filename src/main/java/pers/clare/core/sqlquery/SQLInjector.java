package pers.clare.core.sqlquery;

import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pers.clare.core.sqlquery.annotation.Sql;

import javax.xml.parsers.*;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 利用XML 維護SQL
 */
@Log4j2
public class SQLInjector {
    private static final Pattern SPACE_PATTERN = Pattern.compile("(\\s|\t|\r|\n)+");
    private static final String ROOT = "sqlquery/";
    private static final DocumentBuilder documentBuilder;

    static {
        try {
            documentBuilder = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new Error(e.getMessage(), e);
        }
    }

    private SQLInjector() {
    }

    public static void inject(Object target) {
        if (target == null) return;
        load(target.getClass(), target);
    }

    private static void load(Class<?> clazz, Object target) {
        try {
            Map<String, String> contents = getContents(clazz);
            Field[] fields = clazz.getDeclaredFields();
            Sql sql;
            String tagName, content;
            for (Field field : fields) {
                sql = field.getAnnotation(Sql.class);
                if (sql == null) continue;
                tagName = sql.name();
                if (StringUtils.isEmpty(tagName)) tagName = field.getName();
                content = contents.get(tagName);
                if (StringUtils.isEmpty(content)) content = sql.query();
                if (StringUtils.isEmpty(content))
                    throw new Error(String.format("%s can not load sql", field.getName()));
                Object obj = Modifier.isStatic(field.getModifiers()) ? clazz : target;
                field.setAccessible(true);
                field.set(obj, convert(content));
            }
            if (clazz.getSuperclass() != Object.class) load(clazz.getSuperclass(), target);
        } catch (Exception e) {
            throw new Error(e.getMessage(), e);
        }
    }

    public static Object convert(String sql) {
        char[] cs = sql.toCharArray();
        if (SQLQueryReplaceBuilder.findKeyCount(cs) > 0) {
            return new SQLQueryReplaceBuilder(cs);
        }
        if (SQLQueryBuilder.findKeyCount(cs) > 0) {
            return new SQLQueryBuilder(cs);
        }
        return sql;
    }

    public static Map<String, String> getContents(Class<?> clazz) {
        String xmlPath = toPath(clazz);
        Map<String, String> map = new HashMap<>();
        try (InputStream is = clazz.getClassLoader().getResourceAsStream(xmlPath)) {
            if (is == null) return map;
            log.info("load {}", xmlPath);
            Document doc = documentBuilder.parse(is);
            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            Node node;
            String content;
            for (int i = 0; i < nodeList.getLength(); i++) {
                node = nodeList.item(i);
                if (Node.ELEMENT_NODE != node.getNodeType()) continue;
                content = node.getTextContent();
                if (StringUtils.isEmpty(content)) continue;
                map.put(node.getNodeName(), SPACE_PATTERN.matcher(content).replaceAll(" ").trim());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return map;
    }

    private static String toPath(Class<?> clazz) {
        return ROOT + clazz.getName().replaceAll("\\.", "/") + ".xml";
    }
}