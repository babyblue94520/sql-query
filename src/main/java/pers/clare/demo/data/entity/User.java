package pers.clare.demo.data.entity;

import lombok.*;

import javax.persistence.*;

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

    private String account;

    private String name;

    private String email;

    private Integer count;

    private Boolean locked;

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
