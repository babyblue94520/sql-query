package pers.clare.demo.data.entity;

import lombok.*;


import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccessLog {
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Id
    private Long time;

    private String ip;

    private String location;

    private String remark;

    @Lob
    @Column(name = "request_header", updatable = false)
    private String requestHeader;

    @Lob
    @Column(name = "request_parameter", insertable = false)
    private String requestParameter;
    @Lob
    @Column(name = "request_body")
    private String requestBody;

    @Lob
    @Column(name = "response_header")
    private String responseHeader;

    @Lob
    @Column(name = "response_content")
    private String responseContent;

    private String service;

    @Column(name = "session_id")
    private String sessionId;

    private Integer status;

    private String url;

    private String method;

    private String user;

    private Long ms;
}
