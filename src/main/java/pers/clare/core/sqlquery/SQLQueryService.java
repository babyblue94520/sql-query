package pers.clare.core.sqlquery;

import pers.clare.core.sqlquery.exception.SQLQueryException;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
public class SQLQueryService extends SQLService {

    public SQLQueryService(DataSource write) {
        super(write);
    }

    public SQLQueryService(
            DataSource write
            , DataSource read
    ) {
        super(write, read);
    }

    public <T> T findFirst(
            Class<T> clazz
            , SQLQuery query
    ) {
        try (
                Connection conn = write.getConnection();
        ) {
            PreparedStatement ps = SQLQueryPSUtil.create(conn, query);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getObject(1, clazz);
            }
            return null;
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public Map<String, Object> find(
            SQLQuery query
    ) {
        try (
                Connection conn = write.getConnection();
        ) {
            PreparedStatement ps = SQLQueryPSUtil.create(conn, query);
            return ResultSetUtil.toMap(ps.executeQuery());
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public <T> T find(
            Class<T> clazz
            , SQLQuery query
    ) {
        try (
                Connection conn = write.getConnection();
        ) {
            SQLStore<T> store = SQLStoreFactory.find(clazz);
            PreparedStatement ps = SQLQueryPSUtil.create(conn, query);
            return SQLEntityUtil.toInstance(store.constructorMap, ps.executeQuery());
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public List<Map<String, Object>> findAll(
            SQLQuery query
    ) {
        try (
                Connection conn = write.getConnection();
        ) {
            PreparedStatement ps = SQLQueryPSUtil.create(conn, query);
            return ResultSetUtil.toList(ps.executeQuery());
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public List<Map<String, Object>> findAll(
            SQLQuery query
            , Sort sort
    ) {
        try (
                Connection conn = write.getConnection();
        ) {
            PreparedStatement ps = SQLQueryPSUtil.create(conn, query, sort);
            return ResultSetUtil.toList(ps.executeQuery());
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public Page<Map<String, Object>> findAll(
            SQLQuery query
            , Pageable pageable
    ) {
        try (
                Connection conn = write.getConnection();
        ) {
            String sql = query.toString();
            List<Map<String, Object>> list = ResultSetUtil.toList(SQLQueryPSUtil.create(conn, sql, query, pageable).executeQuery());
            if (list.size() <= pageable.getPageSize()) {
                return new PageImpl<>(list, pageable, list.size());
            }
            ResultSet rs = SQLQueryPSUtil.create(conn, SQLUtil.buildTotalSQL(sql), query).executeQuery();
            if (rs.next()) {
                return new PageImpl<>(list, pageable, rs.getLong(1));
            }
            throw new SQLQueryException("query total error");
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public <T> List<T> findAllFirst(
            Class<T> clazz
            , SQLQuery query
    ) {
        List<T> result = new ArrayList<>();
        try (
                Connection conn = write.getConnection();
        ) {
            PreparedStatement ps = SQLQueryPSUtil.create(conn, query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(rs.getObject(1, clazz));
            }
            return result;
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public <T> List<T> findAll(
            Class<T> clazz
            , SQLQuery query
    ) {
        try (
                Connection conn = write.getConnection();
        ) {
            SQLStore<T> store = SQLStoreFactory.find(clazz);
            PreparedStatement ps = SQLQueryPSUtil.create(conn, query);
            return SQLEntityUtil.toInstances(store.constructorMap, ps.executeQuery());
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public <T> List<T> findAll(
            Class<T> clazz
            , SQLQuery query
            , Sort sort
    ) {
        try (
                Connection conn = write.getConnection();
        ) {
            SQLStore<T> store = SQLStoreFactory.find(clazz);
            return SQLEntityUtil.toInstances(store.constructorMap, SQLQueryPSUtil.create(conn, query, sort).executeQuery());
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public <T> Page<T> findAll(
            Class<T> clazz
            , SQLQuery query
            , Pageable pageable
    ) {
        try (
                Connection conn = write.getConnection();
        ) {
            SQLStore<T> store = SQLStoreFactory.find(clazz);
            String sql = query.toString();
            List<T> list = SQLEntityUtil.toInstances(store.constructorMap, SQLQueryPSUtil.create(conn, sql, query, pageable).executeQuery());
            if (list.size() <= pageable.getPageSize()) {
                return new PageImpl<>(list, pageable, list.size());
            }
            PreparedStatement ps = conn.prepareStatement(SQLUtil.buildTotalSQL(sql));
            SQLQueryPSUtil.setValue(ps, query.values);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new PageImpl<>(list, pageable, rs.getLong(1));
            }
            throw new SQLQueryException("query total error");
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public <T> T insert(
            SQLQuery query
            , Class<T> keyType
    ) {
        try (
                Connection conn = write.getConnection();
        ) {
            PreparedStatement ps = SQLQueryPSUtil.createInsert(conn, query);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getObject(1, keyType) : null;
        } catch (SQLException e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public int update(
            SQLQuery query
    ) {
        try (
                Connection conn = write.getConnection();
        ) {
            return SQLQueryPSUtil.create(conn, query).executeUpdate();
        } catch (SQLException e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

}