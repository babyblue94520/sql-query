package pers.clare.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.clare.core.sqlquery.page.Page;
import pers.clare.core.sqlquery.page.Pagination;
import pers.clare.demo.data.entity.User;
import pers.clare.demo.data.sql.SimpleUserRepository;

import java.util.Collection;

@RequestMapping("user/simple")
@RestController
public class SimpleUserController {

    @Autowired
    private SimpleUserRepository simpleUserRepository;

    @GetMapping("one/id")
    public Long findId(
    ) throws Exception {
        return simpleUserRepository.findId();
    }

    @GetMapping("one")
    public User find(
    ) throws Exception {
        return simpleUserRepository.find();
    }

    @GetMapping("map")
    public Collection findAllSimpleMap(
    ) throws Exception {
        return simpleUserRepository.findAllSimpleMap();
    }

    @GetMapping("map/2")
    public Collection findAllSimpleMap(
            Pagination pagination
    ) throws Exception {
        return simpleUserRepository.findAllMap(pagination);
    }

    @GetMapping
    public Collection findAllSimple(
    ) throws Exception {
        return simpleUserRepository.findAllSimple();
    }

    @GetMapping("id")
    public Collection findAllId(
            Pagination pagination
    ) throws Exception {
        return simpleUserRepository.findAllId(pagination);
    }

    @GetMapping("set")
    public Collection findAllSimpleSetMap(
    ) throws Exception {
        return simpleUserRepository.findAllSimpleSetMap();
    }

    @GetMapping("time")
    public Collection findAllSimpleSetMapString(
            Pagination pagination
    ) throws Exception {
        return simpleUserRepository.findAllTime(pagination);
    }

    @GetMapping("page/map")
    public Page mapPage(
            Pagination pagination
    ) throws Exception {
        return simpleUserRepository.mapPage(pagination);
    }

    @GetMapping("page")
    public Page<User> page(
            Pagination pagination
            , Long startTime
            , Long endTime
            , Long id
            , String name
    ) throws Exception {
        return simpleUserRepository.page(
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
        return simpleUserRepository.findAllSimple(name, page, size);
    }

    @GetMapping("xml")
    public Collection findAllMapXML(
            Pagination pagination
    ) throws Exception {
        return simpleUserRepository.findAllMapXML(pagination);
    }

    @GetMapping("page/xml")
    public Page<User> pageMapXML(
            Pagination pagination
            , Long startTime
            , Long endTime
            , Long id
            , String name
    ) throws Exception {
        return simpleUserRepository.pageMapXML(
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
