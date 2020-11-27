package pers.clare.core.data;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;

import java.util.Locale;
import java.util.regex.Pattern;


/**
 * 为了处理 Quartz 全大写命名
 */
public class MyPhysicalNamingStrategy extends SpringPhysicalNamingStrategy {


    /**
     * 不处理全大写名称
     * @param name
     * @param quoted
     * @param jdbcEnvironment
     * @return
     */
    protected Identifier getIdentifier(String name, boolean quoted, JdbcEnvironment jdbcEnvironment) {
        if(!Pattern.compile("^[A-Z_]+$").matcher(name).find()){
            if (this.isCaseInsensitive(jdbcEnvironment)) {
                name = name.toLowerCase(Locale.ROOT);
            }
        }

        return new Identifier(name, quoted);
    }
}
