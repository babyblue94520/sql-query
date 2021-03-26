package pers.clare.demo.data.entity;

import lombok.*;

import javax.persistence.*;

@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "varchar(100) default ''")
    private String account;

    @Column(nullable = false, columnDefinition = "varchar(100) default ''")
    private String name;

    @Column(nullable = false, columnDefinition = "varchar(200) default ''")
    private String email;

    @Column(nullable = false, columnDefinition = "int default '0'")
    private Integer count;

    @Column(nullable = false, columnDefinition = "boolean default 'false'")
    private Boolean locked;

    @Column(nullable = false, columnDefinition = "boolean default 'true'")
    private Boolean enabled;

    @Column(name = "update_time")
    private Long updateTime;

    @Column(name = "update_user")
    private Long updateUser;

    @Column(name = "create_time", updatable = false)
    private Long createTime;

    @Column(name = "create_user", updatable = false)
    private Long createUser;
}
