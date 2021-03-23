package pers.clare.core.sqlquery.page;

public class Pagination {
    private Integer page;
    private Integer size;
    private String[] sorts;

    public static Pagination of(Integer page, Integer size) {
        return new Pagination(page, size);
    }

    public static Pagination of(Integer page, Integer size, String[] sorts) {
        return new Pagination(page, size, sorts);
    }

    public Pagination() {
        this(0, 20, null);
    }

    public Pagination(Integer page, Integer size) {
        this(page, size, null);
    }

    public Pagination(Integer page, Integer size, String[] sorts) {
        this.page = page;
        this.size = size;
        this.sorts = sorts;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String[] getSorts() {
        return sorts;
    }

    public void setSorts(String[] sorts) {
        this.sorts = sorts;
    }
}
