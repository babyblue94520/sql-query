package pers.clare.core.sqlquery.page;


import java.util.Collections;
import java.util.List;

public class Page<T> {
    private int page;
    private int size;
    private List<T> records;
    private long total;

    public static <T> Page<T> of(int page, int size, List<T> records, long total) {
        return new Page<T>(page, size, records, total);
    }

    public static <T> Page<T> empty(Pagination pagination) {
        return new Page(pagination.getPage(), pagination.getSize(), Collections.emptyList(), 0);
    }

    public Page(int page, int size, List<T> records, long total) {
        this.page = page;
        this.size = size;
        this.records = records;
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public List<T> getRecords() {
        return records;
    }

    public long getTotal() {
        return total;
    }
}
