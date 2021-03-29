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
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='使用者';

CREATE TABLE `access_log`
(
    `id`         bigint(19) NOT NULL AUTO_INCREMENT,
    `time`       bigint(13) NOT NULL DEFAULT '0' COMMENT '訪問時間',
    `service`    varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '服務名稱',
    `session_id` varchar(32) COLLATE utf8mb4_unicode_ci           DEFAULT '' COMMENT 'SessionID',
    `url`        varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '請求網址',
    `method`     varchar(10) COLLATE utf8mb4_unicode_ci  NOT NULL DEFAULT '' COMMENT '方法',
    `ip`         varchar(50) COLLATE utf8mb4_unicode_ci  NOT NULL DEFAULT '' COMMENT 'IP',
    `status`     int(10) NOT NULL DEFAULT '0' COMMENT '請求狀態',
    `ms`         int(10) NOT NULL DEFAULT '0' COMMENT '處理時間(ms)',
    PRIMARY KEY (`id`, `time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='訪問紀錄'
PARTITION BY RANGE (time)
(PARTITION pMin VALUES LESS THAN (1614528000000) COMMENT = 'time < 1614528000000;-- 1970-01-01 08:00:00.000~2021-02-28 23:59:59.999' ENGINE = InnoDB,
 PARTITION `2021-03` VALUES LESS THAN (1617206400000) COMMENT = 'time between 1614528000000 and 1617206399999;-- 2021-03-01 00:00:00.000~31 23:59:59.999' ENGINE = InnoDB,
 PARTITION `2021-04` VALUES LESS THAN (1619798400000) COMMENT = 'time between 1617206400000 and 1619798399999;-- 2021-04-01 00:00:00.000~30 23:59:59.999' ENGINE = InnoDB,
 PARTITION `2021-05` VALUES LESS THAN (1622476800000) COMMENT = 'time between 1619798400000 and 1622476799999;-- 2021-05-01 00:00:00.000~31 23:59:59.999' ENGINE = InnoDB,
 PARTITION `2021-06` VALUES LESS THAN (1625068800000) COMMENT = 'time between 1622476800000 and 1625068799999;-- 2021-06-01 00:00:00.000~30 23:59:59.999' ENGINE = InnoDB,
 PARTITION `2021-07` VALUES LESS THAN (1627747200000) COMMENT = 'time between 1625068800000 and 1627747199999;-- 2021-07-01 00:00:00.000~31 23:59:59.999' ENGINE = InnoDB,
 PARTITION `2021-08` VALUES LESS THAN (1630425600000) COMMENT = 'time between 1627747200000 and 1630425599999;-- 2021-08-01 00:00:00.000~31 23:59:59.999' ENGINE = InnoDB,
 PARTITION `2021-09` VALUES LESS THAN (1633017600000) COMMENT = 'time between 1630425600000 and 1633017599999;-- 2021-09-01 00:00:00.000~30 23:59:59.999' ENGINE = InnoDB,
 PARTITION `2021-10` VALUES LESS THAN (1635696000000) COMMENT = 'time between 1633017600000 and 1635695999999;-- 2021-10-01 00:00:00.000~31 23:59:59.999' ENGINE = InnoDB,
 PARTITION `2021-11` VALUES LESS THAN (1638288000000) COMMENT = 'time between 1635696000000 and 1638287999999;-- 2021-11-01 00:00:00.000~30 23:59:59.999' ENGINE = InnoDB,
 PARTITION `2021-12` VALUES LESS THAN (1640966400000) COMMENT = 'time between 1638288000000 and 1640966399999;-- 2021-12-01 00:00:00.000~31 23:59:59.999' ENGINE = InnoDB,
 PARTITION `2022-01` VALUES LESS THAN (1643644800000) COMMENT = 'time between 1640966400000 and 1643644799999;-- 2022-01-01 00:00:00.000~31 23:59:59.999' ENGINE = InnoDB,
 PARTITION pMax VALUES LESS THAN MAXVALUE COMMENT = 'time > 1643644799999;-- 2022-02-01 00:00:00.000~' ENGINE = InnoDB) ;