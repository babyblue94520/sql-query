package pers.clare.demo.data.entity;

import lombok.Getter;
import lombok.Setter;
import pers.clare.core.sqlquery.SQLStore;
import pers.clare.core.sqlquery.SQLStoreFactory;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class Test {
    public static final SQLStore STORE = SQLStoreFactory.build(Test.class, true);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public Test() {
        this.id = null;
        this.name = null;
    }

    public Test(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
