package pers.clare.demo.data.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(AccessLogPK.class)
public class AccessLog {
    @Id
    @GeneratedValue
    private Long id;

    @ApiModelProperty(value = "訪問時間")
    @Id
    private Long time;

    @ApiModelProperty(value = "請求網址")
    private String url;

    @ApiModelProperty(value = "方法")
    private String method;

    @ApiModelProperty(value = "IP")
    private String ip;

    @ApiModelProperty(value = "請求狀態")
    private Integer status;

    @ApiModelProperty(value = "處理時間(ms)")
    private Long ms;
}
