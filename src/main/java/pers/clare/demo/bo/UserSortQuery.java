package pers.clare.demo.bo;

import lombok.Getter;
import lombok.Setter;
import pers.clare.core.sqlquery.page.Pagination;
import pers.clare.core.sqlquery.page.Sort;

@Getter
@Setter
public class UserSortQuery {
    private Sort sort;
    private Long startTime;
    private Long endTime;
    private Long id;
    private String name;
}
