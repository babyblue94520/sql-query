package pers.clare.core.sqlquery;

import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import pers.clare.core.sqlquery.annotation.Sql;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.regex.Pattern;

/**
 * 利用XML 維護SQL
 */
@Log4j2
public abstract class SQLInjector {
    private static final Pattern spacePattern = Pattern.compile("(\\s|\t|\r|\n)+");
    private static final String root = "sqlquery/";


    public static void inject(Object target) {
        if (target == null) return;
        load(target, target.getClass());
    }

    private static void load(Object target, Class<?> clazz) {
        String xmlPath = root + clazz.getName().replaceAll("\\.", "/") + ".xml";

        try (InputStream is = clazz.getClassLoader().getResourceAsStream(xmlPath)) {
            if (is != null) {
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
                Field[] fields = clazz.getDeclaredFields();
                Sql sql;
                String tagName, content;
                for (Field field : fields) {
                    sql = field.getAnnotation(Sql.class);
                    if (sql == null) continue;
                    tagName = sql.name();
                    if ("".equals(tagName)) tagName = field.getName();
                    content = sql.query();
                    NodeList list = doc.getElementsByTagName(tagName);
                    if (list.getLength() > 0) {
                        content = spacePattern.matcher(list.item(0).getTextContent()).replaceAll(" ").trim();
                    }
                    if (StringUtils.isEmpty(content)) throw new Error(field.getName() + " cannot load sql");

                    field.setAccessible(true);
                    if (String.class == field.getType()) {
                        field.set(target, content);
                    } else if (SQLQueryBuilder.class == field.getType()) {
                        field.set(target, new SQLQueryBuilder(content));
                    } else if (SQLQueryReplaceBuilder.class == field.getType()) {
                        field.set(target, new SQLQueryReplaceBuilder(content));
                    } else {
                        field.set(target, null);
                    }
                }
                log.info("load {}", xmlPath);
            } else {
                throw new Error(xmlPath + " isn't exist");
            }
            if (clazz.getSuperclass() != Object.class) {
                load(target, clazz.getSuperclass());
            }
        } catch (Exception e) {
            throw new Error(e.getMessage(), e);
        }
    }
}
