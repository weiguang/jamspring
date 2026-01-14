-- ============================================
-- MySQL 版本
-- ============================================
CREATE TABLE if not exists tb_test
(
    id           bigint         NOT NULL AUTO_INCREMENT,
    `key`        VARCHAR(255)            DEFAULT NULL,
    `value`      VARCHAR(1024)           DEFAULT NULL,
    `amt`        decimal(15, 2) NOT NULL DEFAULT 0,
    `status`     tinyint(1)     NOT NULL DEFAULT 1,
    `create_time` TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

