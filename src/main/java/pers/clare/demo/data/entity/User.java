package pers.clare.demo.data.entity;

import lombok.*;
import pers.clare.core.sqlquery.SQLStore;
import pers.clare.core.sqlquery.SQLStoreFactory;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final SQLStore Entity = SQLStoreFactory.build(User.class, true);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{manage.Account.NotBlank}")
    private String account;

    @Transient
    private String password;

    private String name;

    private String email;

    @NotNull(message = "{manage.Role.IdNotNull}")
    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "login_fail_count")
    private Integer loginFailCount;

    private boolean locked;

    private boolean enabled;

    @Column(name = "update_time")
    private Long updateTime;

    @Column(name = "update_user")
    private Long updateUser;

    @Column(name = "create_time")
    private Long createTime;

    @Column(name = "create_user")
    private Long createUser;
}
