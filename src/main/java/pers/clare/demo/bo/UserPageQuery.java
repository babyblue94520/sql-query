package pers.clare.demo.bo;

import lombok.Getter;
import lombok.Setter;
import pers.clare.core.sqlquery.page.Pagination;

@Getter
@Setter
public class UserPageQuery {
    private Pagination pagination;
    private Long startTime;
    private Long endTime;
    private Long id;
    private String name;
}
