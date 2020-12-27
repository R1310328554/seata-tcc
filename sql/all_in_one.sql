# Account
DROP SCHEMA IF EXISTS db_account;
CREATE SCHEMA db_account;
USE db_account;


CREATE TABLE `undo_log` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`branch_id` bigint(20) NOT NULL,
`xid` varchar(100) NOT NULL,
`context` varchar(128) NOT NULL,
`rollback_info` longblob NOT NULL,
`log_status` int(11) NOT NULL,
`log_created` datetime NOT NULL,
`log_modified` datetime NOT NULL,
`ext` varchar(100) DEFAULT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

create table account(account_no varchar(256), amount DOUBLE,  freezed_amount DOUBLE, PRIMARY KEY (account_no));
insert into account(account_no, amount, freezed_amount) values('C', 100000000, 0); -- 初始化金额 一个亿！

CREATE TABLE `md` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `branch_id` bigint(20) NOT NULL,
  `xid` varchar(100) NOT NULL,
  `context` varchar(128) DEFAULT NULL,
  `rollback_info` longblob,
  `log_status` int(11) NOT NULL,
  `log_created` datetime DEFAULT NULL,
  `log_modified` datetime DEFAULT NULL,
  `ext` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1002 DEFAULT CHARSET=utf8;


# Order
DROP SCHEMA IF EXISTS db_order;
CREATE SCHEMA db_order;
USE db_order;


CREATE TABLE `undo_log` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`branch_id` bigint(20) NOT NULL,
`xid` varchar(100) NOT NULL,
`context` varchar(128) NOT NULL,
`rollback_info` longblob NOT NULL,
`log_status` int(11) NOT NULL,
`log_created` datetime NOT NULL,
`log_modified` datetime NOT NULL,
`ext` varchar(100) DEFAULT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
create table account(account_no varchar(256), amount DOUBLE,  freezed_amount DOUBLE, PRIMARY KEY (account_no));
insert into account(account_no, amount, freezed_amount) values('A', 100000000, 0); -- 初始化金额 一个亿！
insert into account(account_no, amount, freezed_amount) values('B', 100000000, 0); -- 初始化金额 一个亿！



CREATE TABLE `dtx_tcc_action` (
    `action_id` varchar(96) NOT NULL COMMENT '分支事务号',
    `action_name` varchar(64) DEFAULT NULL COMMENT '参与者名称',
    `tx_id` varchar(128) NOT NULL COMMENT '主事务号',
    `action_group` varchar(32) DEFAULT NULL COMMENT 'action group',
    `status` varchar(10) DEFAULT NULL COMMENT '状态',
    `param_data` varchar(4000) DEFAULT NULL COMMENT '一阶段方法参数数据',
    `gmt_create` datetime NOT NULL COMMENT '创建时间',
    `gmt_modified` datetime NOT NULL COMMENT '修改时间',
    `sharding_key` varchar(128) DEFAULT NULL COMMENT '分库分表字段',
    PRIMARY KEY (`action_id`) ,
    UNIQUE KEY  `idx_tx_id` (`tx_id`, `action_name`)
) ;

CREATE TABLE `md` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `branch_id` bigint(20) NOT NULL,
    `xid` varchar(100) NOT NULL,
    `context` varchar(128) DEFAULT NULL,
    `rollback_info` longblob,
    `log_status` int(11) NOT NULL,
    `log_created` datetime DEFAULT NULL,
    `log_modified` datetime DEFAULT NULL,
    `ext` varchar(100) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1002 DEFAULT CHARSET=utf8;


