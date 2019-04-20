/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 50725
 Source Host           : localhost:3306
 Source Schema         : express1

 Target Server Type    : MySQL
 Target Server Version : 50725
 File Encoding         : 65001

 Date: 20/04/2019 15:54:01
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for order_desc
-- ----------------------------
DROP TABLE IF EXISTS `order_desc`;
CREATE TABLE `order_desc` (
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `courier_id` varchar(128) DEFAULT NULL COMMENT '代取人ID',
  `status` int(11) DEFAULT NULL COMMENT '订单状态',
  `has_delete` int(1) DEFAULT NULL,
  `courier_remark` varchar(255) DEFAULT NULL COMMENT '代取人备注',
  `create_date` datetime NOT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for order_info
-- ----------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
  `id` bigint(20) NOT NULL COMMENT '订单ID',
  `user_id` varchar(128) DEFAULT NULL COMMENT '用户ID',
  `odd` varchar(128) DEFAULT NULL COMMENT '快递单号',
  `company` varchar(128) DEFAULT NULL COMMENT '快递公司',
  `rec_address` varchar(255) DEFAULT NULL COMMENT '收货地址',
  `has_delete` int(1) DEFAULT NULL,
  `create_date` datetime NOT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for order_payment
-- ----------------------------
DROP TABLE IF EXISTS `order_payment`;
CREATE TABLE `order_payment` (
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `status` int(11) DEFAULT NULL COMMENT '支付状态',
  `type` int(11) DEFAULT NULL COMMENT '支付方式',
  `payment` decimal(10,0) DEFAULT NULL COMMENT '付款金额',
  `payment_id` varchar(255) DEFAULT NULL COMMENT '支付流水号',
  `online_seller` varchar(255) DEFAULT NULL COMMENT '收款方',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_date` datetime NOT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`order_id`) USING BTREE,
  KEY `fk_payment_type` (`type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='订单支付表';

-- ----------------------------
-- Table structure for persistent_logins
-- ----------------------------
DROP TABLE IF EXISTS `persistent_logins`;
CREATE TABLE `persistent_logins` (
  `username` varchar(64) NOT NULL,
  `series` varchar(64) NOT NULL,
  `token` varchar(64) NOT NULL,
  `last_used` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`series`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` varchar(128) NOT NULL COMMENT '用户ID',
  `username` varchar(64) DEFAULT NULL COMMENT '用户名',
  `password` varchar(128) DEFAULT NULL COMMENT '密码',
  `role_id` int(11) DEFAULT NULL COMMENT '角色ID',
  `id_card` varchar(64) DEFAULT NULL COMMENT '身份证号',
  `student_id_card` varchar(64) DEFAULT NULL COMMENT '学生证号',
  `tel` varchar(64) DEFAULT NULL COMMENT '手机号',
  `star` varchar(64) DEFAULT '0' COMMENT '评级',
  `third_login_type` int(11) DEFAULT '-1' COMMENT '三方登陆类型（-1：未绑定三方登陆）',
  `third_login_id` varchar(128) DEFAULT NULL COMMENT '三方登陆ID',
  `has_delete` int(1) DEFAULT '0' COMMENT '是否删除（0：未删除；1删除；）',
  `create_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO `sys_user` VALUES ('1', 'test', '$2a$10$lqib8LGGEziYYaJMmnQ4XubOugCjECjtLHb4yJLgZ.0wDwSjh09Yi', 1, NULL, NULL, NULL, NULL, -1, NULL, 0, '2019-04-17 23:10:21', NULL);
INSERT INTO `sys_user` VALUES ('6150146f23bfa506b300f4f2c635dcba', NULL, NULL, 3, NULL, NULL, NULL, '0', 1, '830B7B4639CFB1B4FD0018CC810B8EF6', 0, '2019-04-20 15:31:50', NULL);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
