package pers.clare.demo.data.sql;

import org.springframework.stereotype.Repository;
import pers.clare.core.sqlquery.annotation.Sql;
import pers.clare.core.sqlquery.page.Page;
import pers.clare.core.sqlquery.page.Pagination;
import pers.clare.core.sqlquery.repository.SQLRepository;
import pers.clare.demo.data.entity.User;
import pers.clare.demo.vo.SimpleUser;
import pers.clare.demo.vo.User2;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserQueryRepository extends SQLRepository {
    @Sql("select id from user")
    Long findId();

    @Sql("select * from user")
    User find();

    @Sql("select id,name,account,email,locked,enabled from user")
    User2 find2();

    @Sql("select id,name from user limit 0,10")
    List findAllSimpleMap();

    @Sql("select id,name from user limit 0,10")
    List<SimpleUser> findAllSimple();

    @Sql("select id,name from user")
    List<Map<String, Object>> findAllMap(Pagination pagination);

    @Sql("select id,name from user")
    List<SimpleUser> findAll(Pagination pagination);

    @Sql("select id from user")
    List<Long> findAllId(Pagination pagination);

    @Sql("select id,name from user limit 0,10")
    Set findAllSimpleSetMap();

    @Sql("select id,name from user limit 0,10")
    Set<Map<String, String>> findAllSimpleSetMapString();

    @Sql("select create_time from user")
    Set<Long> findAllTime(Pagination pagination);

    @Sql("select * from user")
    Page<Map> mapPage(Pagination pagination);

    @Sql("select * from user where create_time between :startTime and :endTime {andId}{andName}")
    Page<User> page(
            String andId
            , String andName
            , Pagination pagination
            , Long startTime
            , Long endTime
            , Long id
            , String name
    );

    @Sql("select id,name from user where name like ? limit ?,?")
    List<SimpleUser> findAllSimple(String name, int page, int size);

    /**
     * use method name to get sql from XML
     */
    List<Map<String, Object>> findAllMapXML(Pagination pagination);

    /**
     * use name to get sql from XML
     */
    @Sql(name = "pageMapXML")
    Page<User> pageMapXML(
            String andId
            , String andName
            , Pagination pagination
            , Long startTime
            , Long endTime
            , Long id
            , String name
    );
}
