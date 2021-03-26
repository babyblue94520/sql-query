package pers.clare.core.sqlquery;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Field;


@Getter
@AllArgsConstructor
public class FieldColumn {
    private final Field field;
    private final boolean id;
    private final boolean auto;
    private final boolean nullable;
    private final boolean insertable;
    private final boolean updatable;
    private final String columnName;
}
