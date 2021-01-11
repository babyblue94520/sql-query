package pers.clare.core.sqlquery;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import pers.clare.core.sqlquery.exception.SQLQueryException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Log4j2
public class SQLService {
    protected DataSource write;
    protected DataSource read;

    protected boolean hasRead;

    public SQLService(DataSource write) {
        this.write = write;
        this.read = write;
    }

    public SQLService(
            DataSource write
            , DataSource read
    ) {
        this.write = write;
        this.read = read;
        hasRead = write != read;
    }

    public DataSource getDataSource(boolean readonly) {
        return readonly ? read : write;
    }

    protected boolean retry(Object result, boolean readonly) {
        return result == null && readonly && hasRead;
    }

    public Map<String, Object> find(
            String sql
            , Object... parameters
    ) {
        return find(false, sql, parameters);
    }

    public Map<String, Object> find(
            boolean readonly
            , String sql
            , Object... parameters
    ) {
        try (
                Connection conn = getDataSource(readonly).getConnection()
        ) {
            Map<String, Object> result = ResultSetUtil.toMap(PreparedStatementUtil.query(conn, sql, parameters));
            if (retry(result, readonly)) {
                return find(false, sql, parameters);
            }
            return result;
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public <T> Map<String, T> find(
            String sql
            , Class<T> valueClass
            , Object... parameters
    ) {
        return find(false, sql, valueClass, parameters);
    }

    public <T> Map<String, T> find(
            boolean readonly
            , String sql
            , Class<T> valueClass
            , Object... parameters
    ) {
        try (
                Connection conn = getDataSource(readonly).getConnection()
        ) {
            Map<String, T> result = ResultSetUtil.toMap(valueClass, PreparedStatementUtil.query(conn, sql, parameters));
            if (retry(result, readonly)) {
                return find(false, sql, valueClass, parameters);
            }
            return result;
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public <T> Set<T> findSet(
            String sql
            , Class<T> valueClass
            , Object... parameters
    ) {
        return findSet(false, sql, valueClass, parameters);
    }

    public <T> Set<T> findSet(
            boolean readonly
            , String sql
            , Class<T> valueClass
            , Object... parameters
    ) {
        try (
                Connection conn = getDataSource(readonly).getConnection()
        ) {
            return ResultSetUtil.toSet(valueClass, PreparedStatementUtil.query(conn, sql, parameters));
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public <T> T findFirst(
            Class<T> clazz
            , String sql
            , Object... parameters
    ) {
        return findFirst(false, clazz, sql, parameters);
    }

    public <T> T findFirst(
            boolean readonly
            , Class<T> clazz
            , String sql
            , Object... parameters
    ) {
        try (
                Connection conn = getDataSource(readonly).getConnection()
        ) {
            T result = ResultSetUtil.to(clazz, PreparedStatementUtil.query(conn, sql, parameters));
            if (retry(result, readonly)) {
                return findFirst(false, clazz, sql, parameters);
            }
            return result;
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public <T> List<Map<String, T>> findAllMap(
            Class<T> clazz
            , String sql
            , Object... parameters
    ) {
        return findAllMap(false, clazz, sql, parameters);
    }

    public <T> List<Map<String, T>> findAllMap(
            boolean readonly
            , Class<T> clazz
            , String sql
            , Object... parameters
    ) {
        try (
                Connection conn = getDataSource(readonly).getConnection()
        ) {
            return ResultSetUtil.toMapList(clazz, PreparedStatementUtil.query(conn, sql, parameters));
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public <T> Page<Map<String, T>> findAllMap(
            Class<T> clazz
            , String sql
            , Pageable pageable
            , Object... parameters
    ) {
        return findAllMap(false, clazz, sql, pageable, parameters);
    }

    public <T> Page<Map<String, T>> findAllMap(
            boolean readonly
            , Class<T> clazz
            , String sql
            , Pageable pageable
            , Object... parameters
    ) {
        try (
                Connection conn = getDataSource(readonly).getConnection()
        ) {
            List<Map<String, T>> list = ResultSetUtil.toMapList(clazz, PreparedStatementUtil.query(conn, sql, pageable, parameters));
            if (list.size() <= pageable.getPageSize()) {
                return new PageImpl<>(list, pageable, list.size());
            }
            ResultSet rs = PreparedStatementUtil.query(conn, SQLUtil.buildTotalSQL(sql), parameters);
            if (rs.next()) {
                return new PageImpl<>(list, pageable, rs.getLong(1));
            }
            throw new SQLQueryException("query total error");
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public <T> List<T> findAll(
            Class<T> clazz
            , String sql
            , Object... parameters
    ) {
        return findAll(false, clazz, sql, parameters);
    }

    public <T> List<T> findAll(
            boolean readonly
            , Class<T> clazz
            , String sql
            , Object... parameters
    ) {
        try (
                Connection conn = getDataSource(readonly).getConnection()
        ) {
            return ResultSetUtil.toList(clazz, PreparedStatementUtil.query(conn, sql, parameters));
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public <T> T insert(
            String sql
            , Class<T> keyType
            , Object... parameters
    ) {
        try (
                Connection conn = write.getConnection()
        ) {
            Statement s = PreparedStatementUtil.executeInsert(conn, sql, parameters);
            if (s.getUpdateCount() == 0) return null;
            ResultSet rs = s.getGeneratedKeys();
            return rs.next() ? rs.getObject(1, keyType) : null;
        } catch (SQLException e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public int update(
            String sql
            , Object... parameters
    ) {
        try (
                Connection conn = write.getConnection()
        ) {
            return PreparedStatementUtil.execute(conn, sql, parameters);
        } catch (SQLException e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }
}
