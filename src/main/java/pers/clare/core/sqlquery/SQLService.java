package pers.clare.core.sqlquery;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import pers.clare.core.sqlquery.exception.SQLQueryException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Log4j2
public class SQLService {
    protected DataSource write;
    protected DataSource read;

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
    }

    public DataSource getDataSource(boolean readonly) {
        return readonly ? read : write;
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
                Connection conn = getDataSource(readonly).getConnection();
        ) {
            return ResultSetUtil.toMap(PreparedStatementUtil.create(conn, sql, parameters).executeQuery());
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
                Connection conn = getDataSource(readonly).getConnection();
        ) {
            return ResultSetUtil.to(clazz, PreparedStatementUtil.create(conn, sql, parameters).executeQuery());
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public List<Map<String, Object>> findAll(
            String sql
            , Object... parameters
    ) {
        return findAll(false, sql, parameters);
    }

    public List<Map<String, Object>> findAll(
            boolean readonly
            , String sql
            , Object... parameters
    ) {
        try (
                Connection conn = getDataSource(readonly).getConnection();
        ) {
            return ResultSetUtil.toList(PreparedStatementUtil.create(conn, sql, parameters).executeQuery());
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public Page<Map<String, Object>> findAll(
            String sql
            , Pageable pageable
            , Object... parameters
    ) {
        return findAll(false, sql, pageable, parameters);
    }

    public Page<Map<String, Object>> findAll(
            boolean readonly
            , String sql
            , Pageable pageable
            , Object... parameters
    ) {
        try (
                Connection conn = getDataSource(readonly).getConnection()
        ) {
            List<Map<String, Object>> list = ResultSetUtil.toList(PreparedStatementUtil.create(conn, sql, pageable, parameters).executeQuery());
            if (list.size() <= pageable.getPageSize()) {
                return new PageImpl<>(list, pageable, list.size());
            }
            ResultSet rs = PreparedStatementUtil.create(conn, SQLUtil.buildTotalSQL(sql), parameters).executeQuery();
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
            return ResultSetUtil.toList(clazz, PreparedStatementUtil.create(conn, sql, parameters)
                    .executeQuery());
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public <T> Set<T> findSet(
            boolean readonly
            , Class<T> clazz
            , String sql
            , Object... parameters
    ) {
        try (
                Connection conn = getDataSource(readonly).getConnection()
        ) {
            return ResultSetUtil.toSet(clazz, PreparedStatementUtil.create(conn, sql, parameters)
                    .executeQuery());
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
                Connection conn = write.getConnection();
        ) {
            PreparedStatement ps = PreparedStatementUtil.createInsert(conn, sql, parameters);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
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
                Connection conn = write.getConnection();
        ) {
            return PreparedStatementUtil.create(conn, sql, parameters).executeUpdate();
        } catch (SQLException e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }
}
