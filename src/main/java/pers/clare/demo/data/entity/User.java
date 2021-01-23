package pers.clare.demo.data.entity;

import lombok.*;

import javax.persistence.*;

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

    @Transient
    private String password;

    private String name;

    private String email;

    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "login_fail_count")
    private Integer loginFailCount;

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

    public User(Long id, String account, String name, String email, Integer roleId, Integer loginFailCount, Boolean locked, Boolean enabled, Long updateTime, Long updateUser, Long createTime, Long createUser) {
        this.id = id;
        this.account = account;
        this.name = name;
        this.email = email;
        this.roleId = roleId;
        this.loginFailCount = loginFailCount;
        this.locked = locked;
        this.enabled = enabled;
        this.updateTime = updateTime;
        this.updateUser = updateUser;
        this.createTime = createTime;
        this.createUser = createUser;
    }
}
