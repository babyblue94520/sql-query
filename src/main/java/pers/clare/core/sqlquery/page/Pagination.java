package pers.clare.core.sqlquery.page;

public class Pagination {
    private Integer page;
    private Integer size;
    private String[] sorts;

    public static Pagination of(Integer page, Integer size) {
        return new Pagination(page, size, null);
    }

    public static Pagination of(Integer page, Integer size, String... sorts) {
        return new Pagination(page, size, sorts);
    }

    public Pagination(Integer page, Integer size, String[] sorts) {
        this.page = page;
        this.size = size;
        this.sorts = sorts;
    }

    public Integer getPage() {
        return page;
    }

    public Integer getSize() {
        return size;
    }

    public String[] getSorts() {
        return sorts;
    }

    public Pagination next() {
        return Pagination.of(page + 1, size, sorts);
    }
}
