CREATE TABLE `user`
(
    `id`          bigint(19) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `account`     varchar(20) COLLATE utf8mb4_unicode_ci  DEFAULT '' COMMENT '帳號',
    `name`        varchar(50) COLLATE utf8mb4_unicode_ci  DEFAULT '' COMMENT '名稱',
    `email`       varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '信箱',
    `count`       int(10) DEFAULT NULL,
    `locked`      bigint(13) DEFAULT '0' COMMENT '凍結',
    `enabled`     tinyint(1) DEFAULT '1' COMMENT '是否啟用(0:停用、1:啟用)',
    `update_user` bigint(19) DEFAULT '0' COMMENT '修改者',
    `update_time` bigint(13) DEFAULT '0' COMMENT '修改時間',
    `create_user` bigint(19) DEFAULT '0' COMMENT '建立者',
    `create_time` bigint(13) DEFAULT '0' COMMENT '建立時間',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8003 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='使用者';
