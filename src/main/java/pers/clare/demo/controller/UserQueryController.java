package pers.clare.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.clare.core.sqlquery.page.Page;
import pers.clare.core.sqlquery.page.Pagination;
import pers.clare.core.sqlquery.page.Sort;
import pers.clare.demo.data.entity.User;
import pers.clare.demo.data.sql.UserQueryRepository;
import pers.clare.demo.vo.User2;

import java.util.Collection;
import java.util.List;

@RequestMapping("user/simple")
@RestController
public class UserQueryController {

    @Autowired
    private UserQueryRepository userQueryRepository;

    @GetMapping("one/id")
    public Long findId(
    ) throws Exception {
        return userQueryRepository.findId();
    }

    @GetMapping("one")
    public User find(
    ) throws Exception {
        return userQueryRepository.find();
    }

    @GetMapping("one2")
    public User2 find2(
    ) throws Exception {
        return userQueryRepository.find2();
    }

    @GetMapping("map")
    public Collection findAllSimpleMap(
    ) throws Exception {
        return userQueryRepository.findAllSimpleMap();
    }

    @GetMapping("map/2")
    public Collection findAllSimpleMap(
            Pagination pagination
    ) throws Exception {
        return userQueryRepository.findAllMap(pagination);
    }

    @GetMapping
    public Collection findAllSimple(
    ) throws Exception {
        return userQueryRepository.findAllSimple();
    }

    @GetMapping("id")
    public Collection findAllId(
            Pagination pagination
    ) throws Exception {
        return userQueryRepository.findAllId(pagination);
    }

    @GetMapping("set")
    public Collection findAllSimpleSetMap(
    ) throws Exception {
        return userQueryRepository.findAllSimpleSetMap();
    }

    @GetMapping("time")
    public Collection findAllSimpleSetMapString(
            Pagination pagination
    ) throws Exception {
        return userQueryRepository.findAllTime(pagination);
    }

    @GetMapping("sort")
    public List<User> sort(
            Sort sort
    ) throws Exception {
        return userQueryRepository.findAllId(sort);
    }

    @GetMapping("page/map")
    public Page mapPage(
            Pagination pagination
    ) throws Exception {
        return userQueryRepository.mapPage(pagination);
    }

    @GetMapping("page")
    public Page<User> page(
            Pagination pagination
            , Long startTime
            , Long endTime
            , Long id
            , String name
    ) throws Exception {
        return userQueryRepository.page(
                id == null ? "" : "and id = :id"
                , StringUtils.isEmpty(name) ? "" : "and name like :name"
                , pagination
                , startTime
                , endTime
                , id
                , name
        );
    }

    @GetMapping("2")
    public Collection findAllSimple(
            String name
            , int page
            , int size
    ) throws Exception {
        return userQueryRepository.findAllSimple(name, page, size);
    }

    @GetMapping("xml")
    public Collection findAllMapXML(
            Pagination pagination
    ) throws Exception {
        return userQueryRepository.findAllMapXML(pagination);
    }

    @GetMapping("page/xml")
    public Page<User> pageMapXML(
            Pagination pagination
            , Long startTime
            , Long endTime
            , Long id
            , String name
    ) throws Exception {
        return userQueryRepository.pageMapXML(
                id == null ? "" : "and id = :id"
                , StringUtils.isEmpty(name) ? "" : "and name like :name"
                , pagination
                , startTime
                , endTime
                , id
                , name
        );
    }
}
